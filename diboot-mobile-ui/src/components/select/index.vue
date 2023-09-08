<script lang="ts">
import { Picker } from 'vant'
export default {
  extends: Picker,
  props: {
    multiple: Boolean,
    value: [String, Array<String>],
    remoteMethod: Function
  }
}
</script>

<script setup lang="ts">
import Column from '@/components/select/Column.vue'
import type { PickerColumn, PickerOption } from 'vant/es/picker/types'
import { tree2List } from '@/utils/list'
import type { PickerFieldNames } from 'vant/es/picker/types'
import PickerToolbar from 'vant/es/picker/PickerToolbar'
import { nextTick } from 'vue'
import { useParent } from '@vant/use'
import { PICKER_GROUP_KEY } from 'vant/es/picker-group/PickerGroup'
import { useExpose } from 'vant/es/composables/use-expose'
import type { PickerExpose } from 'vant/es/picker/types'
import type { SelectedValue } from '@/components/di/type'

const { parent } = useParent(PICKER_GROUP_KEY)

const checked = ref()

const currentInstance = getCurrentInstance()
const props = currentInstance?.props as {
  value?: string
  showToolbar?: boolean
  columns?: PickerColumn[]
  columnsFieldNames?: PickerFieldNames
  remoteMethod: (val: string) => undefined
}
const emit = currentInstance?.emit as {
  (e: 'update:modelValue', value?: string): void
  (e: 'change', value: SelectedValue): void
  (e: 'cancel', value: SelectedValue): void
  (e: 'change', value: SelectedValue): void
  (e: 'confirm', value: SelectedValue): void
}

watch(
  () => props?.value,
  value => (checked.value = value),
  { immediate: true }
)

const dataList = ref<PickerColumn[]>([])
const notTree = ref(false)

watch(
  () => props?.columns ?? [],
  options => {
    const list = tree2List(options)
    if ((notTree.value = list.length === options.length)) dataList.value.unshift(...list)
    else dataList.value = list
  },
  { immediate: true }
)

const getEventParams = (): SelectedValue => {
  const values = Array.isArray(checked.value) ? checked.value : checked.value?.length ? [checked.value] : []
  const valueKey = props?.columnsFieldNames?.value ?? 'value'
  return {
    selectedValues: values,
    selectedOptions: dataList.value
      .filter((e: PickerOption) => values?.includes(e[valueKey]))
      .map(e => e as any as LabelValue)
  }
}

const onCancel = () => emit('cancel', getEventParams())

const onConfirm = () => {
  const params = getEventParams()
  nextTick(() => {
    emit('confirm', params)
  })
  return params
}

useExpose<PickerExpose>({ confirm: onConfirm, getSelectedOptions: () => getEventParams().selectedOptions })

watch(checked, () => emit('change', getEventParams()), { deep: true })

const keyword = ref<string>()

const keywordChange = (value: string) => props.remoteMethod?.(value)
</script>

<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <PickerToolbar v-if="props.showToolbar && !parent" v-bind="props" @cancel="onCancel" @confirm="onConfirm" />

    <van-search v-show="notTree" v-model="keyword" placeholder="输入关键字" @update:model-value="keywordChange" />

    <div style="flex: 1; overflow-y: auto">
      <van-checkbox-group v-model="checked" v-if="multiple">
        <template v-for="item in props.columns" :key="item">
          <Column
            v-show="!keyword || `${item[(props.columnsFieldNames?.text ?? 'text') as keyof typeof item]}`.includes(keyword as string)"
            :column="item"
            :columns-field-names="props.columnsFieldNames"
            square
          />
        </template>
      </van-checkbox-group>

      <van-radio-group v-model="checked" v-else>
        <template v-for="item in props.columns" :key="item">
          <Column
            v-show="!keyword || `${item[(props.columnsFieldNames?.text ?? 'text') as keyof typeof item]}`.includes(keyword as string)"
            :column="item"
            :columns-field-names="props.columnsFieldNames"
          />
        </template>
      </van-radio-group>

      <template
        v-if="
          !props.columns?.length ||
          !props.columns?.filter(e => !keyword || `${e[(props.columnsFieldNames?.text ?? 'text') as keyof typeof e]}`.includes(keyword as string))?.length
        "
      >
        <van-empty v-if="remoteMethod && !keyword" image="search" description="搜索选项" />
        <van-empty v-else description="无选项" />
      </template>
    </div>
  </div>
</template>

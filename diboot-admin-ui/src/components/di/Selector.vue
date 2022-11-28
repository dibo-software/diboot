<script setup lang="ts" name="DiListSelector">
import type { Input, ListConfig, ListSelector, TreeConfig } from '@/components/di/type'
import type { RelatedData } from '@/hooks/use-option'

type ModelValue = string | string[]

interface ListSelectorProps extends Omit<ListSelector, keyof Omit<Input, 'placeholder'> | 'type'> {
  modelValue?: ModelValue
  placeholder?: string

  // vue语法限制导致只能在当前文件中再次定义
  // https://cn.vuejs.org/guide/typescript/composition-api.html#typing-component-props
  tree?: Omit<TreeConfig, 'sortApi'>
  list?: Omit<ListConfig, 'operation'>
  dataType: string
  dataLabel?: string
  multiple?: boolean
}

const props = defineProps<ListSelectorProps>()

const config = reactive({ tree: props.tree, list: props.list })

// if (!config.list) {
//
// }

provide('multiple', !!props.multiple)

const selectedKeys = ref<string | string[] | undefined>(props.modelValue)
const selectedDataList = ref<LabelValue[]>([])

const selectedRows = ref<LabelValue[]>([])
provide('selected-rows', selectedRows)
const dataLabel = props.dataLabel ?? (config.list?.columns[0] ?? { prop: 'label' })?.prop
provide('data-label', dataLabel)

const selected: RelatedData = reactive({ type: props.dataType, label: dataLabel })

const { initRelatedData, relatedData } = useOption({ load: { selected } })

watch(
  () => props.modelValue,
  value => {
    if (value && value.length) {
      nextTick(() => {
        const ids = selectedDataList.value.map(e => e.value).sort()
        if (ids.toString() !== (Array.isArray(value) ? value : [value]).sort().toString()) {
          selected.condition = { [config.list?.primaryKey || 'id']: value }
          initRelatedData().then(() => {
            selectedRows.value = relatedData.selected
            selectedDataList.value = relatedData.selected
          })
        }
      })
    } else {
      selectedRows.value.length = 0
      selectedDataList.value.length = 0
    }
  },
  { immediate: true }
)

const confirm = () => {
  selectedDataList.value = selectedRows.value
  if (!selectedDataList.value?.length) {
    selectedKeys.value = props.multiple ? [] : undefined
  } else if (props.multiple) {
    selectedKeys.value = selectedDataList.value.map(e => `${e.value}`)
  } else {
    selectedKeys.value = selectedDataList.value[0].value
  }
  visible.value = false
}

const cancel = () => {
  visible.value = false
  selectedRows.value = selectedDataList.value
}

const removeTag = (val: string) => {
  const index = (selectedKeys.value as string[]).indexOf(val)
  selectedRows.value?.splice(index, 1)
  selectedDataList.value?.splice(index, 1)
}

const clear = () => {
  selectedKeys.value = props.multiple ? [] : undefined
  selectedRows.value = []
  selectedDataList.value = []
}

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue?: ModelValue): void
  (e: 'change', modelValue?: ModelValue): void
}>()

watch(
  selectedKeys,
  value => {
    emit('update:modelValue', value)
    emit('change', value)
  },
  { deep: true }
)

// 选择器显隐
const visible = ref(false)
const open = (val: boolean) => {
  if (val) visible.value = true
}

const parent = ref<string>()
const clickNode = (id?: string) => (parent.value = id)
</script>

<template>
  <el-select
    :model-value="selectedKeys"
    clearable
    :multiple="multiple"
    popper-class="hide"
    :placeholder="placeholder"
    @remove-tag="removeTag"
    @clear="clear"
    @visible-change="open"
  >
    <el-option v-for="item in selectedDataList" :key="item.value" :value="item.value" :label="item.label" />
  </el-select>

  <el-dialog v-model="visible" top="3vh" :width="config.tree ? '75%' : ''" append-to-body>
    <template #header>
      <div style="display: flex">
        <strong style="margin: 0 8px; zoom: 1.1">选择</strong>
        <el-space
          wrap
          :style="config.tree ? { width: 'calc(100% - 260px)', marginLeft: '200px' } : { width: 'calc(100% - 60px)' }"
        >
          <el-tag v-for="(item, index) in selectedRows" :key="index" closable @close="removeTag(item.value)">
            {{ item.label }}
          </el-tag>
        </el-space>
      </div>
    </template>
    <div class="body-container">
      <di-tree v-if="config.tree" v-bind="config.tree" :sort-api="undefined" @click-node="clickNode" />

      <di-list v-bind="config.list" :operation="undefined" :parent="parent" style="width: 0" />

      <div class="bottom-operation">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="confirm">确认</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<style scoped lang="scss">
.body-container {
  height: 80vh;
  display: flex;
  position: relative;
  margin-top: -39px;
  margin-bottom: -20px;

  .bottom-operation {
    position: absolute;
    bottom: 0;
    right: 0;
  }
}
</style>

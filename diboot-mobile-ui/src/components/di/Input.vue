<script setup lang="ts" name="DiInput">
import type { FormItem, InputText, ListSelector, Select as SelectConfig, SelectedValue, Upload } from './type'
import type { FieldRule } from 'vant'
import Select from '../select/index.vue'
import ScanCode from '../scan-code/index.vue'
import { findLabel } from '@/components/select/optionsUtil'
import { checkValue } from '@/utils/validate-form'
import i18n from '@/i18n'

const props = defineProps<{
  config: FormItem
  modelValue?: unknown
  disabled?: boolean
  baseApi?: string
  getId?: () => string | undefined
  relatedDatas?: LabelValue[]
  fileList?: FileRecord[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value?: unknown): void
  (e: 'change', value?: unknown): void
  (e: 'remoteFilter', value?: string): void
}>()

const underDesign = inject('under-design', false)

const instance = getCurrentInstance()

const value = ref<any>(
  underDesign
    ? undefined
    : props.modelValue && ['input-number', 'boolean'].includes(props.config.type)
      ? JSON.parse(`${props.modelValue}`)
      : props.modelValue
)
watch(
  value,
  value => {
    handleChange(value)
    emit('update:modelValue', value)
    instance?.proxy?.$forceUpdate()
  },
  { deep: true }
)
watch(
  () => props.modelValue,
  val => {
    value.value = val && ['input-number', 'boolean'].includes(props.config.type) ? JSON.parse(`${val}`) : val
    if (!val) fileList.value.length = 0
  }
)

const requiredRule = {
  required: true,
  message: i18n.global.t('rules.notnull'),
  ...(['input-number', 'checkbox', 'boolean'].includes(props.config.type) || props.config['multiple' as keyof FormItem]
    ? {}
    : { whitespace: true })
}

const checkUniqueRule = {
  validator: checkValue(
    `${props.baseApi}/check-unique`,
    'value',
    () => (props.getId ? props.getId() : undefined),
    () => ({ field: props.config.prop })
  ),
  trigger: 'blur'
}

const rules = (
  (props.config.required && props.config.unique
    ? [requiredRule, checkUniqueRule]
    : props.config.required
      ? [requiredRule]
      : props.config.unique
        ? [checkUniqueRule]
        : []) as FieldRule[]
).concat(...(props.config.rules ?? []))

const handleChange = (value?: unknown) => emit('change', value)

const remoteFilter = _.debounce((value?: unknown) => emit('remoteFilter', value as string | undefined), 300)

const showPicker = ref(false)

const onClick = () => (showPicker.value = !(props.config.disabled || props.disabled))

const options = ref()

const label = computed(() => findLabel(options.value ?? props.relatedDatas, value.value))

const onConfirmSelect = ({ selectedValues, selectedOptions }: SelectedValue) => {
  showPicker.value = false
  value.value = (props.config as ListSelector).multiple ? selectedValues : selectedValues[0]
  options.value = selectedOptions
  handleChange(value.value)
}

const datetimeArray: any = ref([])
const date = new Date()
const complete = (num: number) => (num <= 9 ? `0${num}` : `${num}`)
datetimeArray.value.push(`${date.getFullYear()}`)
datetimeArray.value.push(complete(date.getMonth() + 1))
datetimeArray.value.push(complete(date.getDate()))
datetimeArray.value.push(complete(date.getHours()))
datetimeArray.value.push(complete(date.getMinutes()))
datetimeArray.value.push(complete(date.getSeconds()))
const getInitDateTime = () => {
  switch (props.config.type) {
    case 'year':
      return datetimeArray.value.slice(0, 1)
    case 'month':
      return datetimeArray.value.slice(0, 2)
    case 'date':
      return datetimeArray.value.slice(0, 3)
    case 'datetime':
      return toRaw(datetimeArray.value)
    case 'time':
      return datetimeArray.value.slice(3, 5)
    default:
      return undefined
  }
}

const dateTime = ref<string[]>(getInitDateTime())

watch(
  value,
  val => {
    if (!val) return
    const split = `${val}`.split(/[ \-:]/)
    switch (props.config.type) {
      case 'year':
        return dateTime.value.splice(0, 1, ...split)
      case 'month':
        return dateTime.value.splice(0, 2, ...split)
      case 'date':
        return dateTime.value.splice(0, 3, ...split)
      case 'datetime':
        return (dateTime.value = split)
      case 'time':
        return dateTime.value.splice(3, 5, ...split)
    }
  },
  { immediate: true }
)

const datePicker = computed({ get: () => dateTime.value.slice(0, 3), set: val => dateTime.value.splice(0, 3, ...val) })
const timePicker = computed({ get: () => dateTime.value.slice(3, 5), set: val => dateTime.value.splice(3, 5, ...val) })

const onConfirmDatePicker = ({ selectedValues }: SelectedValue) => {
  showPicker.value = false
  value.value = selectedValues?.join('-')
  handleChange(value.value)
}

const onConfirmDateTimePicker = (list: SelectedValue[]) => {
  showPicker.value = false
  value.value = list[0].selectedValues?.join('-') + ' ' + list[1].selectedValues?.join(':')
  handleChange(value.value)
}

const onConfirmTimePicker = ({ selectedValues }: SelectedValue) => {
  showPicker.value = false
  value.value = selectedValues?.join(':')
  handleChange(value.value)
}

const { fileList, uploadFileHandle, onRemove } = useUploadFile(
  fileIds => (value.value = fileIds),
  () => props.fileList
)

const onOversize = () => showFailToast(i18n.global.t('di.input.fileLarge', [(props.config as Upload).size]))

const checkFileHandle = (file: File | File[]) => {
  // 文件校验
  Array.isArray(file) || file.type
  return true
}
</script>

<template>
  <van-field
    v-if="['input', 'textarea'].includes(config.type)"
    v-model="value"
    :name="config.prop"
    :label="config.label"
    :required="config.required"
    clearable
    :type="config.type !== 'input' ? 'textarea' : undefined"
    :autosize="config.type !== 'input'"
    :rows="config.type === 'textarea' ? 2 : 1"
    :disabled="config.disabled || disabled"
    :maxlength="(config as InputText).maxlength"
    :show-word-limit="!!(config as InputText).maxlength"
    :placeholder="config.placeholder"
    :rules="rules"
    @update:model-value="handleChange"
  >
    <template #button v-if="config.type === 'input' && config.scanCode">
      <scan-code @change="val => (value = val)" />
    </template>
  </van-field>
  <template v-else-if="config.type === 'rich'">
    <van-field :name="config.prop" :label="config.label" :required="config.required" :rules="rules">
      <template #input />
    </van-field>
    <rich-read
      v-if="config.disabled || disabled"
      :value="`${value ?? ''}`"
      :style="{ flex: 1, height: config.height }"
    />
    <rich-editor
      v-else
      v-model="value"
      :placeholder="config.placeholder"
      :mode="config.mode"
      :style="{ height: config.height }"
    />
  </template>
  <van-field
    v-if="config.type === 'input-number'"
    v-model="value"
    type="number"
    :name="config.prop"
    :label="config.label"
    :required="config.required"
    :rules="rules"
    :min="config.min"
    :max="config.max"
    :precision="config.precision"
    :placeholder="config.placeholder"
    :disabled="config.disabled || disabled"
  />
  <van-field v-if="config.type === 'boolean'" :name="config.prop" :label="config.label">
    <template #input>
      <van-switch v-model="value" :disabled="config.disabled || disabled" />
    </template>
  </van-field>

  <template v-if="['cascader', 'tree-select', 'select', 'list-selector'].includes(config.type)">
    <van-field
      v-model="label"
      is-link
      readonly
      :name="config.prop"
      :label="config.label"
      :rules="rules"
      :required="config.required"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      @click="onClick"
    />
    <van-popup v-model:show="showPicker" round position="bottom" :style="{ height: '60%' }">
      <Select
        :value="value"
        :multiple="(config as SelectConfig).multiple"
        :columns-field-names="{ text: 'label', value: 'value', children: 'children' }"
        :columns="relatedDatas"
        :remote-method="((config as SelectConfig).remote ? remoteFilter : undefined) as Function"
        @cancel="showPicker = false"
        @confirm="onConfirmSelect"
      />
    </van-popup>
  </template>

  <van-field
    v-if="['checkbox', 'radio'].includes(config.type)"
    :name="config.prop"
    :label="config.label"
    :required="config.required"
    :rules="rules"
  >
    <template #input>
      <van-checkbox-group
        v-if="config.type === 'checkbox'"
        v-model="value"
        direction="horizontal"
        :disabled="config.disabled || disabled"
      >
        <van-space wrap>
          <van-checkbox v-for="(item, index) in relatedDatas" :key="index" :name="item.value" shape="square">
            {{ item.label }}
          </van-checkbox>
        </van-space>
      </van-checkbox-group>
      <van-radio-group
        v-if="config.type === 'radio'"
        v-model="value"
        direction="horizontal"
        :disabled="config.disabled || disabled"
      >
        <van-space wrap>
          <van-radio v-for="(item, index) in relatedDatas" :key="index" :name="item.value">{{ item.label }}</van-radio>
        </van-space>
      </van-radio-group>
    </template>
  </van-field>

  <template v-if="config.type === 'year' || config.type === 'month' || config.type === 'date'">
    <van-field
      v-model="value"
      is-link
      readonly
      :name="config.prop"
      :label="config.label"
      :required="config.required"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      :rules="rules"
      @click="onClick"
    />
    <van-popup v-model:show="showPicker" position="bottom" :style="{ height: '60%' }">
      <van-date-picker
        v-model="datePicker"
        :columns-type="
          config.type === 'year' ? ['year'] : config.type === 'month' ? ['year', 'month'] : ['year', 'month', 'day']
        "
        @confirm="onConfirmDatePicker"
        @cancel="showPicker = false"
      />
    </van-popup>
  </template>
  <template v-if="config.type === 'datetime'">
    <van-field
      v-model="value"
      is-link
      readonly
      :name="config.prop"
      :label="config.label"
      :required="config.required"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      :rules="rules"
      @click="onClick"
    />
    <van-popup v-model:show="showPicker" position="bottom" :style="{ height: '60%' }">
      <van-picker-group
        :tabs="[$t('di.input.date'), $t('di.input.time')]"
        :next-step-text="$t('di.input.next')"
        @confirm="onConfirmDateTimePicker"
        @cancel="showPicker = false"
      >
        <van-date-picker v-model="datePicker" />
        <van-time-picker v-model="timePicker" :columns-type="['hour', 'minute', 'second']" />
      </van-picker-group>
    </van-popup>
  </template>
  <template v-if="config.type === 'time'">
    <van-field
      v-model="value"
      is-link
      readonly
      :name="config.prop"
      :label="config.label"
      :required="config.required"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      :rules="rules"
      @click="onClick"
    />
    <van-popup v-model:show="showPicker" position="bottom" :style="{ height: '60%' }">
      <van-time-picker v-model="timePicker" @confirm="onConfirmTimePicker" @cancel="showPicker = false" />
    </van-popup>
  </template>

  <van-field v-if="config.type === 'upload'" :name="config.prop" :label="config.label" :required="config.required">
    <template #input>
      <van-uploader
        v-model="fileList"
        :disabled="config.disabled || disabled"
        :accept="config.accept ?? ''"
        :max-size="(config.size ? 1024 * 1024 * config.size : undefined) as number"
        :max-count="config.limit"
        :before-read="checkFileHandle"
        :after-read="uploadFileHandle"
        @oversize="onOversize"
        @delete="onRemove"
      >
        <template #default v-if="config.listType !== 'picture-card'">
          <van-button icon="plus" size="small" type="primary">{{ $t('di.input.uploadFile') }}</van-button>
        </template>
      </van-uploader>
    </template>
  </van-field>
</template>

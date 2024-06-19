<script setup lang="ts" name="DiInput">
import { Plus, Upload as UploadIcon } from '@element-plus/icons-vue'
import type { FormItem, Select, Upload } from './type'
import type { UploadRawFile, UploadFile, FormItemRule, CascaderNode, CascaderOption } from 'element-plus'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()

const props = withDefaults(
  defineProps<{
    config: FormItem
    modelValue?: unknown
    disabled?: boolean
    baseApi?: string
    getId?: () => string | undefined
    relatedDatas?: LabelValue<string | never>[]
    lazyLoading?: boolean
    fileList?: FileRecord[]
    lazyLoad?: (parentId: string) => Promise<LabelValue[]>
    teleported?: boolean
    formPropPrefix?: string
  }>(),
  {
    modelValue: undefined,
    baseApi: undefined,
    getId: undefined,
    relatedDatas: undefined,
    fileList: undefined,
    lazyLoad: undefined,
    teleported: true,
    formPropPrefix: ''
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value?: unknown): void
  (e: 'change', value?: unknown): void
  (e: 'remoteFilter', value?: string): void
  (e: 'preview', accessUrl: string, isImage: boolean): void
}>()

const instance = getCurrentInstance()

const value = ref(
  props.config.type === 'input-number' && props.modelValue
    ? Number(`${props.modelValue}`)
    : props.config.type === 'boolean' && props.modelValue
      ? Boolean(`${props.modelValue}`)
      : props.modelValue
)
watch(
  value,
  value => {
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
  message: i18n.t('rules.notnull'),
  ...(['input-number', 'checkbox', 'boolean'].includes(props.config.type) || (props.config as Select).multiple
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
        : []) as FormItemRule[]
).concat(...(props.config.rules ?? []))

const handleChange = (value?: unknown) => emit('change', value)

const remoteFilter = (value?: unknown) => emit('remoteFilter', value as string | undefined)

const lazyLoad = ({ data }: CascaderNode, resolve: (data: CascaderOption[]) => void) =>
  props.lazyLoad ? props.lazyLoad(data!.value as string).then(list => resolve(list as CascaderOption[])) : resolve([])

const DEFAULT_DATE_FORMAT: Record<string, string> = { date: 'YYYY-MM-DD', datetime: 'YYYY-MM-DD HH:mm:ss' }
const getDateFormtDef = (type: string) => DEFAULT_DATE_FORMAT[type]

const { action, httpRequest, fileList, onSuccess, onRemove } = useUploadFile(
  fileIds => (value.value = fileIds),
  () => props.fileList
)

const previewFile = (file: UploadFile) =>
  emit(
    'preview',
    file.accessUrl as string,
    ['picture-card', 'picture'].includes((props.config as Upload).listType as string)
  )

const beforeUpload = (rawFile: UploadRawFile) => {
  const fileConfig: Upload = props.config as any
  const accept = convert2accept(fileConfig?.accept)
  if (accept && !accept.split(',').includes(rawFile.name.substring(rawFile.name.lastIndexOf('.')))) {
    ElMessage.error(i18n.t('components.di.input.uploadFormatError', [accept.replace(/,/g, '/')]))
    return false
  }
  if (fileConfig.size && rawFile.size / 1024 / 1024 > fileConfig.size) {
    ElMessage.error(i18n.t('components.di.input.fileLarge', [fileConfig.size]))
    return false
  }
  return true
}
// 获取可用的accept列表
const convert2accept = (accept?: string) => {
  if (!accept) {
    return undefined
  }
  return accept
    .split(',')
    .map((item: string) => {
      if (item.indexOf('.') !== 0) {
        return `.${item}`
      } else {
        return item
      }
    })
    .join(',')
}
</script>

<template>
  <!-- @ts-nocheck -->
  <el-form-item :prop="`${formPropPrefix}${config.prop}`" :label="config.label" :rules="rules">
    <el-input
      v-if="config.type === 'input'"
      :model-value="value as string"
      :placeholder="config.placeholder ?? $t('placeholder.input')"
      clearable
      :maxlength="config.maxlength as number"
      :show-word-limit="!!config.maxlength"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <el-input
      v-else-if="config.type === 'textarea'"
      :model-value="value as string"
      :placeholder="config.placeholder"
      clearable
      type="textarea"
      :autosize="config.autosize as boolean"
      :maxlength="config.maxlength as number"
      :show-word-limit="!!config.maxlength"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <template v-else-if="config.type === 'rich'">
      <rich-read
        v-if="config.disabled || disabled"
        :value="`${value ?? ''}`"
        :style="{ flex: 1, height: config.height }"
      />
      <rich-editor
        v-else
        :model-value="value as string"
        :placeholder="config.placeholder"
        :mode="config.mode"
        :style="{ height: config.height }"
        @update:model-value="value = $event"
      />
    </template>
    <el-input-number
      v-if="config.type === 'input-number'"
      :model-value="value as number"
      :placeholder="config.placeholder"
      :min="config.min"
      :max="config.max"
      :precision="config.precision"
      :step-strictly="!config.precision"
      :controls="config.controls === false ? false : undefined"
      :controls-position="config.controls === 'right' ? 'right' : ''"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <number-range
      v-if="config.type === 'input-number-range'"
      :model-value="value as [number, number]"
      :min="config.min"
      :max="config.max"
      :precision="config.precision"
      :controls="config.controls"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <template v-if="config.type === 'boolean'">
      <el-switch
        v-if="config.mode === 'switch'"
        :model-value="value as boolean"
        :disabled="config.disabled || disabled"
        @update:model-value="value = $event"
      />
      <el-select
        v-else
        :model-value="value as boolean"
        clearable
        :disabled="config.disabled || disabled"
        @update:model-value="value = $event"
      >
        <el-option :label="$t('bool.yes')" :value="true" />
        <el-option :label="$t('bool.no')" :value="false" />
      </el-select>
    </template>
    <el-select
      v-if="config.type === 'select'"
      :model-value="value as string[]"
      clearable
      filterable
      :placeholder="config.placeholder"
      :multiple="config.multiple"
      :remote="config.remote"
      :remote-method="config.remote ? remoteFilter : undefined"
      :loading="lazyLoading"
      :disabled="config.disabled || disabled"
      :teleported="teleported"
      @change="handleChange"
      @update:model-value="value = $event"
    >
      <el-option v-for="item in relatedDatas" :key="item.value" v-bind="item">
        <div v-if="typeof item.ext === 'string'" class="option">
          {{ item.label }}
          <span class="ext">（{{ item.ext }}）</span>
        </div>
      </el-option>
    </el-select>
    <el-cascader
      v-if="config.type === 'cascader'"
      :model-value="value as string[]"
      :placeholder="config.placeholder"
      clearable
      filterable
      :teleported="teleported"
      :options="relatedDatas as CascaderOption[]"
      :props="{
        emitPath: false,
        lazy: config.lazy,
        lazyLoad: config.lazy ? lazyLoad : undefined,
        multiple: config.multiple,
        checkStrictly: config.checkStrictly
      }"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <el-tree-select
      v-if="config.type === 'tree-select'"
      v-model="value"
      :placeholder="config.placeholder"
      :data="relatedDatas"
      :filterable="!config.lazy"
      :lazy="config.lazy"
      :load="config.lazy ? lazyLoad : undefined"
      :check-strictly="config.checkStrictly"
      :default-expand-all="!config.lazy"
      :multiple="config.multiple"
      :disabled="config.disabled || disabled"
      :teleported="teleported"
      clearable
      @change="handleChange"
    />
    <el-checkbox-group
      v-if="config.type === 'checkbox'"
      :model-value="value as string[]"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    >
      <el-checkbox v-for="(item, index) in relatedDatas" :key="index" :label="item.value">{{ item.label }}</el-checkbox>
    </el-checkbox-group>
    <el-radio-group
      v-if="config.type === 'radio'"
      :model-value="value as string"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    >
      <el-radio v-for="(item, index) in relatedDatas" :key="index" :label="item.value">{{ item.label }}</el-radio>
    </el-radio-group>
    <di-selector
      v-if="config.type === 'list-selector'"
      :model-value="value as string[]"
      :tree="config.tree"
      :list="config.list"
      :multiple="config.multiple"
      :data-type="config.dataType"
      :data-label="config.dataLabel"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <el-date-picker
      v-if="
        config.type === 'year' ||
        config.type === 'month' ||
        config.type === 'date' ||
        config.type === 'datetime' ||
        config.type === 'week'
      "
      :model-value="value as string"
      clearable
      :type="config.type"
      :value-format="config.format ? config.format : getDateFormtDef(config.type)"
      :placeholder="config.placeholder"
      :disabled="config.disabled || disabled"
      :teleported="teleported"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <el-time-select
      v-if="config.type === 'time'"
      :model-value="value as string"
      clearable
      :placeholder="config.placeholder"
      start="00:00"
      step="00:15"
      end="23:59"
      :disabled="config.disabled || disabled"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <date-range
      v-if="['daterange', 'datetimerange'].includes(config.type)"
      :model-value="value as [string, string]"
      :type="config.type as 'datetimerange'"
      :teleported="teleported"
      @change="handleChange"
      @update:model-value="value = $event"
    />
    <el-upload
      v-if="config.type === 'upload'"
      v-model:file-list="fileList"
      :action="action"
      :http-request="httpRequest"
      :on-success="onSuccess"
      :on-remove="onRemove"
      :limit="config.limit"
      :accept="config.accept"
      :list-type="config.listType ?? 'text'"
      :multiple="(config.limit ?? 2) > 1"
      :before-upload="beforeUpload"
      :on-preview="previewFile"
      :disabled="config.disabled || disabled"
      :class="{
        'upload-plus-hide': fileList.length >= (config.limit ?? Number.MAX_VALUE),
        'upload-plus-disabled': config.disabled || disabled
      }"
      style="width: 100%"
    >
      <el-icon v-if="config.listType === 'picture-card'">
        <Plus />
      </el-icon>
      <el-button
        v-else-if="!(fileList.length >= (config.limit ?? Number.MAX_VALUE))"
        :icon="UploadIcon"
        :disabled="config.disabled || disabled"
      >
        {{ $t('components.di.input.uploadFile') }}
      </el-button>
      <template #tip>
        <el-button
          v-if="config.listType !== 'picture-card' && fileList.length >= (config.limit ?? Number.MAX_VALUE)"
          :icon="UploadIcon"
          disabled
        >
          {{ $t('components.di.input.uploadFile') }}
        </el-button>
        <div v-if="config.placeholder" class="el-upload__tip">
          {{ config.placeholder }}
        </div>
        <div v-else-if="config.limit || config.accept || config.size" class="el-upload__tip">
          {{ $t('components.di.input.limit.prefix')
          }}<span v-if="config.limit"
            >{{ ' ' }}{{ config.limit }} 个<span v-if="!config.accept">{{
              $t('components.di.input.limit.suffix')
            }}</span></span
          >
          <span v-if="config.accept">{{ $t('components.di.input.accept', [config.accept.replace(/,/g, '/')]) }}</span>
          <span v-if="config.size && (config.limit || config.accept)">，</span>
          <span v-if="config.size">{{ $t('components.di.input.size') }} {{ config.size }} MB</span>。
        </div>
      </template>
    </el-upload>
  </el-form-item>
</template>

<style scoped>
.option {
  display: flex;
  justify-content: space-between;

  .ext {
    font-size: var(--el-font-size-extra-small);
    color: var(--el-text-color-secondary);
  }
}

.upload-plus-hide :deep(.el-upload--picture-card) {
  display: none;
}

.upload-plus-disabled :deep(.el-upload--picture-card) {
  cursor: not-allowed;
}
</style>

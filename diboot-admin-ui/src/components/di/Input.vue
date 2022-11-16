<script setup lang="ts" name="DiInput">
import { Plus } from '@element-plus/icons-vue'
import type { FormItem, Upload } from './type'
import type { FormItemRule } from 'element-plus/es/tokens/form'
import type { UploadRawFile } from 'element-plus'

const props = defineProps<{
  config: FormItem
  modelValue?: unknown
  baseApi?: string
  getId?: () => string | undefined
  relatedDatas?: LabelValue[]
  lazyLoading?: boolean
  getFileList?: () => FileRecord[] | undefined
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value?: unknown): void
  (e: 'change', value?: unknown): void
  (e: 'remoteFilter', value?: unknown): void
  (e: 'lazyLoad', parentId: string): LabelValue[]
}>()

const instance = getCurrentInstance()

const value = ref(props.modelValue)
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
  val => (value.value = val)
)

const requiredRule = { required: true, message: '不能为空', whitespace: true }
const checkUniqueRule = {
  validator: (rule: unknown, value: unknown, callback: (error?: string | Error) => void) => {
    if (value) {
      const params = {
        id: props.getId ? props.getId() : undefined,
        field: props.config.prop,
        [props.config.prop]: value
      }
      api
        .get(`${props.baseApi}/check-unique`, params)
        .then(() => {
          callback()
        })
        .catch(err => {
          callback(err.msg || err)
        })
    }
  },
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

const remoteFilter = (value?: unknown) => emit('remoteFilter', value)

const lazyLoad = ({ data }: { data: LabelValue }, resolve: (data: LabelValue[]) => void) =>
  resolve(emit('lazyLoad', data.value))

const bindUpload = useUploadFile(fileIds => (value.value = fileIds), props.getFileList ?? (() => []))

const beforeUpload = (rawFile: UploadRawFile) => {
  const fileConfig: Upload = props.config as any
  if (
    fileConfig.accept &&
    !fileConfig.accept.split(',').includes(rawFile.name.substring(rawFile.name.lastIndexOf('.') + 1))
  ) {
    ElMessage.error(`请上传${fileConfig.accept.replace(/,/, '/')}格式的文件！`)
    return false
  }
  if (fileConfig.size && rawFile.size / 1024 / 1024 > fileConfig.size) {
    ElMessage.error(`文件过大，超出了限制${fileConfig.size}MB，请调整您的文件!`)
    return false
  }
  return true
}
</script>

<template>
  <el-form-item :prop="config.prop" :label="config.label" :rules="rules">
    <el-input
      v-if="config.type === 'input'"
      v-model="value"
      :placeholder="config.placeholder"
      clearable
      @change="handleChange"
    />
    <el-input-number
      v-if="config.type === 'input-number'"
      v-model="value"
      :placeholder="config.placeholder"
      :min="config.min"
      :max="config.max"
      :step-strictly="config.stepStrictly"
      :controls="config.controls === false ? false : undefined"
      :controls-position="config.controls === 'right' ? 'right' : undefined"
      @change="handleChange"
    />
    <template v-if="config.type === 'boolean'">
      <el-switch v-if="config.mode === 'switch'" v-model="value" />
      <el-select v-else v-model="value" clearable>
        <el-option label="是" :value="true" />
        <el-option label="否" :value="false" />
      </el-select>
    </template>
    <el-select
      v-if="config.type === 'select'"
      v-model="value"
      clearable
      filterable
      :placeholder="config.placeholder"
      :multiple="config.multiple"
      :remote="config.remote"
      :remote-method="remoteFilter"
      :loading="lazyLoading"
      @change="handleChange"
    >
      <el-option v-for="(item, index) in relatedDatas" :key="index" v-bind="item" />
    </el-select>
    <el-cascader
      v-if="config.type === 'cascader'"
      v-model="value"
      :placeholder="config.placeholder"
      clearable
      filterable
      :options="relatedDatas"
      :props="{
        lazy: config.lazy,
        lazyLoad: config.lazy ? lazyLoad : undefined,
        multiple: config.multiple,
        checkStrictly: config.checkStrictly
      }"
      @change="handleChange"
    />
    <el-tree-select
      v-if="config.type === 'tree-select'"
      v-model="value"
      :placeholder="config.placeholder"
      :data="relatedDatas"
      :lazy="config.lazy"
      :load="config.lazy ? lazyLoad : undefined"
      :multiple="config.multiple"
      filterable
      clearable
      @change="handleChange"
    />
    <el-checkbox-group v-if="config.type === 'checkbox'" v-model="value" @change="handleChange">
      <el-checkbox v-for="(item, index) in relatedDatas" :key="index" :label="item.value">{{ item.label }}</el-checkbox>
    </el-checkbox-group>
    <el-radio-group v-if="config.type === 'radio'" v-model="value" @change="handleChange">
      <el-radio v-for="(item, index) in relatedDatas" :key="index" :label="item.value">{{ item.label }}</el-radio>
    </el-radio-group>
    <di-selector
      v-if="config.type === 'list-selector'"
      v-model="value"
      :tree="config.tree"
      :list="config.list"
      :multiple="config.multiple"
      :data-type="config.dataType"
      :data-label="config.dataLabel"
      :placeholder="config.placeholder"
      @change="handleChange"
    />
    <el-date-picker
      v-if="['year', 'month', 'date', 'datetime', 'week'].includes(config.type)"
      v-model="value"
      clearable
      :type="config.type"
      :placeholder="config.placeholder"
      @change="handleChange"
    />
    <el-time-picker
      v-if="config.type === 'time'"
      v-model="value"
      :is-range="config.range"
      clearable
      :placeholder="config.placeholder"
      @change="handleChange"
    />
    <date-range
      v-if="['daterange', 'datetimerange'].includes(config.type)"
      v-model="value"
      :type="config.type"
      @change="handleChange"
    />
    <el-upload
      v-if="config.type === 'upload'"
      v-bind="bindUpload"
      :list-type="config.listType"
      :limit="config.limit"
      :multiple="config.limit ?? 2 > 1"
      :before-upload="beforeUpload"
      style="width: 100%"
    >
      <el-icon v-if="config.listType === 'picture-card'">
        <Plus />
      </el-icon>
      <el-button v-else>上传文件</el-button>
      <template #tip>
        <div v-if="config.placeholder" class="el-upload__tip">
          {{ config.placeholder }}
        </div>
        <div v-else-if="config.limit || config.accept || config.size" class="el-upload__tip">
          可上传
          <span v-if="config.limit">
            {{ config.limit }} 个文件
            <span v-if="config.accept">，</span>
          </span>
          <span v-if="config.accept">类型为 {{ config.accept.replace(/,/, '/') }} 的文件</span>
          <span v-if="config.size && (config.limit || config.accept)">，</span>
          <span v-if="config.size">单文件大小应小于 {{ config.size }} MB</span>。
        </div>
      </template>
    </el-upload>
  </el-form-item>
</template>

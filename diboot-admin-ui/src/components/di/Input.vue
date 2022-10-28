<script setup lang="ts" name="DiInput">
import type { FormItem } from './type'

const props = defineProps<{
  config: FormItem
  modelValue?: unknown
  relatedDatas?: LabelValue[]
  lazyLoading?: boolean
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

const handleChange = (value?: unknown) => emit('change', value)

const remoteFilter = (value?: unknown) => emit('remoteFilter', value)

const lazyLoad = ({ data }: { data: LabelValue }, resolve: (data: LabelValue[]) => void) =>
  resolve(emit('lazyLoad', data.value))
</script>

<template>
  <el-form-item :prop="config.prop" :label="config.label" :rules="config.rule">
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
      @change="handleChange"
    />
    <el-select
      v-if="config.type === 'select'"
      v-model="value"
      clearable
      :placeholder="config.placeholder"
      :multiple="config.multiple"
      :filterable="config.filterable || config.remote"
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
      :options="relatedDatas"
      :props="{
        lazy: config.lazy,
        lazyLoad: config.lazy ? lazyLoad : undefined,
        checkStrictly: config.checkStrictly
      }"
    />
    <el-tree-select
      v-if="config.type === 'tree-select'"
      v-model="value"
      :placeholder="config.placeholder"
      :data="relatedDatas"
      :lazy="config.lazy"
      :load="config.lazy ? lazyLoad : undefined"
      :filterable="config.filterable"
      clearable
    />
    <el-date-picker
      v-if="['year', 'month', 'date', 'datetime', 'week'].includes(config.type)"
      v-model="value"
      clearable
      :type="config.type"
      @change="handleChange"
    />
    <el-time-picker
      v-if="config.type === 'time'"
      v-model="value"
      :is-range="config.range"
      clearable
      @change="handleChange"
    />
    <date-range
      v-if="['daterange', 'datetimerange'].includes(config.type)"
      v-model="value"
      :type="config.type"
      @change="handleChange"
    />
  </el-form-item>
</template>

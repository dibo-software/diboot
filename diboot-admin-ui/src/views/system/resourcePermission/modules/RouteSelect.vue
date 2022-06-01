<script setup lang="ts">
import { buildViewMap } from '@/utils/route'
const componentNameOptions = reactive<LabelValue[]>([])
const viewMap = ref<Record<string, string>>({})
buildViewMap().then(res => {
  viewMap.value = res
  componentNameOptions.push(
    ...Object.keys(res).map(componentName => {
      return {
        label: componentName,
        value: componentName
      }
    })
  )
})

const componentName = ref('')
defineProps<{ modelValue?: string | unknown; routePath?: string | unknown }>()

// 事件定义
const emits = defineEmits<{
  (e: 'update:modelValue', value?: string): void
  (e: 'update:componentPath', value?: string): void
}>()
// 更改组件 和 资源路径
const changeComponentName = (val?: string) => {
  emits('update:modelValue', val)
  emits('update:componentPath', val ? viewMap.value[val] : '')
}
</script>
<template>
  <el-select v-model="componentName" placeholder="请选择组件" clearable @change="changeComponentName">
    <el-option v-for="item in componentNameOptions" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup lang="ts">
// 引入VueOfficeExcel组件
import VueOfficeExcel from '@vue-office/excel'
// 引入相关样式
import '@vue-office/excel/lib/index.css'

const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value?: string | Blob | ArrayBuffer
}>()

const fileName = ref()
const source = ref()
const loading = ref(false)

const init = (value: string | Blob | ArrayBuffer) => {
  if (typeof value === 'string') {
    loading.value = true
    api
      .download(value)
      .then(res => {
        fileName.value = res.filename
        source.value = res.data
      })
      .catch(err => ElMessage.error(err.msg || err.message))
      .finally(() => (loading.value = false))
  } else source.value = value
}

watch(
  () => props.value,
  value => {
    if (value) init(value)
    else source.value = void 0
  },
  { immediate: true }
)

const errorHandler = () => {
  ElMessage.error('Excel 加载失败')
  console.log('渲染失败')
}
</script>

<template>
  <vue-office-excel v-if="source" :src="source" @error="errorHandler" />
  <el-skeleton v-else loading animated>
    <template #template>
      <el-skeleton-item v-loading="loading" variant="rect" style="width: 100%; height: 300px" />
    </template>
  </el-skeleton>
</template>

<script setup lang="ts">
import { renderAsync } from 'docx-preview'
import { print } from '@/utils/print'

const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value: string | Blob | ArrayBuffer
}>()

const context = ref<Blob | ArrayBuffer>()
const fileName = ref<string>()
const container = ref()

/**
 * 使用docx-preview预览docx文件
 * @param file
 */
const wordPreview = (file: Blob | ArrayBuffer) => {
  renderAsync(file, container.value)
}

watch(
  () => props.value,
  value => {
    if (!value) return
    if (typeof value === 'string') {
      api
        .download(value)
        .then(res => {
          fileName.value = res.filename
          wordPreview((context.value = res.data))
        })
        .catch(err => {
          ElMessage.error(err.msg || err.message || '获取文件失败')
        })
    } else wordPreview((context.value = value))
  },
  { immediate: true }
)

defineExpose({
  print: () => context.value && print(container.value?.children[3]?.children[0]),
  download: (filename?: string) => {
    if (!context.value) return ElMessage.error('文件内容为空')
    const elink = document.createElement('a')
    const date = new Date()
    elink.download =
      filename || fileName.value || date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + '.docx'
    elink.style.display = 'none'
    const fileBlob = context.value instanceof Blob ? context.value : new Blob([context.value])
    elink.href = URL.createObjectURL(fileBlob)
    document.body.appendChild(elink)
    elink.click()
    URL.revokeObjectURL(elink.href)
    document.body.removeChild(elink)
  }
})
</script>

<template>
  <el-scrollbar style="height: 100%">
    <div ref="container" />
  </el-scrollbar>
</template>

<style scoped lang="scss">
:deep(.docx-wrapper) {
  background: var(--el-bg-color-page);
}

:deep(.docx-wrapper section.docx) {
  box-shadow: 0 0 0;
}
</style>

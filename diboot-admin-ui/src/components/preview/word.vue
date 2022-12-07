<script setup lang="ts">
import { renderAsync } from 'docx-preview'
import { print } from '@/utils/print'

const props = defineProps<{
  // string: 文件uri ;  Blob | ArrayBuffer: 文件流
  value: string | Blob | ArrayBuffer
}>()

const container = ref()
const context = ref<Blob | ArrayBuffer>()
const fileName = ref('')

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
    if (typeof value === 'string') {
      if (value) {
        api
          .download(value)
          .then(res => {
            if (res.data) {
              if (res.filename) fileName.value = res.filename
              context.value = res.data
              wordPreview(res.data)
            }
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || '获取文件失败')
          })
      }
    } else wordPreview((context.value = value))
  },
  { immediate: true }
)

const date = new Date()
const download = (filename?: string) => {
  if (!context.value) return ElMessage.error('文件内容为空')
  const elink = document.createElement('a')
  elink.download =
    filename || fileName.value || date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + '.docx'
  elink.style.display = 'none'
  const fileBlob = context.value instanceof Blob ? context.value : new Blob([context.value])
  if (fileBlob) elink.href = URL.createObjectURL(fileBlob)
  document.body.appendChild(elink)
  elink.click()
  URL.revokeObjectURL(elink.href) // 释放URL 对象
  document.body.removeChild(elink)
}

defineExpose({
  print: () => {
    print(container.value)
  },
  download
})
</script>

<template>
  <el-scrollbar>
    <div ref="container" />
  </el-scrollbar>
</template>

<style scoped lang="scss">
:deep(.docx-wrapper) {
  padding: 0 !important;
  background: #ffffff;
}
:deep(.docx-wrapper section.docx) {
  box-shadow: 0 0 0;
  padding-top: 0 !important;
}
</style>

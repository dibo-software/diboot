<script setup lang="ts">
import { renderAsync } from 'docx-preview'
import { print } from '@/utils/print'

const props = defineProps<{
  file: string | Blob | ArrayBuffer // 文件流
}>()

const container = ref() // 展示word的div
const downloadBlob = ref<Blob | ArrayBuffer>()
const fileName = ref('')

/**
 * 使用docx-preview预览docx文件
 * @param file
 */
const wordPreview = (file: Blob | ArrayBuffer) => {
  renderAsync(file, container.value)
}

watch(
  () => props.file,
  value => {
    if (typeof value === 'string') {
      if (value) {
        api.download(value).then(res => {
          if (res.data) {
            if (res.filename) fileName.value = res.filename
            downloadBlob.value = res.data
            wordPreview(res.data)
          }
        })
      }
    } else wordPreview((downloadBlob.value = value))
  },
  { immediate: true }
)

const date = new Date()
const currDate = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate()
const download = () => {
  const elink = document.createElement('a')
  elink.download = fileName.value || currDate + '.docx'
  elink.style.display = 'none'
  let fileBlob
  if (downloadBlob.value)  fileBlob = new Blob([downloadBlob.value])
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
  <div ref="container" />
</template>

<style scoped lang="scss">
:deep(.docx-wrapper) {
  padding: 0;
  background: #ffffff;
}
:deep(.docx-wrapper section.docx) {
  box-shadow: 0 0 0;
  padding: 0 !important;
}
</style>

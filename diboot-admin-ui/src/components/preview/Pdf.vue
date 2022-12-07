<script setup lang="ts">
import VuePdfEmbed from 'vue-pdf-embed'

const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value?: string | Blob | ArrayBuffer
}>()

const fileName = ref<string>('')
const objectURL = ref<string>()

const revoke = () => objectURL.value && URL.revokeObjectURL(objectURL.value)

watch(
  () => props.value,
  value => {
    if (!value) return
    if (typeof value === 'string') {
      api
        .download(value)
        .then(res => {
          revoke()
          fileName.value = res.filename
          objectURL.value = URL.createObjectURL(new Blob([res.data]))
        })
        .catch(err => ElMessage.error(err.msg || err.message || '获取文件失败'))
    } else {
      revoke()
      objectURL.value = URL.createObjectURL(value instanceof Blob ? value : new Blob([value]))
    }
  },
  { immediate: true }
)

const pdfEmbed = ref()
defineExpose({
  print: () => pdfEmbed.value?.print(),
  download: (filename: string) => {
    if (!objectURL.value) return ElMessage.error('文件内容为空')
    const elink = document.createElement('a')
    const date = new Date()
    elink.download =
      filename || fileName.value || date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + '.pdf'
    elink.style.display = 'none'
    elink.href = objectURL.value
    document.body.appendChild(elink)
    elink.click()
    document.body.removeChild(elink)
  }
})

// 释放URL对象
onBeforeUnmount(() => revoke)
</script>

<template>
  <el-scrollbar style="height: 100%">
    <vue-pdf-embed ref="pdfEmbed" :source="objectURL" />
  </el-scrollbar>
</template>

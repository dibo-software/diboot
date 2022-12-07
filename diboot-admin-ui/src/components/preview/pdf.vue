<script setup lang="ts">
import VuePdfEmbed from 'vue-pdf-embed'

const props = defineProps<{
  // string: 文件uri ;  Blob | ArrayBuffer: 文件流
  value: string | Blob | ArrayBuffer
}>()

const newUrl = ref<string>('')
const fileName = ref<string>('')
watch(
  () => props.value,
  value => {
    if (value) {
      if (typeof value === 'string') {
        api
          .download(value)
          .then(res => {
            if (res.data) {
              if (res.filename) fileName.value = res.filename
              getNewUrl(res.data)
            }
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || '获取文件失败')
          })
      } else {
        getNewUrl(value)
      }
    }
  },
  { immediate: true }
)

const getNewUrl = (val: Blob | ArrayBuffer) => {
  const pdfBlob = val instanceof Blob ? val : new Blob([val])
  newUrl.value = URL.createObjectURL(pdfBlob)
}

const date = new Date()
const download = (filename: string) => {
  const elink = document.createElement('a')
  elink.download =
    filename || fileName.value || date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + '.pdf'
  elink.style.display = 'none'
  elink.href = newUrl.value
  document.body.appendChild(elink)
  elink.click()
  URL.revokeObjectURL(elink.href) // 释放URL 对象
  document.body.removeChild(elink)
}

const pdfEmbed = ref()
defineExpose({
  print: () => {
    pdfEmbed.value.print()
  },
  download
})
</script>

<template>
  <el-scrollbar>
    <vue-pdf-embed ref="pdfEmbed" :source="newUrl" />
  </el-scrollbar>
</template>

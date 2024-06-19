<script setup lang="ts">
import VuePdfEmbed from 'vue-pdf-embed'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value?: string | Blob | ArrayBuffer
}>()

const fileName = ref<string>('')
const objectURL = ref<string>('')

const revoke = () => objectURL.value && URL.revokeObjectURL(objectURL.value)

const pdfInit = (value: string | Blob | ArrayBuffer) => {
  isPropsValue.value = true
  if (typeof value === 'string') {
    api
      .download(value)
      .then(res => {
        revoke()
        fileName.value = res.filename
        objectURL.value = URL.createObjectURL(new Blob([res.data]))
      })
      .catch(err => ElMessage.error(err.msg || err.message || i18n.t('components.document.fetchFileFailed')))
  } else {
    revoke()
    objectURL.value = URL.createObjectURL(value instanceof Blob ? value : new Blob([value]))
  }
}

const isPropsValue = ref(false)

watch(
  () => props.value,
  value => {
    if (value) pdfInit(value)
    else isPropsValue.value = false
  },
  { immediate: true }
)

const pdfEmbed = ref()
const dispaly = ref(true)

defineExpose({
  print: (value?: string | Blob | ArrayBuffer) => {
    if (value) {
      dispaly.value = false
      pdfInit(value)
      setTimeout(() => {
        pdfEmbed.value?.print()
      }, 100)
    } else pdfEmbed.value?.print()
  },
  download: (filename: string) => {
    if (!objectURL.value) return ElMessage.error(i18n.t('components.document.emptyContent'))
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
  <el-scrollbar v-show="dispaly">
    <vue-pdf-embed v-if="isPropsValue" ref="pdfEmbed" :source="objectURL" />
  </el-scrollbar>
</template>

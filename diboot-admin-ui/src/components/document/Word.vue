<script setup lang="ts">
import { renderAsync } from 'docx-preview'
import print from '@/utils/print'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value?: string | Blob | ArrayBuffer
}>()

const context = ref<Blob | ArrayBuffer>()
const fileName = ref<string>()
const container = ref()
const contextDom = ref()

/**
 * 使用docx-preview预览docx文件
 * @param file
 */
const wordPreview = async (file: Blob | ArrayBuffer) =>
  await renderAsync(file, container.value).then(
    () =>
      new Promise<HTMLElement>(resolve =>
        // 延时，等待图片渲染
        setTimeout(() => resolve((contextDom.value = container.value?.querySelector('div')?.children[0])), 100)
      )
  )

const wordInit = (value: string | Blob | ArrayBuffer) => {
  if (typeof value === 'string') {
    return new Promise<HTMLElement>((resolve, reject) => {
      api
        .download(value)
        .then(res => {
          fileName.value = res.filename
          resolve(wordPreview((context.value = res.data)))
        })
        .catch(err => {
          ElMessage.error(err.msg || err.message || i18n.t('components.document.fetchFileFailed'))
          reject()
        })
    })
  } else return wordPreview((context.value = value))
}

watch(
  () => props.value,
  value => {
    if (value) wordInit(value)
    else container.value && container.value.removeChild(container.value.children[3])
  },
  { immediate: true }
)

const dispaly = ref(true)

defineExpose({
  print: (value?: string | Blob | ArrayBuffer) => {
    if (value) {
      dispaly.value = false
      wordInit(value).then(print)
    } else contextDom.value && print(contextDom.value)
  },
  download: (filename?: string) => {
    if (!context.value) return ElMessage.error(i18n.t('components.document.emptyContent'))
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
  <el-scrollbar v-show="dispaly">
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

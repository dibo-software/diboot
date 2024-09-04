<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const i18n = useI18n()

const props = defineProps<{
  // string: 文件链接 ;  Blob | ArrayBuffer: 文件流
  value?: string | Blob | ArrayBuffer
}>()

const fileName = ref<string>()
const content = ref<string>()

const init = async (value: string | Blob | ArrayBuffer) => {
  isPropsValue.value = true
  if (typeof value === 'string') {
    api
      .download(value)
      .then(async res => {
        fileName.value = res.filename
        content.value = await new Blob([res.data]).text()
      })
      .catch(err => ElMessage.error(err.msg || err.message || i18n.t('components.document.fetchFileFailed')))
  } else {
    content.value = await (value instanceof Blob ? value : new Blob([value])).text()
  }
}

const isPropsValue = ref(false)

watch(
  () => props.value,
  value => {
    if (value) init(value)
    else fileName.value = content.value = void 0
  },
  { immediate: true }
)
</script>

<template>
  <div style="white-space: pre-wrap">{{ content ?? '' }}</div>
</template>

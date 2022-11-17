<script setup lang="ts">
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import auth, { AUTH_HEADER_KEY } from '@/utils/auth'

const props = withDefaults(
  defineProps<{
    modelValue?: string
    placeholder?: string
    height?: number | string
    width?: number | string
  }>(),
  {
    modelValue: '',
    placeholder: '请输入内容...',
    height: undefined,
    width: '100%'
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue: string): void
}>()

const vditor = ref<Vditor>()
const vditorId = 'vditor' + Math.random()

watch(
  () => props.modelValue,
  value => vditor.value?.getValue() !== value && vditor.value?.setValue(value)
)

onMounted(() => {
  vditor.value = new Vditor(vditorId, {
    mode: 'wysiwyg',
    toolbarConfig: { pin: true },
    counter: { enable: true },
    height: props.height,
    width: props.width,
    minHeight: 300,
    placeholder: props.placeholder,
    after() {
      vditor.value?.setValue(props.modelValue)
    },
    input(value) {
      emit('update:modelValue', value)
    },
    upload: {
      url: baseURL + '/file/batch-upload',
      // linkToImgUrl: baseURL + '/file/fetch-upload',
      setHeaders: () => ({ [AUTH_HEADER_KEY]: 'Bearer ' + auth.getToken() }),
      fieldName: 'files',
      filename(name) {
        return name
          .replace(/[^(a-zA-Z0-9\u4e00-\u9fa5.)]/g, '')
          .replace(/[?\\/:|<>*[\]()$%{}@~]/g, '')
          .replace(/\\s/g, '')
      },
      format(files: File[], responseText: string): string {
        const res = JSON.parse(responseText)
        const fileRecords: FileRecord[] = res.data.fileRecords
        const succMap: Record<string, string> = {}
        for (const fileRecord of fileRecords) {
          succMap[fileRecord.fileName] = baseURL + fileRecord.accessUrl
        }
        res.data.succMap = succMap
        return JSON.stringify(res)
      },
      error: msg => ElMessage.error(msg || '上传失败')
    }
  })
})
</script>

<template>
  <div :id="vditorId" />
</template>

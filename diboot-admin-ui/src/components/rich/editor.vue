<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { buildImgSrc } from '@/utils/file'
import { isExternal } from '@/utils/validate'

const props = withDefaults(defineProps<{ modelValue?: string; placeholder?: string; mode?: 'default' | 'simple' }>(), {
  modelValue: undefined,
  placeholder: '请输入内容...',
  mode: 'simple'
})

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue: string): void
  (e: 'change', value: string): void
}>()

const handleChange = (editor: IDomEditor) => {
  emit('update:modelValue', editor.getHtml())
  emit('change', editor.getHtml())
}

// 自定义上传
function customUpload<InsertFn>(uploadInsert: (file: FileRecord, insertFn: InsertFn) => void) {
  return async (file: File, insertFn: InsertFn) => {
    const formData = new FormData()
    formData.append('file', file)
    api
      .upload<FileRecord>('/file/upload', formData)
      .then(({ data }) => {
        if (!data) return
        uploadInsert(data, insertFn)
      })
      .catch(err => {
        ElMessage.error(err.msg || err.message || '上传异常，请稍后重试！')
      })
  }
}

// 内容 HTML
const valueHtml = ref<string>(props.modelValue)

const toolbarConfig: Partial<IToolbarConfig> = {}
const editorConfig: IEditorConfig = {
  placeholder: props.placeholder,
  scroll: true,
  readOnly: false,
  autoFocus: false,
  customAlert: (s: string, t: string) => {
    switch (t) {
      case 'success':
        ElMessage.success(s)
        break
      case 'info':
        ElMessage.info(s)
        break
      case 'warning':
        ElMessage.warning(s)
        break
      case 'error':
        ElMessage.error(s)
        break
      default:
        ElMessage.info(s)
        break
    }
  },
  MENU_CONF: {
    uploadImage: {
      customUpload: customUpload<(url: string, alt: string, href: string) => void>((file, insertFn) => {
        const url = buildImgSrc(file.accessUrl)
        insertFn(url, file.fileName, url)
      })
    },
    uploadVideo: {
      customUpload: customUpload<(url: string) => void>((file, insertFn) => {
        const url = isExternal(file.accessUrl) ? file.accessUrl : baseURL + file.accessUrl
        insertFn(url)
      })
    }
  }
}
</script>

<template>
  <div class="editor">
    <Toolbar :editor="editorRef" :default-config="toolbarConfig" :mode="mode" />
    <Editor
      v-model="valueHtml"
      :default-config="editorConfig"
      :mode="mode"
      @on-created="handleCreated"
      @on-change="handleChange"
    />
  </div>
</template>

<style scoped lang="scss">
.editor {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--el-border-color-lighter);

  div:first-child {
    border: 1px solid var(--el-border-color-lighter);
  }

  div:last-child {
    overflow-y: hidden;
  }
}
</style>

<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { buildImgSrc } from '../../utils/file'
import { isExternal } from '../../utils/validate'
import { api, baseURL } from '../../utils/request'

interface PropsType {
  // 模型值
  modelValue?: string
  // 编辑器占位符
  placeholder?: string
  // 标题
  title?: string
}

const props = withDefaults(defineProps<PropsType>(), {
  modelValue: '',
  placeholder: '请输入内容...',
  title: ''
})

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef<IDomEditor>()

// 创建处理
const handleCreated = (editor: IDomEditor) => {
  editorRef.value?.destroy() // 及时销毁编辑器（编辑器形式如果动态切换，应需先销毁原编辑器）
  editorRef.value = editor // 记录 editor 实例，重要！
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => editorRef.value?.destroy())

const titleValue = ref(props.title)
const contentValue = ref(props.modelValue)

watch([() => props.title, () => props.modelValue], ([title, value]) => {
  titleValue.value = title
  contentValue.value = value
})

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue: string): void
  (e: 'update:title', modelValue: string): void
}>()

const handleChangeValue = (editor: IDomEditor) => {
  emit('update:modelValue', editor.getHtml())
}

const handleChangeTitle = () => {
  emit('update:title', titleValue.value)
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
        showNotify({ type: 'danger', message: err.msg || err.message || '上传异常，请稍后重试！' })
      })
  }
}

const toolbarConfig: Partial<IToolbarConfig> = {
  // ['blockquote', 'header1', 'header2', 'header3', '|', 'bold', 'underline', 'italic', 'through', 'color', 'bgColor', 'clearStyle', '|', 'bulletedList', 'numberedList', 'todo', 'justifyLeft', 'justifyRight', 'justifyCenter', '|', 'insertLink', {…}, 'insertVideo', 'insertTable', 'codeBlock', '|', 'undo', 'redo', '|', 'fullScreen']
  toolbarKeys: ['header1', 'header2', 'header3', 'bold', 'underline', 'italic', 'bulletedList', 'numberedList']
}
const editorConfig: IEditorConfig = {
  placeholder: props.placeholder,
  scroll: true,
  readOnly: false,
  autoFocus: false,
  customAlert: (s: string, t: string) => {
    switch (t) {
      case 'success':
        showNotify({ type: 'success', message: s })
        break
      case 'info':
        showNotify({ type: 'primary', message: s })
        break
      case 'warning':
        showNotify({ type: 'warning', message: s })
        break
      case 'error':
        showNotify({ type: 'danger', message: s })
        break
      default:
        showNotify({ type: 'primary', message: s })
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
    <Toolbar :editor="editorRef" :default-config="toolbarConfig" class="editor-border" />
    <!-- 编辑器 -->
    <Editor
      v-model="contentValue"
      class="editor-content editor-border"
      :default-config="editorConfig"
      @on-created="handleCreated"
      @on-change="handleChangeValue"
      @click="editorRef?.focus()"
    />
  </div>
</template>

<style scoped lang="scss">
.editor {
  min-height: 300px;
  display: flex;
  flex-direction: column;

  .editor-border {
    border: 1px solid #ebeef5;
  }

  .editor-content {
    flex: 1;
    height: 0 !important;
  }

  .editor-container {
    max-width: 850px;
    min-height: 1200px;
    margin: 30px auto 100px auto;
    padding: 20px 50px 50px 50px;
    box-shadow: 0 2px 10px rgb(0 0 0 / 12%);
    background-color: var(--w-e-textarea-bg-color);

    .title-container {
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;

      input {
        width: 100%;
        border: 0;
        outline: none;
        font-size: 32px;
        background-color: var(--w-e-textarea-bg-color);
      }
    }
  }
}
</style>

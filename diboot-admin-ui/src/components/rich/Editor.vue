<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { buildImgSrc } from '@/utils/file'
import { isExternal } from '@/utils/validate'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
interface PropsType {
  // 模型值
  modelValue?: string
  // 编辑器占位符
  placeholder?: string
  // 编辑器模式
  mode?: 'default' | 'simple'
  // 编辑器是否采用文档形式（title：文档形式且需要标题）
  doc?: boolean | 'title'
  // 标题
  title?: string
}

const props = withDefaults(defineProps<PropsType>(), {
  modelValue: '',
  // placeholder: i18n.t('components.rich.placeholder'),
  mode: 'simple',
  doc: false,
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
        ElMessage.error(err.msg || err.message || i18n.t('components.rich.uploadError'))
      })
  }
}

const toolbarConfig: Partial<IToolbarConfig> = {}
// 监听 编辑器形式变化，移除或还原全屏功能（文档形式无法使用全屏）
watch(
  () => props.doc,
  value => (toolbarConfig.excludeKeys = value ? ['fullScreen'] : []),
  { immediate: true }
)
const editorConfig: IEditorConfig = {
  placeholder: props.placeholder || i18n.t('components.rich.placeholder'),
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
    <Toolbar :editor="editorRef" :default-config="toolbarConfig" :mode="mode" class="editor-border" />

    <!-- 文档编辑器 -->
    <el-scrollbar v-if="doc" class="editor-border" style="background-color: var(--el-bg-color-page)">
      <div class="editor-container" @click="editorRef?.focus(true)">
        <div v-if="doc === 'title'" class="title-container" @click.stop>
          <input v-model="titleValue" placeholder="Page Title..." @change="handleChangeTitle" />
        </div>
        <Editor
          v-model="contentValue"
          :default-config="editorConfig"
          :mode="mode"
          @click.stop="() => {}"
          @on-created="handleCreated"
          @on-change="handleChangeValue"
        />
      </div>
    </el-scrollbar>

    <!-- 编辑器 -->
    <Editor
      v-else
      v-model="contentValue"
      class="editor-content editor-border"
      :default-config="editorConfig"
      :mode="mode"
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
    border: 1px solid var(--el-border-color-lighter);
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
      border-bottom: 1px solid var(--el-border-color-lighter);

      input {
        width: 100%;
        border: 0;
        outline: none;
        font-size: 32px;
        background-color: var(--w-e-textarea-bg-color);
      }
    }
  }

  :deep(.w-e-textarea-video-container > :first-child) {
    max-height: 100%;
    max-width: 100%;
  }
  :deep(.w-e-scroll) {
    overflow-y: unset !important;
  }
}
</style>

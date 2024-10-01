<script setup lang="ts">
import { fileDownload } from '@/utils/file'
// import type { ButtonProps } from 'element-plus'
import { Download, View } from '@element-plus/icons-vue'

const Text = defineAsyncComponent(() => import('../document/Text.vue'))
const Pdf = defineAsyncComponent(() => import('../document/Pdf.vue'))
const Word = defineAsyncComponent(() => import('../document/Word.vue'))
const Excel = defineAsyncComponent(() => import('../document/Excel.vue'))
const Player = defineAsyncComponent(() => import('../player/index.vue'))

type DownloadButton = { url: string; title: string; params?: unknown; textMaxWidth?: string | number } //  & ButtonProps

const props = defineProps<DownloadButton>()

const fileType = computed(() => props.title.split('.').pop()?.toLowerCase())
const txtMaxWidth = computed(
  () => `${props.textMaxWidth ?? 120}${`${props.textMaxWidth ?? '0'}`.match(/\d+/) ? 'px' : ''}`
)

const progress = ref(0)

const download = async () => {
  let data = props.params
  if (typeof props.params === 'function') {
    data = await props.params()
  }
  fileDownload(props.url, data, percentage => (progress.value = percentage))
    ?.catch(() => (progress.value = -1))
    .finally(() => setTimeout(() => (progress.value = 0), 1000))
}

const visible = ref(false)

const filePreviewType = {
  text: ['txt', 'sql'],
  pdf: ['pdf'],
  word: ['docx'],
  excel: ['xlsx'],
  video: ['mp4', 'mp3', 'wav']
}

const previewFileType = Object.values(filePreviewType).reduce((arr, e) => arr.concat(e), [])

const playerRef = ref()

watch(visible, val => !val && playerRef.value?.stop())
</script>

<template>
  <el-button-group v-bind="props">
    <el-button
      :icon="Download"
      v-bind="props"
      class="cartoon"
      :class="progress === 100 ? 'download-complete' : progress < 0 ? 'download-error' : progress > 0 ? 'download' : ''"
      @click="download"
    >
      <el-text truncated>
        {{ title }}
      </el-text>
    </el-button>
    <el-button
      v-if="fileType && url.startsWith('/file') && previewFileType.includes(fileType)"
      v-bind="props"
      :icon="View"
      :title="$t('components.download.previewTheFile')"
      @click="visible = true"
    >
      <el-dialog v-model="visible" :title="$t('components.download.filePreview')" draggable align-center>
        <el-scrollbar max-height="calc(100vh - 160px)">
          <Text v-if="filePreviewType.text.includes(fileType)" :value="url" />
          <Pdf v-else-if="filePreviewType.pdf.includes(fileType)" :value="url" />
          <Word v-else-if="filePreviewType.word.includes(fileType)" :value="url" />
          <Excel v-else-if="filePreviewType.excel.includes(fileType)" :value="url" style="min-height: 360px" />
          <Player v-else-if="filePreviewType.video.includes(fileType)" ref="playerRef" :value="url" />
        </el-scrollbar>
      </el-dialog>
    </el-button>
  </el-button-group>
</template>

<style scoped lang="scss">
.el-button-group {
  vertical-align: baseline;
  margin-right: 12px;

  .el-button > :deep(span) {
    max-width: v-bind('txtMaxWidth');
  }
}

.download {
  background: linear-gradient(
    to right,
    var(--el-color-primary) v-bind('progress + "%"'),
    var(--el-bg-color) 0
  ) !important;

  &-error {
    background: var(--el-color-danger) !important;
  }

  &-complete {
    background: var(--el-color-success) !important;
  }
}

.cartoon {
  transition: cubic-bezier(0.2, -0.5, 0.36, 1.36) 1s;
}
</style>

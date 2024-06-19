<script setup lang="ts">
import { fileDownload } from '@/utils/file'
// import type { ButtonProps } from 'element-plus'

type DownloadButton = { url: string; params?: unknown } //  & ButtonProps

const props = defineProps<DownloadButton>()

const progress = ref(0)

const download = () => {
  fileDownload(props.url, props.params, percentage => (progress.value = percentage))
    ?.catch(() => (progress.value = -1))
    .finally(() => setTimeout(() => (progress.value = 0), 1000))
}
</script>

<template>
  <el-button
    v-bind="props"
    class="cartoon"
    :class="progress === 100 ? 'download-complete' : progress < 0 ? 'download-error' : progress > 0 ? 'download' : ''"
    @click="download"
  >
    <slot>{{ $t('components.download') }}</slot>
  </el-button>
</template>

<style scoped lang="scss">
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

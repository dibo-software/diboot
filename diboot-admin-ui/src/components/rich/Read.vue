<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor } from '@wangeditor/editor-for-vue'
import auth from '@/utils/auth'

defineProps<{ value?: string }>()
</script>

<template>
  <editor
    :model-value="(value || '').replaceAll('{{token}}', (auth.getToken() || '') as string)"
    :default-config="{ readOnly: true, autoFocus: false }"
    style="height: auto"
  />
</template>

<style scoped lang="scss">
:deep(.w-e-textarea-video-container > :first-child) {
  max-height: 100%;
  max-width: 100%;
}
</style>

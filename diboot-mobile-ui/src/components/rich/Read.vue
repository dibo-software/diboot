<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor } from '@wangeditor/editor-for-vue'

defineProps<{ value?: string }>()
</script>

<template>
  <editor :model-value="value ?? ''" :default-config="{ readOnly: true, autoFocus: false }" style="height: auto" />
</template>

<script setup lang="ts">
import Vditor from 'vditor'

const props = withDefaults(
  defineProps<{
    value?: string
  }>(),
  {
    value: undefined
  }
)

const vditorDom = ref<HTMLDivElement>()

onMounted(() => {
  if (vditorDom.value) {
    Vditor.preview(vditorDom.value as HTMLDivElement, props.value)
  }
})
</script>

<template>
  <div ref="vditorDom" class="vditor-reset" />
</template>

<style scoped></style>

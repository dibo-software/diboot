<script setup lang="ts">
import Vditor from 'vditor'
import 'vditor/dist/index.css'

const props = withDefaults(defineProps<{ value?: string }>(), { value: undefined })

const vditorDom = ref<HTMLDivElement>()

onMounted(() => {
  watch(
    () => props.value,
    value => vditorDom.value && Vditor.preview(vditorDom.value as HTMLDivElement, value ?? ''),
    { immediate: true }
  )
})
</script>

<template>
  <div ref="vditorDom" />
</template>

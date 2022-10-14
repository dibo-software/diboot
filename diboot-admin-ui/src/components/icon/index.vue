<script setup lang="ts">
import IconLibrary from '@/config/icon-library'
/**
 * 图标名称 [库别名（不填默认为Element）]:[(路径)图标名]
 */
const props = defineProps<{
  name?: string
}>()

const Icon = computed(() => {
  if (!props.name) return
  const split = props.name.includes(':') ? props.name.split(/:/) : ['Element', props.name]
  const lib = IconLibrary[split[0]]
  return lib && lib[split[1]]
})
</script>

<template>
  <template v-if="Icon">
    <icon />
  </template>
</template>

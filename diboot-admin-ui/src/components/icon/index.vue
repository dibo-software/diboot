<script setup lang="ts">
import IconLibrary from '@/config/iconLibrary'

const props = defineProps<{
  /**
   * 图标名称 [库别名]:[(路径)图标名]
   */
  name?: string
}>()

const Icon = computed(() => {
  if (!props.name) return
  const split = props.name.split(/:/)
  const lib = IconLibrary[split[0]]
  return lib && lib[split[1]]
})
</script>

<template>
  <template v-if="Icon">
    <icon />
  </template>
</template>

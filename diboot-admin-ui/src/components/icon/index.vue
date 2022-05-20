<script setup lang="ts">
import IconLibrary from '@/config/iconLibrary'
/**
 * 图标名称 [库别名（不填默认为Element）]:[(路径)图标名]
 */
const props = withDefaults(
  defineProps<{
    name?: string
    size?: number
    color?: string
  }>(),
  {
    name: '',
    color: 'inherit',
    size: 20
  }
)

const Icon = computed(() => {
  if (!props.name) return
  const split = props.name.includes(':') ? props.name.split(/:/) : ['Element', props.name]
  console.log(split)
  const lib = IconLibrary[split[0]]
  return lib && lib[split[1]]
})
</script>

<template>
  <template v-if="Icon">
    <el-icon :size="size" :color="color">
      <icon />
    </el-icon>
  </template>
</template>

<script setup lang="ts">
import { Icon } from '@vicons/utils'
import iconLoad from '@/utils/icon-loader'

/**
 * 图标名称 [库别名]:[图标名]
 */
const props = defineProps<{
  name?: string
  tag?: string
  color?: string
  size?: string | number
}>()

const iconComponent = computed(() => iconLoad(props.name))
</script>

<template>
  <Icon v-if="iconComponent || $slots.default" :tag="tag" :color="color" :size="size">
    <slot>
      <component :is="iconComponent" />
    </slot>
  </Icon>
</template>

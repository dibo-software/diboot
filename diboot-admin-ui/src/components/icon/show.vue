<script setup lang="ts">
import { findIcon } from './library'

const props = defineProps<{ name?: string }>()

const Icon = computed(() => findIcon(props.name))
</script>

<template>
  <template v-if="Icon">
    <icon />
  </template>
</template>

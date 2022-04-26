<script setup lang="ts" name="Dashboard">
// 可接收 已使用的可视高度，方便主区域计算使用
defineProps<{ usedVisibleHeight?: number }>()
</script>

<template>
  Dashboard<br /><br />
  已使用的可视高度：{{ usedVisibleHeight }}px
</template>

<style scoped></style>

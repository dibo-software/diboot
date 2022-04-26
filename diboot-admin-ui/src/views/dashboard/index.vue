<script setup lang="ts" name="Dashboard">
console.log('Dashboard')
// 可接收已占用的高度，方便主区域计算使用
defineProps<{ alreadyOccupyHeight?: number }>()
</script>

<template>
  Dashboard<br /><br />
  已占有的高度：{{ alreadyOccupyHeight }}px
</template>

<style scoped></style>

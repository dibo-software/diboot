<script setup lang="ts">
console.log('Dashboard')
</script>

<template>
  Dashboard

  <el-button @click="$router.push({ name: 'Login' })">跳转登录页面</el-button>
</template>

<style scoped></style>

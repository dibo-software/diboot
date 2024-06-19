<script setup lang="ts" name="ChatAI">
import ChatAside from './ChatAside.vue'
import ChatMain from './ChatMain.vue'
</script>

<template>
  <el-container style="height: 100%">
    <chat-aside width="200px" />
    <el-main style="background-color: var(--el-bg-color-page)">
      <chat-main />
      <div class="chat" />
    </el-main>
  </el-container>
</template>

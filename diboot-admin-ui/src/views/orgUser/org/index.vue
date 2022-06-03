<script lang="ts" setup name="orgIndex">
import orgTree from './orgTree.vue'
defineProps<{ usedVisibleHeight?: number }>()
</script>
<template>
  <el-container :style="{ height: `calc(100vh - ${usedVisibleHeight}px)` }" class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree />
    </el-aside>
    <el-container>
      <el-header>Header</el-header>
      <el-main>Main</el-main>
      <el-footer>Footer</el-footer>
    </el-container>
  </el-container>
</template>
<style lang="scss" scoped>
.el-container {
  align-items: stretch;
}
.el-aside {
  border-right: 1px solid #eee;
}
</style>

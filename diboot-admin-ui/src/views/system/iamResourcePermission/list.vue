<script setup lang="ts" name="IamResourcePermissionList">
// import { useLoadRelatedData } from '@/utils/relatedData'
import { ref } from 'vue'
import ResourcePermissionForm from './form.vue'
import MenuTree from './menuTree.vue'

// 接收可视高度
defineProps<{ usedVisibleHeight?: number }>()
</script>
<template>
  <el-container class="menu-permission-container">
    <el-aside class="menu-container">
      <menu-tree></menu-tree>
    </el-aside>
    <el-main>
      <resource-permission-form />
    </el-main>
  </el-container>
</template>
<style scoped lang="scss">
.menu-permission-container {
  height: calc(100vh - v-bind('usedVisibleHeight + "px"'));
}
.menu-container {
  border-right: 1px solid var(--el-color-info-light-9);
  padding-right: 10px;
}
</style>

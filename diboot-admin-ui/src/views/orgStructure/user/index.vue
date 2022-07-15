<script setup lang="ts" name="UserIndex">
import OrgTree from '@/views/orgStructure/org/orgTree.vue'
import UserList from './list.vue'
import type { OrgModel } from '@/views/orgStructure/org/type'

const currentNodeId = ref('')
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeId.value = currentNode.id || ''
}
</script>
<template>
  <el-container class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" :readonly="true" @change-current-node="changeCurrentNode" />
    </el-aside>
    <el-container>
      <div class="list-container">
        <user-list ref="userListRef" :org-id="currentNodeId" />
      </div>
    </el-container>
  </el-container>
</template>
<style lang="scss" scoped>
.el-container {
  height: 100%;
}
.el-aside {
  box-sizing: border-box;
  border-right: solid 1px var(--el-menu-border-color);
}
.list-container {
  width: 100%;
  height: 100%;
}
</style>

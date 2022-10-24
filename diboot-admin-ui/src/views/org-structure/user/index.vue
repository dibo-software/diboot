<script setup lang="ts" name="User">
import OrgTree from '@/views/org-structure/org/OrgTree.vue'
import UserList from './List.vue'

const currentNodeId = ref('')
const changeCurrentNode = (currentNodeKey?: string) => {
  currentNodeId.value = currentNodeKey ?? ''
}
</script>
<template>
  <el-container class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" @click-node="changeCurrentNode" />
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

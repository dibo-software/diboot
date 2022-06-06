<script setup lang="ts" name="OrgUserIndex">
import orgTree from '../org/orgTree.vue'
import userList from './list.vue'
import { OrgModel } from '@/views/orgUser/org/type'
defineProps<{ usedVisibleHeight?: number }>()

const currentNodeId = ref('0')
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeId.value = currentNode.id || '0'
}

const tabLabel = computed(() => {
  return currentNodeId.value === '0' ? '所有部门' : '子部门'
})
</script>
<template>
  <el-container :style="{ height: `calc(100vh - ${usedVisibleHeight}px)` }" class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" :readonly="true" @change-current-node="changeCurrentNode" />
    </el-aside>
    <el-container class="list-container">
      <el-tabs model-value="orgListTab" class="el-tabs">
        <el-tab-pane :label="tabLabel" name="orgListTab">
          <user-list ref="userListRef" :org-id="currentNodeId" />
        </el-tab-pane>
      </el-tabs>
    </el-container>
  </el-container>
</template>
<style lang="scss" scoped>
.el-aside {
  box-sizing: border-box;
  padding-right: 10px;
  border-right: 1px solid #eee;
}
.list-container {
  box-sizing: border-box;
  padding-left: 10px;
}
.el-tabs {
  width: 100%;
}
</style>

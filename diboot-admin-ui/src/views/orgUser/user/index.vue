<script setup lang="ts" name="OrgUserIndex">
import orgTree from '../org/orgTree.vue'
import userList from './list.vue'
import type { OrgModel } from '@/views/orgUser/org/type'
defineProps<{ usedVisibleHeight?: number }>()

const currentNodeId = ref('0')
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeId.value = currentNode.id || '0'
}

const tabLabel = computed(() => {
  return currentNodeId.value === '0' ? '所有人员列表' : '当前人员列表'
})
</script>
<template>
  <el-container class="el-container">
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
.el-container {
  height: 100%;
}
.el-aside {
  box-sizing: border-box;
  padding: 10px;
  border-right: 1px solid #eee;
}
.list-container {
  box-sizing: border-box;
  padding: 0 10px;
}
.el-tabs {
  width: 100%;
  height: 100%;
  :deep(.el-tabs__content) {
    height: calc(100% - 55px);
    .el-tab-pane {
      height: 100%;
    }
    .tab-wrapper {
      height: 100%;
    }
  }
}
</style>

<script lang="ts" setup name="OrgIndex">
import OrgTree from './OrgTree.vue'
import OrgList from './OrgList.vue'
import type { OrgModel } from './type'

const currentNodeId = ref<string>('0')
const currentNodeInfo = ref<OrgModel | undefined>()
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeInfo.value = currentNode
  currentNodeId.value = currentNode.id || ''
}
// 重新加载树结构
const orgTreeRef = ref()
const reload = async () => {
  await orgTreeRef.value?.reload()
}
// 书结构变更函数
const orgListRef = ref()
const treeDataChange = async () => {
  await orgListRef.value?.onSearch()
}
</script>
<template>
  <el-container class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" @change-current-node="changeCurrentNode" @data-change="treeDataChange" />
    </el-aside>
    <el-container class="right-container">
      <div class="content-container full-height-container">
        <div class="el-tabs">
          <org-list ref="orgListRef" :parent-id="currentNodeId" @reload="reload" />
        </div>
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
.right-container {
  box-sizing: border-box;
  .content-container {
    width: 100%;
    :deep(.item-label) {
      width: 60px;
    }
    .detail-wrapper {
      margin-top: 10px;
    }
  }
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

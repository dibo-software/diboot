<script lang="ts" setup name="orgIndex">
import orgTree from './orgTree.vue'
import orgList from './orgList.vue'
import { OrgModel } from './type'
defineProps<{ usedVisibleHeight?: number }>()

const currentNodeId = ref<string>('0')
const currentNodeInfo = reactive({ currentNode: {} })
const changeCurrentNode = (currentNode: OrgModel) => {
  console.log('changeCurrentNode', currentNode)
  currentNodeInfo.currentNode = currentNode
  currentNodeId.value = currentNode.id || '0'
}
</script>
<template>
  <el-container :style="{ height: `calc(100vh - ${usedVisibleHeight}px)` }" class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree @change-current-node="changeCurrentNode" />
    </el-aside>
    <el-container>
      <org-list :parent-id="currentNodeId" />
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

<script lang="ts" setup name="orgIndex">
import orgTree from './orgTree.vue'
import orgList from './orgList.vue'
import { OrgModel } from './type'
import OrgTree from '@/views/orgUser/org/hooks/orgTree'
defineProps<{ usedVisibleHeight?: number }>()

const currentNodeId = ref<string>('0')
const currentNodeInfo = reactive({ currentNode: {} })
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeInfo.currentNode = currentNode
  currentNodeId.value = currentNode.id || '0'
}

const tabLabel = computed(() => {
  return currentNodeId.value === '0' ? '所有部门' : '子部门'
})
// 重新加载树结构
const orgTreeRef = ref<InstanceType<typeof orgTree>>()
const reload = async () => {
  await orgTreeRef.value?.reload()
}
</script>
<template>
  <el-container :style="{ height: `calc(100vh - ${usedVisibleHeight}px)` }" class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" @change-current-node="changeCurrentNode" />
    </el-aside>
    <el-container class="el-container">
      <el-tabs model-value="orgListTab" class="el-tabs">
        <el-tab-pane :label="tabLabel" name="orgListTab">
          <org-list :parent-id="currentNodeId" @reload="reload" />
        </el-tab-pane>
      </el-tabs>
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
.el-container {
  box-sizing: border-box;
  padding: 0 10px;
}
.el-tabs {
  width: 100%;
}
</style>

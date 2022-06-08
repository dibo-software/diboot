<script lang="ts" setup name="orgIndex">
import type orgTree from './orgTree.vue'
import type orgList from './orgList.vue'
import orgForm from './form.vue'
import type { OrgModel } from './type'
import OrgTree from '@/views/orgUser/org/orgTree.vue'
defineProps<{ usedVisibleHeight?: number }>()

const currentNodeId = ref<string>('0')
const currentNodeInfo = ref<OrgModel | undefined>()
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeInfo.value = currentNode
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
// 书结构变更函数
const orgListRef = ref<InstanceType<typeof orgList>>()
const treeDataChange = async () => {
  await orgListRef.value?.onSearch()
}
</script>
<template>
  <el-container class="el-container">
    <el-aside class="el-aside" width="240px">
      <org-tree ref="orgTreeRef" @change-current-node="changeCurrentNode" @data-change="treeDataChange" />
    </el-aside>
    <el-container class="list-container">
      <div class="detail-container">
        <el-descriptions v-if="currentNodeInfo !== undefined" class="detail-wrapper" :column="3" border>
          <el-descriptions-item label-class-name="item-label" label-align="right" label="全称">
            {{ currentNodeInfo.name }}
          </el-descriptions-item>
          <el-descriptions-item label-class-name="item-label" label-align="right" label="简称">
            {{ currentNodeInfo.shortName }}
          </el-descriptions-item>
          <el-descriptions-item label-class-name="item-label" label-align="right" label="编码">
            {{ currentNodeInfo.code }}
          </el-descriptions-item>
        </el-descriptions>
        <el-tabs model-value="orgListTab" class="el-tabs">
          <el-tab-pane :label="tabLabel" name="orgListTab">
            <org-list ref="orgListRef" :parent-id="currentNodeId" @reload="reload" />
          </el-tab-pane>
        </el-tabs>
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
  padding: 10px;
  border-right: 1px solid #eee;
}
.list-container {
  box-sizing: border-box;
  padding: 0 10px;
}
.detail-container {
  width: 100%;
  :deep(.item-label) {
    width: 60px;
  }
  .detail-wrapper {
    margin-top: 10px;
  }
}
.el-tabs {
  width: 100%;
}
</style>

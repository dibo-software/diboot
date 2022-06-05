<script lang="ts" setup name="orgTree">
import { defineEmits } from 'vue'
import { OrgModel } from '@/views/orgUser/org/type'
import useOrgTree from './hooks/orgTree'

const emit = defineEmits(['changeCurrentNode'])

const { data, loadTree } = useOrgTree({})

const changeCurrentNode = (currentNode: OrgModel) => {
  emit('changeCurrentNode', currentNode)
}
const reload = async () => {
  await loadTree()
}
loadTree()
defineExpose({ reload })
</script>
<template>
  <el-tree
    class="org-tree"
    :default-expand-all="true"
    :highlight-current="true"
    :expand-on-click-node="false"
    :props="{ label: 'shortName' }"
    :data="data"
    @current-change="changeCurrentNode"
  />
</template>
<style lang="scss" scoped>
.org-tree {
  :deep(.el-tree-node__content) {
    height: 36px;
  }
}
</style>

<script lang="ts" setup name="orgTree">
import { defineEmits } from 'vue'
import { OrgModel } from '@/views/orgUser/org/type'

const emit = defineEmits(['changeCurrentNode'])

const data = ref<OrgModel[]>([])
const loadTree = async () => {
  const res = await api.get<OrgModel[]>('/org/tree')
  if (res.code === 0 && res.data !== undefined) {
    data.value = res.data
  }
}
const changeCurrentNode = (currentNode: OrgModel) => {
  emit('changeCurrentNode', currentNode)
}

loadTree()
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

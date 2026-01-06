<script setup lang="ts">
import type { TreeNodeData } from 'element-plus/es/components/tree/src/tree.type'
import type { Resource } from '@/views/system/resource/type'
import type { FilterNodeMethodFunction } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{
    initQueryParam?: { [key: string]: unknown }
  }>(),
  {
    initQueryParam: () => ({})
  }
)

const emit = defineEmits<{
  (e: 'getSelectedIdList', value: string[]): void
}>()
// 权限树相关
const transformField = {
  label: 'displayName'
}
const { treeRef, treeDataList, selectedIdList, getTree, checkNode, flatTreeNodeClass } = useTreeCrud<Resource>({
  baseApi: '/iam/resource',
  treeApi: '/tree',
  initQueryParam: props.initQueryParam,
  transformField
})
const treeProps = {
  label: 'displayName',
  class: (data: TreeNodeData) => ({ ...flatTreeNodeClass(data), mobile: data.appModule === 'mobile' })
}
getTree()

// 展开缩起树
const isExpandTree = ref(true)
const changeTreeExpandStatus = (value: boolean) => {
  const nodes = treeRef?.value?.store?._getAllNodes()
  nodes &&
    nodes.forEach(item => {
      item.expanded = value
    })
}

// 树搜索
const filterTreeName = ref('')
watch(filterTreeName, val => {
  treeRef.value!.filter(val)
})
const filterNode: FilterNodeMethodFunction = (value: string, data: Resource) => {
  isExpandTree.value = true
  changeTreeExpandStatus(true)
  if (!value) return true
  return data?.displayName?.includes(value)
}

const handleCheckNode = (currentNode: Resource, data: { checkedKeys: string[] }) => {
  checkNode(currentNode, data)
  emit('getSelectedIdList', selectedIdList.value)
}

const setCheckedKeys = (idList: string[]) => {
  selectedIdList.value = idList
  treeRef.value?.setCheckedKeys(idList)
}

defineExpose({
  setCheckedKeys
})
</script>
<template>
  <el-input v-model="filterTreeName" :prefix-icon="Search" placeholder="" style="width: 200px; margin-right: 20px" />
  <el-switch
    v-model="isExpandTree"
    :active-text="$t('role.expand')"
    :inactive-text="$t('role.putAway')"
    @change="changeTreeExpandStatus"
  />
  <el-scrollbar height="calc(80vh - 350px)" style="width: 100%; padding-top: 10px">
    <el-tree
      ref="treeRef"
      style="width: 100%"
      :expand-on-click-node="false"
      :props="treeProps"
      :data="treeDataList"
      show-checkbox
      check-strictly
      node-key="id"
      default-expand-all
      :filter-node-method="filterNode"
      @check="handleCheckNode"
    />
  </el-scrollbar>
</template>

<style scoped></style>

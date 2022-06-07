<script lang="ts" setup name="orgTree">
import { defineEmits } from 'vue'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import type { OrgModel } from '@/views/orgUser/org/type'
import useTree from '@/views/system/resourcePermission/hooks/tree'
import orgForm from './form.vue'

type Props = {
  readonly?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  readonly: false
})
const emit = defineEmits(['changeCurrentNode', 'dataChange'])

const { filterNode, getTree, addTreeNode, nodeClick, treeRef, searchWord, treeDataList, loading } = useTree<OrgModel>({
  baseApi: '/org',
  treeApi: '/tree',
  transformField: { label: 'shortName', value: 'id' }
})
// 移除树节点
const removeCurrentNode = async () => {
  if (!currentNodeId.value) {
    ElMessage.warning('未选择数据')
    return
  }
  ElMessageBox.confirm('确认删除已选节点吗？', '删除节点', { type: 'warning' })
    .then(() => {
      api
        .post(`/org/batchDelete`, [currentNodeId.value])
        .then(() => {
          ElMessage.success('删除节点成功！')
          getTree()
          emit('dataChange')
        })
        .catch(err => {
          ElMessage.error(err.msg || err.message || '删除失败！')
        })
    })
    .catch(() => null)
}

const currentNodeId = ref('0')
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeId.value = currentNode?.id || '0'
  emit('changeCurrentNode', currentNode)
}

// 打开表单
const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const add = () => {
  openForm()
}
const editCurrentNode = () => {
  if (!currentNodeId.value || currentNodeId.value === '0') {
    ElMessage.warning('未选择数据')
    return
  }
  openForm(currentNodeId.value)
}
const reload = async () => {
  await getTree()
}

const formComplete = async () => {
  emit('dataChange')
  await getTree()
}
getTree()
defineExpose({ reload })
</script>
<template>
  <div v-if="!readonly" class="tree-header">
    <div class="handle-container">
      <template v-if="currentNodeId === '0'">
        <el-button type="primary" :icon="Plus" class="btn-block" @click="add()">添加顶层部门</el-button>
      </template>
      <el-button-group v-else>
        <el-button type="primary" :icon="Plus" @click="add()" />
        <el-button type="primary" :icon="Edit" @click="editCurrentNode()" />
        <el-button type="danger" :icon="Delete" @click="removeCurrentNode()" />
      </el-button-group>
    </div>
    <div class="tree-header__search">
      <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
    </div>
  </div>
  <el-tree
    ref="treeRef"
    class="org-tree"
    node-key="id"
    :default-expand-all="true"
    :highlight-current="true"
    :expand-on-click-node="false"
    :props="{ label: 'shortName' }"
    :data="treeDataList"
    :check-strictly="true"
    :filter-node-method="filterNode"
    @current-change="changeCurrentNode"
  />
  <org-form ref="formRef" :parent-id="currentNodeId" @complete="formComplete" />
</template>
<style lang="scss" scoped>
.tree-header__search {
  margin: 5px 0;
}
.btn-block {
  width: 100%;
}
.org-tree {
  :deep(.el-tree-node__content) {
    height: 36px;
  }
}
</style>

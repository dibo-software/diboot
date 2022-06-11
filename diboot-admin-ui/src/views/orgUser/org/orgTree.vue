<script lang="ts" setup name="orgTree">
import { defineEmits } from 'vue'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import type { OrgModel } from '@/views/orgUser/org/type'
import orgForm from './form.vue'

type Props = {
  readonly?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  readonly: false
})
const emit = defineEmits(['changeCurrentNode', 'dataChange'])

const { filterNode, getTree, addTreeNode, nodeClick, treeRef, searchWord, treeDataList, loading } =
  useTreeCrud<OrgModel>({
    baseApi: '/org',
    treeApi: '/tree',
    transformField: { label: 'shortName', value: 'id' }
  })

const currentNodeId = ref('')
const changeCurrentNode = (currentNode: OrgModel) => {
  currentNodeId.value = currentNode?.id || ''
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

const edit = (data: OrgModel) => {
  openForm(data.id)
}

const loadTreeWithNullNode = async () => {
  await getTree()
  if (treeDataList.value) {
    treeDataList.value.unshift({
      id: '',
      parentId: '',
      topOrgId: '',
      name: '所有部门',
      shortName: '所有部门',
      type: '',
      code: 'ALL',
      managerId: '',
      depth: 1,
      createTime: ''
    })
  }
}
// 移除树节点
const remove = async (data: OrgModel) => {
  ElMessageBox.confirm('确认删除已选节点吗？', '删除节点', { type: 'warning' })
    .then(() => {
      api
        .post(`/org/batchDelete`, [data.id])
        .then(() => {
          ElMessage.success('删除节点成功！')
          loadTreeWithNullNode()
          emit('dataChange')
        })
        .catch(err => {
          ElMessage.error(err.msg || err.message || '删除失败！')
        })
    })
    .catch(() => null)
}
const reload = async () => {
  await loadTreeWithNullNode()
}

const formComplete = async () => {
  emit('dataChange')
  await loadTreeWithNullNode()
}
loadTreeWithNullNode()
defineExpose({ reload })
</script>
<template>
  <div class="full-height-container">
    <el-header>
      <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
    </el-header>
    <div class="tree-container">
      <el-tree
        ref="treeRef"
        node-key="id"
        :default-expand-all="true"
        :highlight-current="true"
        :expand-on-click-node="false"
        :props="{ label: 'shortName' }"
        :data="treeDataList"
        :check-strictly="true"
        :filter-node-method="filterNode"
        @current-change="changeCurrentNode"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="label">{{ node.label }}</span>
            <span v-if="!readonly" class="btn-wrapper">
              <el-button type="text" :icon="Edit" @click="edit(data)" />
              <el-button class="btn-delete" type="text" :icon="Delete" @click="remove(data)" />
            </span>
          </span>
        </template>
      </el-tree>
    </div>
    <el-footer v-if="!readonly" class="el-footer">
      <el-button type="primary" :icon="Plus" class="btn-block" @click="add()">添加顶层部门</el-button>
    </el-footer>
  </div>
  <org-form ref="formRef" :parent-id="currentNodeId" @complete="formComplete" />
</template>
<style lang="scss" scoped>
.el-header {
  height: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color);
}
.el-footer {
  padding: 15px 10px;
  border-top: 1px solid #e5e5e5;
}
.tree-container {
  height: 100%;
  .tree-node {
    display: flex;
    flex: 1;
    align-items: center;
    justify-content: space-between;
    .label {
      font-size: 14px;
      font-weight: normal;
    }
    .btn-wrapper {
      display: none;
      box-sizing: border-box;
      padding-right: 5px;
    }
    .btn-delete {
      margin-left: 6px;
      color: var(--el-color-danger);
    }
  }
  :deep(.el-tree-node__content) {
    height: 36px;
  }
  :deep(.el-tree-node__content:hover) {
    .btn-wrapper {
      display: inline-block;
    }
  }
}
.btn-block {
  width: 100%;
}
</style>

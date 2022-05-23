<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
// import type { Tree } from './type'
import type { ElTree } from 'element-plus'
import useTree from './tree'
import type { Tree } from './tree'
const searchWord = ref('')
const treeRef = ref<InstanceType<typeof ElTree>>()

const filterNode = (value: string, data: Partial<Tree>) => {
  if (!value) return true
  return (data as Tree).label.includes(value)
}
const { checkChange, selectedIdList, selectedFullList } = useTree()

//监听keyword变化
watch(searchWord, val => {
  treeRef.value!.filter(val)
})
const addMenu = () => {
  console.log('add')
}
const delMenu = () => {
  console.log('del', selectedFullList.value)
  ElMessage({
    message: 'delete:node ' + selectedFullList.value.map((item: Tree) => item.label).join('、'),
    grouping: true,
    type: 'success'
  })
}
const defaultProps = {
  children: 'children',
  label: 'label'
}
const dataSource = ref<Tree[]>([
  {
    id: 1,
    label: '系统管理',
    children: [
      {
        id: 3,
        label: '字典管理'
      },
      {
        id: 4,
        label: '角色管理'
      },
      {
        id: 5,
        label: '资源权限'
      }
    ]
  }
])
</script>
<template>
  <el-space :fill="true" wrap class="tree-container">
    <div class="tree-header">
      <el-button type="primary" :icon="Plus" @click="addMenu">添加</el-button>
      <el-button type="danger" :icon="Delete" @click="delMenu">删除</el-button>
    </div>
    <div class="tree-body">
      <div class="tree-body__search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
      </div>
      <el-tree
        ref="treeRef"
        :data="dataSource"
        :props="defaultProps"
        draggable
        show-checkbox
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        @check-change="checkChange"
        :filter-node-method="filterNode"
      >
        <template #default="{ node }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <el-icon class="plus-icon">
              <icon name="Plus" />
            </el-icon>
          </span>
        </template>
      </el-tree>
    </div>
  </el-space>
</template>

<style scoped lang="scss">
.tree-container {
  .tree-body {
    &__search {
      margin-bottom: 5px;
    }
  }
}
.custom-tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 10px;
  font-size: var(--el-font-size-base);
  &:hover {
    .plus-icon {
      display: inline-block;
    }
  }
  .plus-icon {
    display: none;
    padding: 2px;
    border-radius: 50%;
    &:hover {
      background-color: #d3d3d3;
      transition: background-color 0.3s;
    }
  }
}
</style>

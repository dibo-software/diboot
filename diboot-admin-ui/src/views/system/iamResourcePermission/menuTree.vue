<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import useTree from './tree'
import type { ResourcePermission } from './type'
const defaultProps = {
  label: 'displayName'
}
const { checkChange, filterNode, getTree, addTreeNode, removeTreeNode, treeRef, searchWord, treeDataList } =
  useTree<ResourcePermission>({
    baseApi: '/iam/resourcePermission',
    treeApi: '/getMenuTreeList',
    transformField: defaultProps
  })
// 初始化tree数据
getTree()
</script>
<template>
  <el-space :fill="true" wrap class="tree-container">
    <div class="tree-header">
      <el-button type="primary" :icon="Plus">添加</el-button>
      <el-button type="danger" :icon="Delete" @click="removeTreeNode">删除</el-button>
    </div>
    <div class="tree-body">
      <div class="tree-body__search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
      </div>
      <el-tree
        ref="treeRef"
        :data="treeDataList"
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

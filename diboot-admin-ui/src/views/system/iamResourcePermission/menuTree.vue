<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import useTree from './tree'
import type { ResourcePermission } from './type'
import { defineEmits } from 'vue'
const defaultProps = {
  label: 'displayName'
}
const emit = defineEmits<{
  (e: 'click-node', id: string): void
}>()
const {
  checkChange,
  filterNode,
  getTree,
  addTreeNode,
  removeTreeNode,
  nodeClick,
  treeRef,
  searchWord,
  treeDataList,
  currentNodeKey
} = useTree<ResourcePermission>({
  baseApi: '/iam/resourcePermission',
  treeApi: '/getMenuTreeList',
  transformField: defaultProps,
  clickNodeCallback(id) {
    emit('click-node', id)
  }
})
// 初始化tree数据
getTree()

/**
 * 添加顶级菜单
 */
const addTopNode = () => {
  addTreeNode({
    parentId: '0'
  })
}
/**
 * 添加子菜单
 */
const addChildNode = (parentId: string) => {
  // addTreeNode({
  //   parentId
  // })
  console.log(parentId)
}
</script>
<template>
  <el-space :fill="true" wrap class="tree-container">
    <div class="tree-header">
      <el-button type="primary" :icon="Plus" @click="addTopNode">添加顶级菜单</el-button>
      <el-button type="danger" :icon="Delete" @click="removeTreeNode">删除菜单</el-button>
    </div>
    <div class="tree-body">
      <div class="tree-body__search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
      </div>
      <el-tree
        class="custom-tree"
        ref="treeRef"
        :data="treeDataList"
        :props="defaultProps"
        draggable
        show-checkbox
        node-key="id"
        default-expand-all
        :highlight-current="true"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        @check-change="checkChange"
        @node-click="nodeClick"
      >
        <template #default="{ node }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <el-icon class="plus-icon" @click.stop="addChildNode(node.data.id)">
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

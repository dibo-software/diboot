<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import useTree from '../hooks/tree'
import useScrollbarHeight from '../hooks/scrollbarHeight'
import type { ResourcePermission } from '../type'

const treeProps = {
  label: 'displayName'
}
const boxHeight = inject<number>('boxHeight', 0)
const emit = defineEmits<{
  (e: 'click-node', resourcePermission: ResourcePermission): void
}>()
const {
  checkChange,
  filterNode,
  getTree,
  addTreeNode,
  setSelectNode,
  removeTreeNode,
  nodeClick,
  treeRef,
  searchWord,
  treeDataList,
  loading
} = useTree<ResourcePermission>({
  baseApi: '/resourcePermission',
  treeApi: '/getMenuTreeList',
  transformField: treeProps,
  clickNodeCallback(nodeData) {
    emit('click-node', nodeData)
  }
})
const { height, computedFixedHeight } = useScrollbarHeight({
  boxHeight,
  fixedBoxSelectors: ['.tree-container>.el-space__item:first-child'],
  extraHeight: 30
})
// 初始化tree数据
getTree().then(() => {
  setSelectNode()
  computedFixedHeight()
})
/**
 * 添加顶级菜单
 */
const addTopNode = () => {
  addTreeNode({
    parentId: '0',
    displayType: 'MENU',
    status: 'A',
    routeMeta: {}
  })
}
/**
 * 添加子菜单
 */
const addChildNode = (parentId: string) => {
  addTreeNode({
    parentId: parentId,
    displayType: 'MENU',
    status: 'A',
    routeMeta: {}
  })
}
</script>
<template>
  <el-skeleton v-if="loading" :rows="5" animated />
  <el-space v-else :fill="true" wrap class="tree-container">
    <div class="tree-header">
      <div>
        <el-button type="primary" :icon="Plus" @click="addTopNode">添加顶级菜单</el-button>
        <el-button type="danger" :icon="Delete" @click="removeTreeNode">删除菜单</el-button>
      </div>
      <div class="tree-header__search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
      </div>
    </div>
    <el-scrollbar :height="height">
      <el-tree
        ref="treeRef"
        class="custom-tree"
        :data="treeDataList"
        :props="treeProps"
        :check-strictly="true"
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
    </el-scrollbar>
  </el-space>
</template>

<style scoped lang="scss">
.tree-container {
  padding: 10px 0 10px 5px;
  .tree-header {
    &__search {
      margin-top: 5px;
    }
  }
}
.custom-tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
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
  checkStrictlyChange,
  filterNode,
  getTree,
  addTreeNode,
  setSelectNode,
  removeSingleTreeNode,
  nodeClick,
  treeRef,
  searchWord,
  treeDataList,
  loading
} = useTreeCrud<ResourcePermission>({
  baseApi: '/resourcePermission',
  treeApi: '/menuTree',
  sortApi: '/sortTree',
  transformField: treeProps,
  clickNodeCallback(nodeData) {
    emit('click-node', nodeData)
  }
})
const { height, computedFixedHeight } = useScrollbarHeight({
  boxHeight,
  fixedBoxSelectors: ['.tree-container>.el-space>.el-space__item:first-child', '.btn-fixed'],
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
  <div class="tree-container">
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-space v-else :fill="true" wrap>
      <div class="tree-search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
      </div>
      <el-scrollbar :height="height">
        <el-tree
          ref="treeRef"
          class="custom-tree"
          :data="treeDataList"
          :props="treeProps"
          :check-strictly="true"
          draggable
          node-key="id"
          default-expand-all
          :highlight-current="true"
          :expand-on-click-node="false"
          :filter-node-method="filterNode"
          @check-change="checkStrictlyChange"
          @node-click="nodeClick"
        >
          <template #default="{ node }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span v-has-permission="['create', 'delete']" class="icon-container">
                <el-icon
                  v-has-permission="'create'"
                  class="plus-icon custom-icon"
                  @click.stop="addChildNode(node.data.id)"
                >
                  <icon name="Plus" />
                </el-icon>
                <el-icon
                  v-has-permission="'delete'"
                  class="delete-icon custom-icon"
                  @click.stop="removeSingleTreeNode(node.data.id)"
                >
                  <icon name="Delete" />
                </el-icon>
              </span>
            </span>
          </template>
        </el-tree>
      </el-scrollbar>
    </el-space>
    <div v-has-permission="'create'" class="is-fixed btn-fixed">
      <el-button style="width: 100%" type="primary" :icon="Plus" @click="addTopNode">添加顶级菜单</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tree-container {
  position: relative;
  height: 100%;
  .tree-search {
    padding: 5px 5px 0;
  }
  .is-fixed {
    box-sizing: border-box;
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 39px;
    border-top: 1px solid var(--el-border-color-lighter);
    padding: 5px 16px;
    background: var(--el-bg-color);
    text-align: center;
    z-index: 1;
  }
}
.custom-tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--el-font-size-base);
  padding-right: 10px;
  &:hover {
    .custom-icon {
      display: inline-block;
    }
    .plus-icon {
      background-color: var(--el-color-primary);
    }
    .delete-icon {
      background-color: var(--el-color-error);
    }
    .custom-icon + .custom-icon {
      margin-left: 5px;
    }
  }
  .custom-icon {
    display: none;
    color: white;
    padding: 2px;
    border-radius: 50%;
    &:hover {
      transition: background-color 0.3s;
    }
  }
  .plus-icon {
    &:hover {
      background-color: var(--el-color-primary-light-3);
    }
  }
  .delete-icon {
    &:hover {
      background-color: var(--el-color-error-light-3);
    }
  }
}
</style>

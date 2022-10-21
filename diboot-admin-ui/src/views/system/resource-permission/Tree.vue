<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import type { ElTreeInstanceType } from 'element-plus'
import type { ResourcePermission } from './type'
import { WatchStopHandle } from 'vue'

const baseApi = '/iam/resource-permission'

// tree实例
const treeRef = ref<ElTreeInstanceType>()

const { getList, dataList, loading, remove } = useList<ResourcePermission>({
  baseApi,
  listApi: `${baseApi}/menu-tree`
})

const setTreeCurrentKey = (key: string) => {
  const watchTreeRef: WatchStopHandle = watch(
    treeRef,
    value => {
      if (value) {
        value.setCurrentKey(key)
        const currentNode = value.getCurrentNode()
        if (currentNode) {
          clickNode(currentNode as ResourcePermission)
        } else if (dataList.length) {
          value.setCurrentKey(dataList[0].id as string)
          clickNode(value.getCurrentNode() as ResourcePermission)
        }
        watchTreeRef()
      }
    },
    { immediate: true }
  )
}

getList().then(() => {
  if (dataList.length) {
    setTreeCurrentKey(dataList[0].id as string)
  }
})

const removeData = (id: string) => {
  const currentKey = treeRef.value?.getCurrentKey()
  remove(id).then(result => {
    if (result) setTreeCurrentKey(currentKey as string)
  })
}

const emit = defineEmits<{
  (e: 'click-node', resource: ResourcePermission): void
}>()

const clickNode = (data: ResourcePermission) => {
  emit('click-node', data)
}

// 搜索值
const searchWord = ref('')
//监听searchWord变化
watch(searchWord, val => {
  treeRef.value?.filter(val)
})
const filterNode = (value: string, data: Partial<ResourcePermission>) => !value || data.displayName?.includes(value)

/**
 * 添加子菜单
 */
const addChildNode = (parent?: ResourcePermission) => {
  clickNode({
    parentId: parent?.id ?? '0',
    parentDisplayName: parent?.displayName,
    displayType: parent ? 'MENU' : 'CATALOGUE',
    displayName: '新建',
    resourceCode: '',
    sortId: '0',
    status: 'A',
    routeMeta: {}
  })
}

defineExpose({
  refresh: async (id: string) => {
    await getList()
    setTreeCurrentKey(id)
  }
})

const { nodeDrag } = useSort<Required<ResourcePermission>>({
  sortApi: `${baseApi}/sort`,
  callback: async () => {
    const currentKey = treeRef.value?.getCurrentKey()
    await getList()
    setTreeCurrentKey(currentKey)
  }
})
</script>
<template>
  <div class="tree-container">
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-space v-else :fill="true" wrap>
      <div class="tree-search">
        <el-input v-model="searchWord" placeholder="请输入内容过滤" clearable :prefix-icon="Search" />
      </div>
      <el-scrollbar :height="checkPermission('create') ? 'calc(100vh - 175px)' : 'calc(100vh - 139px)'">
        <el-tree
          ref="treeRef"
          :data="dataList"
          :props="{ label: 'displayName' }"
          draggable
          node-key="id"
          highlight-current
          default-expand-all
          :expand-on-click-node="false"
          :filter-node-method="filterNode"
          @node-click="clickNode"
          @node-drop="nodeDrag"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span v-has-permission="['create', 'delete']" class="icon-container">
                <el-icon
                  v-show="data.displayType === 'CATALOGUE'"
                  v-has-permission="'create'"
                  class="plus-icon"
                  @click.stop="addChildNode(data)"
                >
                  <Plus />
                </el-icon>
                <el-icon
                  v-show="(data.children ?? []).length === 0"
                  v-has-permission="'delete'"
                  class="delete-icon"
                  @click.stop="removeData(data.id)"
                >
                  <Delete />
                </el-icon>
              </span>
            </span>
          </template>
        </el-tree>
      </el-scrollbar>
    </el-space>
    <div v-has-permission="'create'" class="is-fixed">
      <el-button style="width: 100%" type="primary" :icon="Plus" @click="addChildNode()">添加顶级菜单</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tree-container {
  height: 100%;
  position: relative;
  border-right: 1px solid var(--el-border-color-lighter);

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
  }

  .el-tree :deep(.el-tree-node__label) {
    flex: 1;

    .custom-tree-node {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: var(--el-font-size-base);

      .el-icon {
        display: none;
        color: white;
        padding: 2px;
        margin-right: 8px;
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

    &:hover {
      .el-icon {
        display: inline-block;
      }

      .plus-icon {
        background-color: var(--el-color-primary);
      }

      .delete-icon {
        background-color: var(--el-color-error);
      }
    }
  }
}
</style>

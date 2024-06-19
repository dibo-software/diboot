<script setup lang="ts">
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import type { ElTreeInstanceType } from 'element-plus'
import type { Resource } from './type'
import type { WatchStopHandle } from 'vue'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/resource'

// tree实例
const treeRef = ref<ElTreeInstanceType>()

const { getList, dataList, loading, remove } = useList<Resource>({
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
          clickNode(currentNode as Resource)
        } else if (dataList.length) {
          value.setCurrentKey(dataList[0].id as string)
          clickNode(value.getCurrentNode() as Resource)
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
  (e: 'click-node', resource: Resource): void
}>()

const clickNode = (data: Resource) => {
  emit('click-node', data)
}

// 搜索值
const searchWord = ref('')
//监听searchWord变化
watch(searchWord, val => {
  treeRef.value?.filter(val)
})
const filterNode = (value: string, data: Record<string, any>) => !value || data.displayName?.includes(value)

/**
 * 添加子菜单
 */
const addChildNode = (parent?: Resource) => {
  treeRef.value?.setCurrentKey()
  const children = parent ? parent.children ?? [] : dataList
  clickNode({
    parentId: parent?.id ?? '0',
    parentDisplayName: parent?.displayName,
    displayType: parent ? 'MENU' : 'CATALOGUE',
    displayName: i18n.t('operation.create'),
    resourceCode: '',
    sortId: (children?.length ? Number(children[children.length - 1].sortId ?? children.length) : 0) + 1 + '',
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

const { nodeDrag } = useSort({
  sortApi: `${baseApi}/sort`,
  callback: async () => {
    const currentKey = treeRef.value?.getCurrentKey()
    await getList()
    setTreeCurrentKey(currentKey)
  }
})

const treeNodeClass = (data: Record<string, any>) => {
  if (data.status !== 'A') {
    // 非可用
    return 'through'
  } else if (data.routeMeta?.hidden) {
    // 隐藏
    return 'hidden'
  }
  return {}
}
</script>

<template>
  <div class="tree-container">
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-space v-else :fill="true" wrap style="width: 100%">
      <div class="tree-search">
        <el-button v-has-permission="'create'" :icon="Plus" style="margin-right: 2px" @click="addChildNode()">
          {{ $t('resource.displayTypeOptions.catalogue') }}
        </el-button>
        <el-input v-model="searchWord" :placeholder="$t('placeholder.filter')" clearable :prefix-icon="Search" />
      </div>
      <el-scrollbar height="calc(100vh - 139px)">
        <el-tree
          ref="treeRef"
          :data="dataList"
          :props="{ label: 'displayName', class: treeNodeClass }"
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
              <span class="content">
                <el-icon v-if="data.routeMeta.icon" style="margin-right: 5px">
                  <Icon :name="data.routeMeta.icon" />
                </el-icon>
                <span :style="data.routeMeta.icon ? '' : 'margin-left: 19px'">{{ node.label }}</span>
              </span>
              <span class="operation-container">
                <el-tooltip :show-after="1000" :content="$t('resource.addChild')">
                  <el-button
                    v-show="data.displayType === 'CATALOGUE'"
                    v-has-permission="'create'"
                    :icon="Plus"
                    type="success"
                    link
                    @click.stop="addChildNode(data)"
                  />
                </el-tooltip>
                <el-tooltip :show-after="1000" :content="$t('operation.delete')">
                  <el-button
                    v-show="(data.children ?? []).length === 0"
                    v-has-permission="'delete'"
                    :icon="Delete"
                    type="danger"
                    link
                    @click.stop="removeData(data.id)"
                  />
                </el-tooltip>
              </span>
            </span>
          </template>
        </el-tree>
      </el-scrollbar>
    </el-space>
  </div>
</template>

<style scoped lang="scss">
.tree-container {
  height: 100%;
  position: relative;
  border-right: 1px solid var(--el-border-color-lighter);
  .custom-tree-node {
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .tree-search {
    display: flex;
    padding: 5px 5px 0;
  }

  .hidden,
  .through {
    .content {
      color: var(--el-color-info-light-3);
    }
  }

  .through {
    .content {
      text-decoration: line-through;
    }
  }

  .el-tree :deep(.el-tree-node__label) {
    flex: 1;
  }
}
</style>

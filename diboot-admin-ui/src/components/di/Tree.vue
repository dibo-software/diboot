<script setup lang="ts" name="DiTree">
import { Search } from '@element-plus/icons-vue'
import type { TreeConfig } from '@/components/di/type'
import type { ElTreeInstanceType } from 'element-plus'
import type { ConditionItem } from '@/hooks/use-option'

// vue语法限制导致只能在当前文件中再次定义 Props
// https://cn.vuejs.org/guide/typescript/composition-api.html#typing-component-props
interface TreeProps extends /* @vue-ignore */ TreeConfig {
  type: string
  label: string
  ext?: string
  orderBy?: string
  parent: string
  parentPath?: string
  lazyChild?: boolean
  conditions?: Array<ConditionItem>
  sortApi?: string
}

const props = withDefaults(defineProps<TreeProps>(), {
  ext: undefined,
  orderBy: undefined,
  parentPath: undefined,
  lazyChild: true,
  conditions: undefined,
  sortApi: undefined
})

const treeDataKey = 'treeData'

const { relatedData, asyncLoading, remoteRelatedDataFilter, lazyLoadRelatedData } = useOption({
  asyncLoad: { [treeDataKey]: props }
})

const initTreeData = async () => (relatedData[treeDataKey] = await lazyLoadRelatedData(treeDataKey))

if (!props.lazyChild) initTreeData()

const loadNodes = ({ data }: { data: Record<string, any> }, resolve: (options: Record<string, any>[]) => void) =>
  lazyLoadRelatedData(treeDataKey, data.value).then(lsit => {
    resolve(lsit)
    treeRef.value?.setCurrentKey(activateNode.value)
  })

const emit = defineEmits<{
  (e: 'clickNode', nodeKey?: string): void
  (e: 'changeOrder', nodeKey?: string): void
}>()

const searchValue = ref('')
const treeRef = ref<ElTreeInstanceType>()
//监听search变化
watch(searchValue, val => {
  if (!props.lazyChild) {
    treeRef.value?.filter(val)
  } else if (props.parentPath)
    remoteRelatedDataFilter(treeDataKey, val).then(() => treeRef.value?.setCurrentKey(activateNode.value))
})

const filterNode = (value: string, data: Record<string, any>) => !value || data.label?.includes(value)

const activateNode = ref<string>()
const clickNode = (data: LabelValue) => {
  if (activateNode.value === data.value) {
    treeRef.value?.setCurrentKey()
    emit('clickNode', (activateNode.value = undefined))
  } else {
    emit('clickNode', (activateNode.value = data.value))
  }
}

const treeKey = ref(0)
const refresh = () => {
  if (props.lazyChild) treeKey.value = +new Date()
  else initTreeData().then(() => treeRef.value?.setCurrentKey(activateNode.value))
}

defineExpose({ refresh })

const { nodeDrag } = useSort({
  sortApi: `${props.sortApi}`,
  callback: () => {
    emit('changeOrder')
    refresh()
  },
  idKey: 'value',
  sortIdKey: 'ext'
})
</script>

<template>
  <div class="tree-container">
    <div class="top">
      <slot name="top" />
      <el-input
        v-if="!lazyChild || !!parentPath"
        v-model.lazy.trim="searchValue"
        :prefix-icon="Search"
        :placeholder="$t('components.di.tree.searchFilter')"
        clearable
      />
    </div>
    <div :style="lazyChild === !!parentPath ? { height: 'calc(100% - 48px)' } : {}">
      <el-skeleton v-if="asyncLoading" :rows="5" animated />
      <el-scrollbar v-show="!asyncLoading">
        <el-tree
          :key="lazyChild ? searchValue + treeKey : 0"
          ref="treeRef"
          node-key="value"
          :data="relatedData[treeDataKey]"
          :lazy="lazyChild && !searchValue"
          :load="loadNodes"
          highlight-current
          :draggable="!!sortApi && !!ext"
          :expand-on-click-node="false"
          :filter-node-method="filterNode"
          :default-expand-all="!lazyChild || !!searchValue"
          @node-click="clickNode"
          @node-drop="nodeDrag"
        >
          <template #default="{ node, data }">
            <slot v-bind="{ node, data }">
              {{ node.label }}
            </slot>
          </template>
        </el-tree>
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tree-container {
  height: 100%;
  width: 240px;
  border-right: 1px solid var(--el-border-color-lighter);

  .top {
    display: flex;
    padding: 8px;
  }

  .el-tree :deep(.el-tree-node__label) {
    flex: 1;
  }
}
</style>

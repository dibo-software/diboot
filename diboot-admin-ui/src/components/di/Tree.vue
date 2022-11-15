<script setup lang="ts" name="DiTree">
import { Search } from '@element-plus/icons-vue'
import type { TreeConfig } from '@/components/di/type'
import type { ElTreeInstanceType } from 'element-plus'

// vue语法限制导致只能在当前文件中再次定义 Props
// https://cn.vuejs.org/guide/typescript/composition-api.html#typing-component-props
interface TreeProps extends TreeConfig {
  type: string
  label: string
  ext?: string
  orderBy?: string
  parent: string
  parentPath?: string
  lazyChild?: boolean
  condition?: Record<string, boolean | string | number | (string | number)[] | null>
  sortApi?: string
}

const props = withDefaults(defineProps<TreeProps>(), {
  ext: undefined,
  orderBy: undefined,
  parentPath: undefined,
  lazyChild: true,
  condition: undefined,
  sortApi: undefined
})

const treeDataKey = 'treeData'

const { initRelatedData, relatedData, initLoading, remoteRelatedDataFilter, lazyLoadRelatedData } = useOption({
  load: { [treeDataKey]: props },
  asyncLoad: { [treeDataKey]: props }
})

if (!props.lazyChild) initRelatedData()

const loadNodes = async ({ data }: { data: LabelValue }, resolve: (options: LabelValue[]) => void) =>
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
    remoteRelatedDataFilter(val, treeDataKey).then(() => treeRef.value?.setCurrentKey(activateNode.value))
})

const filterNode = (value: string, data: Partial<LabelValue>) => !value || data.label?.includes(value)

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
  else initRelatedData().then(() => treeRef.value?.setCurrentKey(activateNode.value))
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
    <el-input
      v-if="lazyChild === !!parentPath || !lazyChild"
      v-model="searchValue"
      :prefix-icon="Search"
      placeholder="搜索过滤"
      clearable
    />
    <div :style="lazyChild === !!parentPath ? { height: 'calc(100% - 48px)' } : {}">
      <el-skeleton v-if="initLoading" :rows="5" animated />
      <el-scrollbar v-show="!initLoading">
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
        />
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tree-container {
  height: 100%;
  width: 300px;
  border-right: 1px solid var(--el-border-color-lighter);

  .el-input {
    padding: 8px;
  }
}
</style>

<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import type { OrgModel } from '@/views/org-structure/org/type'
import type { ElTreeInstanceType } from 'element-plus'
import type { WatchStopHandle } from 'vue'

const baseApi = '/iam/org'

defineProps<{ draggable?: boolean }>()

// tree实例
const treeRef = ref<ElTreeInstanceType>()

const { getList, dataList, loading } = useList<OrgModel>({
  baseApi,
  listApi: `${baseApi}/tree`
})

getList()

// 搜索值
const searchWord = ref('')
//监听searchWord变化
watch(searchWord, val => {
  treeRef.value?.filter(val)
})
const filterNode = (value: string, data: Partial<OrgModel>) => !value || data.shortName?.includes(value)

const emit = defineEmits<{
  (e: 'clickNode', currentKey?: string): void
}>()

const currentKey = ref()
const clickNode = (data: OrgModel) => {
  if (currentKey.value === data.id) {
    treeRef.value?.setCurrentKey()
    emit('clickNode', (currentKey.value = undefined))
  } else {
    emit('clickNode', (currentKey.value = data.id))
  }
}

const setTreeCurrentKey = (key: string) => {
  const watchTreeRef: WatchStopHandle = watch(
    treeRef,
    value => {
      if (value) {
        value.setCurrentKey(key)
        watchTreeRef()
      }
    },
    { immediate: true }
  )
}

defineExpose({
  reload: async () => {
    await getList()
    setTreeCurrentKey(currentKey.value)
  }
})

const { nodeDrag } = useSort<Required<OrgModel>>({
  sortApi: `${baseApi}/sort`,
  callback: async () => {
    const currentKey = treeRef.value?.getCurrentKey()
    await getList()
    setTreeCurrentKey(currentKey)
  }
})
</script>

<template>
  <div class="full-height-container">
    <el-header>
      <el-input v-model="searchWord" placeholder="请输入内容过滤" :prefix-icon="Search" />
    </el-header>
    <el-scrollbar>
      <el-tree
        ref="treeRef"
        v-loading="loading"
        node-key="id"
        :draggable="draggable"
        :default-expand-all="true"
        :highlight-current="true"
        :expand-on-click-node="false"
        :props="{ label: 'shortName' }"
        :data="dataList"
        :check-strictly="true"
        :filter-node-method="filterNode"
        @node-click="clickNode"
        @node-drop="nodeDrag"
      />
    </el-scrollbar>
  </div>
</template>

<style lang="scss" scoped>
.el-header {
  height: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color);
}
</style>

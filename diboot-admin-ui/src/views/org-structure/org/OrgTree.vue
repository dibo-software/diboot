<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import type { OrgModel } from '@/views/org-structure/org/type'
import type { ElTreeInstanceType } from 'element-plus'

const baseApi = '/iam/org'

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
const filterNode = (value: string, data: Record<string, any>) => !value || data.name?.includes(value)

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

defineExpose({
  reload: async () => {
    await getList()
    treeRef.value?.setCurrentKey(currentKey.value)
  }
})
</script>

<template>
  <div class="full-height-container">
    <el-header>
      <el-input v-model="searchWord" :placeholder="$t('placeholder.filter')" :prefix-icon="Search" />
    </el-header>
    <el-scrollbar>
      <el-tree
        ref="treeRef"
        v-loading="loading"
        node-key="id"
        :default-expand-all="true"
        :highlight-current="true"
        :expand-on-click-node="false"
        :props="{ label: 'name' }"
        :data="dataList"
        :check-strictly="true"
        :filter-node-method="filterNode"
        @node-click="clickNode"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node" :title="data.type === 'COMP' ? '公司' : '部门'">
            <icon v-if="data.type === 'COMP'" name="Local:Company" />
            <icon v-else name="Local:Department" />
            <span>{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </el-scrollbar>
  </div>
</template>

<style lang="scss" scoped>
.el-header {
  height: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color);
}

.custom-tree-node {
  display: flex;
  align-items: center;

  i {
    margin-right: 3px;
  }
}
</style>

<script setup lang="ts" name="FileRecord">
import { Search } from '@element-plus/icons-vue'
import Form from './Form.vue'
import type { SystemConfig } from '@/views/system/config/type'

defineProps<{ categoryList: string[] }>()

const { queryParam, onSearch, resetFilter, getList, loading, dataList, remove } = useList<SystemConfig>({
  baseApi: '/system-config',
  deleteCallback: () => refresh()
})

getList()

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const emit = defineEmits<{
  (e: 'refresh'): void
}>()

const refresh = () => {
  getList()
  emit('refresh')
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-select v-model="queryParam.category" filterable clearable @change="onSearch">
          <el-option v-for="item in categoryList" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button :icon="Search" type="primary" @click="onSearch">查询</el-button>
        <el-button title="重置搜索条件" @click="resetFilter">重置</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="propKey" label="属性名" show-overflow-tooltip />
      <el-table-column prop="propValue" label="属性值" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="185" />
      <el-table-column prop="updateTime" label="更新时间" width="185" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.id)">
              编辑
            </el-button>
            <el-button v-has-permission="'delete'" text bg type="danger" size="small" @click="remove(row.id)">
              删除
            </el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <Form ref="formRef" @complete="refresh" />
  </div>
</template>

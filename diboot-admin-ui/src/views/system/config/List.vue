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
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="propKey" :label="$t('config.propKey')" show-overflow-tooltip />
      <el-table-column prop="propValue" :label="$t('config.propValue')" show-overflow-tooltip />
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" width="185" />
      <el-table-column prop="updateTime" :label="$t('baseField.updateTime')" width="185" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.id)">
              {{ $t('operation.update') }}
            </el-button>
            <el-button v-has-permission="'delete'" text bg type="danger" size="small" @click="remove(row.id)">
              {{ $t('operation.delete') }}
            </el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <Form ref="formRef" @complete="refresh" />
  </div>
</template>

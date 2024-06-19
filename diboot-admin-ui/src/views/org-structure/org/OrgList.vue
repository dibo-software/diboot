<script setup name="OrgList" lang="ts">
import type { OrgModel } from '@/views/org-structure/org/type'
import { Search, Plus } from '@element-plus/icons-vue'
import OrgForm from './Form.vue'

const props = defineProps<{ parentId?: string }>()

const emit = defineEmits<{
  (e: 'reload'): void
}>()

interface OrgSearch extends OrgModel {
  keywords?: string
}
const { queryParam, onSearch, getList, loading, dataList, pagination, remove, resetFilter } = useList<
  OrgModel,
  OrgSearch
>({
  baseApi: '/iam/org',
  deleteCallback() {
    getList()
    emit('reload')
  }
})

watch(
  () => props.parentId,
  val => {
    queryParam.parentId = val
    onSearch()
  },
  { immediate: true }
)

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const onFormComplete = () => {
  getList()
  emit('reload')
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.name" clearable :placeholder="$t('org.name')" @change="onSearch" />
        <el-input v-model="queryParam.code" clearable :placeholder="$t('org.code')" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table
      ref="tableRef"
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children__' }"
      :data="dataList"
      stripe
      height="100%"
    >
      <el-table-column prop="name" :label="$t('org.name')" />
      <el-table-column prop="code" :label="$t('org.code')" />
      <el-table-column prop="managerName" :label="$t('org.managerName')" />
      <el-table-column prop="sortId" :label="$t('org.sortId')" />
      <el-table-column prop="updateTime" :label="$t('baseField.updateTime')" width="185" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.id)">
            {{ $t('operation.update') }}
          </el-button>
          <el-button v-has-permission="'delete'" text bg type="danger" size="small" @click="remove(row.id)">
            {{ $t('operation.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="pagination.total"
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 15, 20, 30, 50, 100]"
      small
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />
    <org-form
      ref="formRef"
      :parent-id="props.parentId"
      :siblings-number="pagination.total"
      @complete="onFormComplete"
    />
  </div>
</template>

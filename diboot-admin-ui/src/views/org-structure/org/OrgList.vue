<script setup name="OrgList" lang="ts">
import type { OrgModel } from '@/views/org-structure/org/type'
import { Search, Refresh } from '@element-plus/icons-vue'
import OrgForm from './Form.vue'

const props = defineProps<{ parentId?: string }>()

const emit = defineEmits<{
  (e: 'reload'): void
}>()

interface OrgSearch extends OrgModel {
  keywords?: string
}
const { queryParam, onSearch, getList, loading, dataList, pagination, remove } = useList<OrgModel, OrgSearch>({
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

getList()

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
    <el-header>
      <el-space wrap class="list-operation">
        <el-button v-has-permission="'create'" type="primary" @click="openForm()">新建</el-button>
        <el-space>
          <el-input
            v-model="queryParam.keywords"
            class="search-input"
            placeholder="编码/名称"
            clearable
            :suffix-icon="Search"
            @keyup.enter="onSearch"
          />
          <el-button :icon="Refresh" circle @click="getList()" />
        </el-space>
      </el-space>
    </el-header>
    <el-table
      ref="tableRef"
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children__' }"
      :data="dataList"
      stripe
      height="100%"
    >
      <el-table-column prop="name" label="全称" />
      <el-table-column prop="shortName" label="简称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="updateTime" label="更新时间" width="185" />
      <el-table-column label="操作" width="160" fixed="right">
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
      v-model:currentPage="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 30, 50, 100]"
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

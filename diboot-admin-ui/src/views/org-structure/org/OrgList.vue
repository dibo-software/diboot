<script setup name="OrgList" lang="ts">
import type { OrgModel } from '@/views/org-structure/org/type'
import { Search, CircleClose, ArrowDown, ArrowUp, Plus } from '@element-plus/icons-vue'
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

// 搜索区折叠
const searchState = ref(false)

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
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :lg="8" :sm="12">
          <el-form-item label="名称">
            <el-input v-model="queryParam.name" clearable placeholder="" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="8" :sm="12">
          <el-form-item label="编码">
            <el-input v-model="queryParam.code" clearable placeholder="" @change="onSearch" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-show="!searchState" v-model="queryParam.name" clearable placeholder="名称" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
        <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
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
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="managerName" label="负责人" />
      <el-table-column prop="sortId" label="排序号" />
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

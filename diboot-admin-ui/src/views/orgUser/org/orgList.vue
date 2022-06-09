<script setup name="OrgList" lang="ts">
import type { OrgModel } from '@/views/orgUser/org/type'
import { Search, Refresh } from '@element-plus/icons-vue'
import OrgForm from './form.vue'

type Props = {
  parentId?: string
}
const props = withDefaults(defineProps<Props>(), {
  parentId: '0'
})

const emit = defineEmits(['reload'])

interface OrgSearch extends OrgModel {
  keywords?: string
}
const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination, remove, batchRemove } =
  useListDefault<OrgModel, OrgSearch>({
    baseApi: '/org',
    deleteCallback() {
      emit('reload')
    }
  })
getList()
const searchVal = ref('')
const onSearchValChanged = (val: string) => {
  queryParam.keywords = val
  onSearch()
}

watch(
  () => props.parentId,
  val => {
    queryParam.parentId = val
    onSearch()
  }
)

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const onFormComplete = (id?: string) => {
  if (id) {
    getList()
  } else {
    onSearch()
  }
  emit('reload')
}

defineExpose({ onSearch })
</script>
<template>
  <div class="table-page">
    <el-space wrap class="list-operation">
      <el-button type="primary" @click="openForm()">新建</el-button>
      <el-space>
        <el-input
          v-model="searchVal"
          class="search-input"
          placeholder="编码/名称"
          clearable
          :suffix-icon="Search"
          @change="onSearchValChanged"
        />
        <el-button :icon="Refresh" circle @click="getList()" />
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
      <el-table-column prop="name" label="全称" />
      <el-table-column prop="shortName" label="简称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openForm(row.id)">编辑</el-button>
          <el-button text bg type="danger" size="small" @click="remove(row.id)">删除</el-button>
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
    <org-form ref="formRef" :parent-id="props.parentId" @complete="onFormComplete" />
  </div>
</template>
<style lang="scss" scoped></style>

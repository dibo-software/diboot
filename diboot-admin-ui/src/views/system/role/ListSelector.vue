<script setup lang="ts">
import type { Role } from './type'
import { defineEmits, defineProps } from 'vue'
import useListSelector from '@/hooks/use-list-selector'
import type { ElTable } from 'element-plus'

type Props = {
  selectedKeys: string[]
  selectedRows: Role[]
  multi?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  selectedKeys: () => [],
  selectedRows: () => [],
  multi: false
})
const emit = defineEmits(['update:selectedKeys', 'update:selectedRows', 'select'])

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination, remove, batchRemove } =
  useList<Role>({
    baseApi: '/iam/role',
    loadSuccess: () => {
      setCheckedKeys()
    }
  })

// 获取列表结束后，自定设置选中行
getList()

const { rowSelectChangeHandler } = useListSelector<Role>({
  multi: props.multi
})

const onSelectionChange = (selectedRows: Role[], single: boolean) => {
  rowSelectChange(selectedRows, single, dataList)
}
const getSingleRow = (row: Role) => {
  onSelectionChange([row], true)
}

const rowSelectChange = (selectedRows: Role[], single: boolean, dataList: Role[]) => {
  const { allSelectedKeys, allSelectedRows } = rowSelectChangeHandler(
    selectedRows,
    props.selectedRows,
    single,
    dataList
  )
  emit('update:selectedKeys', allSelectedKeys)
  emit('update:selectedRows', allSelectedRows)
  // 发送选中的数据对象列表
  emit('select', allSelectedRows)
}

const single = ref('')
const tableRef = ref<InstanceType<typeof ElTable>>()
const setCheckedKeys = () => {
  const { multi, selectedKeys } = props
  if (!dataList || !selectedKeys) {
    return false
  }
  if (multi) {
    dataList.forEach(item => {
      if (selectedKeys.includes(item.id)) {
        tableRef.value?.toggleRowSelection(item, true)
      }
    })
  } else {
    if (selectedKeys.length > 0) {
      single.value = selectedKeys[0]
    }
  }
}
</script>

<template>
  <el-form label-width="80px" class="list-search" @submit.prevent>
    <el-row :gutter="18">
      <el-col :md="8" :sm="24">
        <el-form-item label="名称">
          <el-input v-model="queryParam.name" clearable @change="onSearch" />
        </el-form-item>
      </el-col>
      <el-col :md="8" :sm="24">
        <el-form-item label="编码">
          <el-input v-model="queryParam.code" clearable @change="onSearch" />
        </el-form-item>
      </el-col>
      <el-col :md="8" :sm="24" style="margin-left: auto">
        <el-form-item>
          <el-button type="primary" @click="onSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>

  <el-table
    ref="tableRef"
    v-loading="loading"
    :data="dataList"
    stripe
    height="calc(100% - 85px)"
    @selection-change="onSelectionChange"
  >
    <el-table-column v-if="multi" type="selection" width="55" />
    <el-table-column v-else width="30">
      <template #default="{ row }">
        <el-radio v-model="single" :label="row.id" @change="getSingleRow(row)" />
      </template>
    </el-table-column>
    <el-table-column prop="name" label="名称" />
    <el-table-column prop="code" label="编码" />
    <el-table-column prop="createTime" label="创建时间" width="165" />
  </el-table>
  <el-pagination
    v-if="pagination.total"
    v-model:currentPage="pagination.current"
    v-model:page-size="pagination.pageSize"
    :page-sizes="[10, 20, 30, 50, 100]"
    background
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
    @size-change="getList()"
    @current-change="getList()"
  />
</template>

<style lang="scss" scoped>
.list-search {
  padding-bottom: 10px;
}
</style>

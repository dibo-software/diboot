<script setup lang="ts" name="RoleListSelector">
import type { Role } from './type'
import { defineEmits, defineProps } from 'vue'
import { ElTable } from 'element-plus'

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
  useListDefault<Role>({ baseApi: '/role' })

// 获取列表结束后，自定设置选中行
getList().then(() => {
  setCheckedKeys(dataList)
})

const keyName = 'id'
const onSelectionChange = (selectedRows: Role[], single: boolean) => {
  rowSelectChange(selectedRows, single, dataList)
}
const getSingleRow = (row: Role) => {
  onSelectionChange([row], true)
}

const rowSelectChange = (selectedRows: Role[], single: boolean, dataList: Role[]) => {
  let allSelectedRows = selectedRows || []
  const selectedKeys = selectedRows.map(item => item[keyName])
  let allSelectedKeys = selectedKeys
  if (!single) {
    const { selectedRows: parentSelectedRows } = props
    // 合并已存在和当前选中数据列表
    const existIdList = parentSelectedRows.map(item => item[keyName])
    const allSelectedKeySet = new Set([...existIdList, ...allSelectedKeys])
    allSelectedKeys = Array.from(allSelectedKeySet)
    // 过滤当前页面已存在数据，却不在已选中数据的数据
    const currentPageKeys = dataList.map(item => item[keyName])
    allSelectedKeys = allSelectedKeys.filter(key => {
      return selectedKeys.includes(key) || !currentPageKeys.includes(key)
    })
    allSelectedRows = []
    for (let key of allSelectedKeys) {
      let row = selectedRows.find(item => item[keyName] === key)
      if (row === undefined) {
        row = parentSelectedRows.find(item => item[keyName] === key)
      }
      if (row !== undefined) {
        allSelectedRows.push(row)
      }
    }
  }
  emit('update:selectedKeys', allSelectedKeys)
  emit('update:selectedRows', allSelectedRows)
  // 发送选中的数据对象列表
  emit('select', allSelectedRows)
}

const single = ref('')
const tableRef = ref<InstanceType<typeof ElTable>>()
const setCheckedKeys = (dataList: Role[]) => {
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
    <el-table-column prop="createTime" label="创建时间" />
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

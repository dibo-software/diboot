<script setup lang="ts" name="PositionList">
import { Refresh, Search, ArrowDown } from '@element-plus/icons-vue'
import type { Position } from './type'
import Detail from './detail.vue'
import Form from './form.vue'
interface PositionSearch extends Position {
  keywords?: string
}

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination, remove, batchRemove } =
  useListDefault<Position, PositionSearch>({ baseApi: '/position' })

getList()

// 选中的数据 Id 集合
const selectedKeys = ref<string[]>([])

const searchVal = ref('')
const onSearchValChanged = (val: string) => {
  queryParam.keywords = val
  onSearch()
}

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}
</script>

<template>
  <div class="table-page">
    <el-space wrap class="list-operation">
      <el-button type="primary" @click="openForm()">新建</el-button>
      <el-button @click="batchRemove(selectedKeys)">批量删除</el-button>
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
      :data="dataList"
      stripe
      height="100%"
      @selection-change="(arr: Position[]) => (selectedKeys = arr.map((e: Position) => e.id))"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="gradeValue" label="职级" />
      <el-table-column prop="gradeName" label="职级头衔" />
      <el-table-column prop="dataPermissionTypeLabel" label="数据权限" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-space>
            <el-button text bg type="primary" size="small" @click="openDetail(row.id)">详情</el-button>
            <el-dropdown>
              <el-button text bg type="primary" size="small">
                更多
                <el-icon :size="16" style="margin-left: 5px">
                  <arrow-down />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="openForm(row.id)">编辑</el-dropdown-item>
                  <el-dropdown-item @click="remove(row.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
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

    <Detail ref="detailRef" />
    <Form ref="formRef" @complete="getList()" />
  </div>
</template>

<style scoped></style>

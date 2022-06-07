<script setup lang="ts" name="RoleList">
import { Refresh, Search, ArrowDown } from '@element-plus/icons-vue'
import type { Role } from './type'
import Detail from './detail.vue'
import Form from './form.vue'

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination, remove, batchRemove } =
  useListDefault<Role>({ baseApi: '/role' })

getList()

// 搜索区折叠
const searchState = ref(false)

// 选中的数据 Id 集合
const selectedKeys = ref<string[]>([])

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
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
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

    <el-space wrap class="list-operation">
      <el-button type="primary" @click="openForm()">新建</el-button>
      <el-button @click="batchRemove(selectedKeys)">批量删除</el-button>
      <el-space>
        <el-button :icon="Refresh" circle @click="getList()" />
        <el-button :icon="Search" circle @click="searchState = !searchState" />
      </el-space>
    </el-space>
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="dataList"
      stripe
      height="100%"
      @selection-change="(arr: Role[]) => (selectedKeys = arr.map(e => e.id))"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column prop="updateTime" label="更新时间" />
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

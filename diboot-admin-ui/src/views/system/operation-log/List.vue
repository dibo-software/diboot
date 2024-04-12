<script setup lang="ts" name="OperationLog">
import { Search, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import type { OperationLog } from './type'
import Detail from '@/views/system/operation-log/Detail.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter } = useList<OperationLog>({
  baseApi: '/iam/operation-log'
})
getList()

const tagMap = {
  GET: 'success',
  POST: void 0,
  PUT: 'warning',
  DELETE: 'danger',
  PATCH: 'info'
}

// 搜索区折叠
const searchState = ref(false)

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const getTagType = (val: string, map: Record<string, unknown>) => {
  return map[val as keyof typeof map] as 'success' | 'warning' | 'info' | 'primary' | 'danger' | undefined
}
</script>

<template>
  <div class="list-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :lg="6" :sm="12">
          <el-form-item label="业务对象">
            <el-input v-model="queryParam.businessObj" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :sm="12">
          <el-form-item label="请求方式">
            <el-select v-model="queryParam.requestMethod" clearable @change="onSearch">
              <el-option value="GET" />
              <el-option value="POST" />
              <el-option value="PUT" />
              <el-option value="DELETE" />
              <el-option value="PATCH" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :lg="6" :sm="12">
          <el-form-item label="状态码">
            <el-input v-model="queryParam.statusCode" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-space wrap class="list-operation">
      <el-space>
        <el-input
          v-show="!searchState"
          v-model="queryParam.businessObj"
          placeholder="业务对象"
          clearable
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">查询</el-button>
        <el-button title="重置搜索条件" @click="resetFilter">重置</el-button>
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="userType" label="用户类型" />
      <el-table-column prop="userRealname" label="用户姓名" />
      <el-table-column prop="businessObj" label="业务对象" />
      <el-table-column prop="operation" label="操作事项" />
      <el-table-column prop="requestMethod" label="请求方式">
        <template #default="{ row }">
          <el-tag :type="getTagType(row.requestMethod, tagMap)" effect="plain">{{ row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="requestUri" label="请求URI" show-overflow-tooltip />
      <el-table-column prop="statusCode" label="状态码">
        <template #default="{ row }">
          <el-tag v-if="row.statusCode === 0">{{ row.statusCode }}</el-tag>
          <el-tag v-else type="danger">{{ row.statusCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="165" />
      <el-table-column label="操作" width="70" fixed="right">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openDetail(row.id)">详情</el-button>
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

    <Detail ref="detailRef" />
  </div>
</template>

<style scoped></style>

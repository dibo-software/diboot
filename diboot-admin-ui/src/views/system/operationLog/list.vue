<script setup lang="ts" name="OperationLog">
import { Search, CircleClose, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import type { OperationLog } from './type'
import Detail from '@/views/system/operationLog/detail.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter } = useListDefault<OperationLog>({
  baseApi: '/operationLog'
})
getList()

const tagMap = {
  GET: 'success',
  POST: '',
  PUT: 'warning',
  DELETE: 'danger',
  PATCH: 'info'
}

const advanced = ref(false)

const detailRef = ref<InstanceType<typeof Detail>>()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}
</script>

<template>
  <div class="table-page">
    <el-form label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :md="6" :sm="12">
          <el-form-item label="业务对象">
            <el-input v-model="queryParam.businessObj" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="6" :sm="12">
          <el-form-item label="请求方式">
            <el-select v-model="queryParam.statusCode" clearable @change="onSearch">
              <el-option value="GET" />
              <el-option value="POST" />
              <el-option value="PUT" />
              <el-option value="DELETE" />
              <el-option value="PATCH" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="6" :sm="12">
          <el-form-item label="状态码">
            <el-input v-model="queryParam.statusCode" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <template v-if="advanced">
          <el-col :md="6" :sm="12">
            <el-form-item label="用户类型">
              <el-input v-model="queryParam.userType" clearable @change="onSearch" />
            </el-form-item>
          </el-col>
        </template>
        <el-col :md="6" :sm="12" style="margin-left: auto">
          <el-form-item>
            <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
            <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
            <el-button
              :icon="advanced ? ArrowUp : ArrowDown"
              :title="advanced ? '收起' : '展开'"
              @click="advanced = !advanced"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table ref="tableRef" v-loading="loading" :data="dataList" stripe height="100%">
      <el-table-column prop="userType" label="用户类型" />
      <el-table-column prop="userRealname" label="用户姓名" />
      <el-table-column prop="businessObj" label="业务对象" />
      <el-table-column prop="operation" label="操作事项" />
      <el-table-column prop="requestMethod" label="请求方式">
        <template #default="{ row }">
          <el-tag :type="tagMap[row.requestMethod]" effect="plain">{{ row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="requestUri" label="请求URI" show-overflow-tooltip />
      <el-table-column prop="statusCode" label="状态码">
        <template #default="{ row }">
          <el-tag v-if="row.statusCode === 0">{{ row.statusCode }}</el-tag>
          <el-tag v-else type="danger">{{ row.statusCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="70">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openDetail(row.id)">详情</el-button>
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
  </div>
</template>

<style scoped></style>

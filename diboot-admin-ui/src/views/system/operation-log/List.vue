<script setup lang="ts" name="OperationLog">
import { Search } from '@element-plus/icons-vue'
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
    <el-space wrap class="list-operation">
      <el-space>
        <el-input
          v-model="queryParam.businessObj"
          :placeholder="$t('operationLog.businessObj')"
          clearable
          @change="onSearch"
        />
        <el-select
          v-model="queryParam.requestMethod"
          clearable
          :placeholder="$t('operationLog.placeholder.requestMethod')"
          @change="onSearch"
        >
          <el-option value="GET" />
          <el-option value="POST" />
          <el-option value="PUT" />
          <el-option value="DELETE" />
          <el-option value="PATCH" />
        </el-select>
        <el-input
          v-model="queryParam.statusCode"
          :placeholder="$t('operationLog.statusCode')"
          clearable
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="userType" :label="$t('operationLog.userType')" />
      <el-table-column prop="userRealname" :label="$t('operationLog.userRealname')" />
      <el-table-column prop="businessObj" :label="$t('operationLog.businessObj')" />
      <el-table-column prop="operation" :label="$t('operationLog.operation')" />
      <el-table-column prop="requestMethod" :label="$t('operationLog.requestMethod')">
        <template #default="{ row }">
          <el-tag :type="getTagType(row.requestMethod, tagMap)" effect="plain">{{ row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="requestUri" :label="$t('operationLog.requestUri')" show-overflow-tooltip />
      <el-table-column prop="statusCode" :label="$t('operationLog.statusCode')">
        <template #default="{ row }">
          <el-tag v-if="row.statusCode === 0">{{ row.statusCode }}</el-tag>
          <el-tag v-else type="danger">{{ row.statusCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('operationLog.createTime')" width="165" />
      <el-table-column :label="$t('operation.label')" width="70" fixed="right">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openDetail(row.id)"
            >{{ $t('title.detail') }}
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

    <Detail ref="detailRef" />
  </div>
</template>

<style scoped></style>

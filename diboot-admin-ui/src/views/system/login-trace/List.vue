<script setup lang="ts" name="LoginTrace">
import { Search } from '@element-plus/icons-vue'
import type { LoginTrace } from './type'

const { queryParam, dateRangeQuery, loading, dataList, pagination, getList, onSearch, resetFilter } =
  useList<LoginTrace>({
    baseApi: '/iam/login-trace'
  })

getList()
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-space>
        <el-input
          v-model="queryParam.authAccount"
          :placeholder="$t('loginTrace.authAccount')"
          clearable
          @change="onSearch"
        />
        <el-select
          v-model="queryParam.isSuccess"
          :placeholder="$t('loginTrace.placeholder.successStatus')"
          clearable
          @change="onSearch"
        >
          <el-option :label="$t('loginTrace.successStatus.yes')" :value="true" />
          <el-option :label="$t('loginTrace.successStatus.yes')" :value="false" />
        </el-select>
        <date-range
          :start-placeholder="$t('loginTrace.placeholder.start')"
          :end-placeholder="$t('loginTrace.placeholder.end')"
          :model-value="dateRangeQuery.createTime as [string, string]"
          @update:model-value="dateRangeQuery.createTime = $event as [string, string]"
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column :label="$t('loginTrace.userTypeId')" width="260">
        <template #default="{ row }">
          <span>{{ row.userType }}:{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="authAccount" :label="$t('loginTrace.authAccount')" />
      <el-table-column prop="ipAddress" :label="$t('loginTrace.ipAddress')" />
      <el-table-column prop="authType" :label="$t('loginTrace.authType')" />
      <el-table-column prop="success" :label="$t('loginTrace.success')">
        <template #default="{ row }">
          <el-tag v-if="row.isSuccess">{{ $t('loginTrace.successStatus.yes') }}</el-tag>
          <el-tag v-else type="danger">{{ $t('loginTrace.successStatus.no') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('loginTrace.createTime')" />
      <el-table-column prop="logoutTime" :label="$t('loginTrace.logoutTime')" />
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
  </div>
</template>

<style scoped></style>

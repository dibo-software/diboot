<script setup lang="ts" name="LoginTrace">
import { Search, CircleClose } from '@element-plus/icons-vue'
import type { LoginTrace } from './type'

const { queryParam, dateRangeQuery, loading, dataList, pagination, getList, onSearch, resetFilter } =
  useListDefault<LoginTrace>({
    baseApi: '/loginTrace'
  })
getList()
</script>

<template>
  <div class="table-page">
    <el-form label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :md="5" :sm="12">
          <el-form-item label="用户名">
            <el-input v-model="queryParam.authAccount" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="5" :sm="12">
          <el-form-item label="登录状态">
            <el-select v-model="queryParam.success" clearable @change="onSearch">
              <el-option label="成功" :value="true" />
              <el-option label="失败" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="12">
          <el-form-item label="登录时间">
            <date-range v-model="dateRangeQuery.createTime" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="6" :sm="12" style="margin-left: auto">
          <el-form-item>
            <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
            <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table ref="tableRef" v-loading="loading" :data="dataList" stripe height="100%">
      <el-table-column label="用户标识" width="260">
        <template #default="{ row }">
          <span>{{ row.userType }}:{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="authAccount" label="用户名" />
      <el-table-column prop="ipAddress" label="登录IP" />
      <el-table-column prop="authType" label="登录方式" />
      <el-table-column prop="success" label="登录状态">
        <template #default="{ row }">
          <el-tag v-if="row.success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="登录时间" />
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
  </div>
</template>

<style scoped></style>

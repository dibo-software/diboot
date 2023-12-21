<script setup lang="ts" name="Tenant">
import { ArrowDown, ArrowUp, Plus, Search } from '@element-plus/icons-vue'
import type { Tenant } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'
import AdminForm from './AdminForm.vue'
import ResourceForm from './ResourceForm.vue'

interface TenantSearch extends Tenant {
  keywords?: string
}

const baseApi = '/iam/tenant'
const { queryParam, onSearch, getList, loading, dataList, buildQueryParam, pagination, remove, resetFilter } = useList<
  Tenant,
  TenantSearch
>({
  baseApi
})
getList()
// 搜索区折叠
const searchState = ref(false)

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}
const adminFormRef = ref()
const openAdminForm = (id: string) => {
  adminFormRef.value?.open(id)
}
const resourceFormRef = ref()
const openResourceForm = (id: string) => {
  resourceFormRef.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')
</script>

<template>
  <div class="list-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :lg="6" :sm="12">
          <el-form-item label="租户名称">
            <el-input v-model="queryParam.name" clearable placeholder="租户名称" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :sm="12">
          <el-form-item label="租户简称">
            <el-input v-model="queryParam.shortName" clearable placeholder="租户简称" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :sm="12">
          <el-form-item label="租户code">
            <el-input v-model="queryParam.code" clearable placeholder="租户code" @change="onSearch" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <excel-export
        v-has-permission="'export'"
        :build-param="buildQueryParam"
        :export-url="`${baseApi}/excel/export`"
        :table-head-url="`${baseApi}/excel/export-table-head`"
      />
      <excel-import v-has-permission="'import'" :excel-base-api="`${baseApi}/excel`" @complete="onSearch" />
      <el-space>
        <span v-show="!searchState" class="search">
          <el-input v-model="queryParam.name" clearable placeholder="租户名称" @change="onSearch" />
        </span>
        <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
        <el-button title="重置查询条件" @click="resetFilter">重置</el-button>
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="name" label="租户名称" />
      <el-table-column prop="code" label="租户编码" />
      <el-table-column prop="startDate" label="有效开始日期" />
      <el-table-column prop="endDate" label="有效结束日期" />
      <el-table-column prop="manager" label="负责人" />
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="statusLabel" label="租户状态">
        <template #default="{ row }">
          <el-tag
            v-if="(row.statusLabel as LabelValue)?.value"
            :color="(row.statusLabel as LabelValue<{ color: string }>).ext?.color"
            effect="dark"
            type="info"
          >
            {{ (row.statusLabel as LabelValue)?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="330" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)">
              {{ $t('operation.detail') }}
            </el-button>
            <el-button v-has-permission="'create'" text bg type="primary" size="small" @click="openAdminForm(row.id)">
              {{ $t('operation.createTenantAdmin') }}
            </el-button>
            <el-button
              v-has-permission="'create'"
              text
              bg
              type="primary"
              size="small"
              @click="openResourceForm(row.id)"
            >
              {{ $t('operation.createTenantResource') }}
            </el-button>
            <el-dropdown v-has-permission="['update', 'delete']">
              <el-button text bg type="primary" size="small">
                {{ $t('operation.more') }}
                <el-icon :size="16" style="margin-left: 5px">
                  <arrow-down />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="updatePermission" @click="openForm(row.id)">
                    {{ $t('operation.update') }}
                  </el-dropdown-item>
                  <el-dropdown-item v-if="deletePermission" @click="remove(row.id)">
                    {{ $t('operation.delete') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
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
    <Form ref="formRef" @complete="getList()" />
    <AdminForm ref="adminFormRef" />
    <ResourceForm ref="resourceFormRef" />
  </div>
</template>

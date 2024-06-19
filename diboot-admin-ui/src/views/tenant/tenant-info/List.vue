<script setup lang="ts" name="Tenant">
import { ArrowDown, Plus, Search } from '@element-plus/icons-vue'
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

const { relatedData, initRelatedData } = useOption({
  dict: ['TENANT_STATUS']
})
initRelatedData()

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
        <el-input v-model="queryParam.name" clearable :placeholder="$t('tenantInfo.name')" @change="onSearch" />
        <el-input v-model="queryParam.code" clearable :placeholder="$t('tenantInfo.code')" @change="onSearch" />
        <el-select
          v-model="queryParam.status"
          filterable
          :placeholder="$t('tenantInfo.placeholder.status')"
          clearable
          @change="onSearch"
        >
          <el-option
            v-for="item in relatedData.tenantStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="name" :label="$t('tenantInfo.name')" />
      <el-table-column prop="code" :label="$t('tenantInfo.code')" />
      <el-table-column prop="startDate" width="150" :label="$t('tenantInfo.startDate')" />
      <el-table-column prop="endDate" width="150" :label="$t('tenantInfo.endDate')" />
      <el-table-column prop="manager" :label="$t('tenantInfo.manager')" />
      <el-table-column prop="phone" :label="$t('tenantInfo.phone')" />
      <el-table-column prop="statusLabel" :label="$t('tenantInfo.status')">
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
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" />
      <el-table-column :label="$t('operation.label')" width="330" fixed="right">
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

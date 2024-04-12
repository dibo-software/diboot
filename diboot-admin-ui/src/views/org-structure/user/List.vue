<script setup lang="ts" name="UserList">
import { Search, ArrowUp, ArrowDown, Plus } from '@element-plus/icons-vue'
import type { UserPosition } from '../position/type'
import type { UserModel } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'
import type { Role } from '@/views/system/role/type'
import ExcelImport from '@/components/excel/Import.vue'
import ExcelExport from '@/components/excel/Export.vue'

const baseApi = '/iam/user'

type Props = {
  orgId?: string
}
const props = withDefaults(defineProps<Props>(), {
  orgId: '0'
})
watch(
  () => props.orgId,
  val => {
    loadListByOrgId(val)
  }
)

interface UserSearch extends UserModel {
  keywords?: string
}
const { queryParam, loading, dataList, pagination, getList, buildQueryParam, onSearch, remove, resetFilter } = useList<
  UserModel,
  UserSearch
>({ baseApi })
getList()

// 搜索区折叠
const searchState = ref(false)

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const loadListByOrgId = (orgId: string) => {
  queryParam.orgId = orgId
  onSearch()
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')

const isPrimaryPosition = (userPositionList?: UserPosition[]) =>
  !userPositionList?.length || userPositionList.some(e => e.isPrimaryPosition)
const buildRoleList = (roleList?: Role[]) => roleList?.map(e => e.name).join('、')
</script>

<template>
  <div class="list-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :lg="8" :sm="12">
          <el-form-item label="姓名">
            <el-input v-model="queryParam.realname" clearable placeholder="" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="8" :sm="12">
          <el-form-item label="员工编号">
            <el-input v-model="queryParam.userNum" clearable placeholder="" @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="8" :sm="12">
          <el-form-item label="电话">
            <el-input v-model="queryParam.mobilePhone" clearable placeholder="" @change="onSearch" />
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
      <excel-import :excel-base-api="`${baseApi}/excel`" :attach="() => ({ orgId })" @complete="onSearch" />
      <el-space>
        <el-input v-show="!searchState" v-model="queryParam.realname" clearable placeholder="姓名" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">查询</el-button>
        <el-button title="重置查询条件" @click="resetFilter">重置</el-button>
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" row-key="id" :data="dataList" stripe height="100%">
      <el-table-column prop="realname" label="姓名">
        <template #default="{ row }">
          <span v-if="isPrimaryPosition(row.userPositionList)">
            {{ row.realname }}
          </span>
          <el-tooltip v-else placement="top" content="兼职">
            <el-badge is-dot>
              {{ row.realname }}
            </el-badge>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="userNum" label="员工编号" />
      <el-table-column prop="genderLabel" label="性别" width="80">
        <template #default="{ row }">
          <el-tag :color="row.genderLabel?.ext?.color" effect="dark">
            {{ row.genderLabel?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="mobilePhone" label="电话" show-overflow-tooltip />
      <el-table-column prop="genderLabel" label="状态">
        <template #default="{ row }">
          <el-tag :color="row.statusLabel?.ext?.color" effect="dark">
            {{ row.statusLabel?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleList" label="角色" show-overflow-tooltip>
        <template #default="{ row }">
          {{ buildRoleList(row.roleList) }}
        </template>
      </el-table-column>
      <el-table-column prop="accountStatus" label="账号状态">
        <template #default="{ row }">
          <span>{{ row.accountStatusLabel || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortId" label="排序号" width="90" />
      <el-table-column prop="updateTime" label="更新时间" width="150" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)">
              {{ $t('operation.detail') }}
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
                  <el-dropdown-item v-if="deletePermission && row.status === 'I'" @click="remove(row.id)">
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
  </div>
  <Detail ref="detailRef" />
  <Form ref="formRef" @complete="getList()" />
</template>

<style lang="scss" scoped>
.el-header {
  height: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color);
}

.el-badge :deep(.is-dot) {
  right: 0;
  top: 5px;
}
</style>

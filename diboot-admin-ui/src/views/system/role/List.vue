<script setup lang="ts" name="Role">
import { Search, ArrowDown, Plus } from '@element-plus/icons-vue'
import type { Select } from '@/components/di/type'
import type { Role } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()
const baseApi = '/iam/role'

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination, remove } = useList<Role>({ baseApi })

getList()

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')

const model = ref<Partial<Role>>({})
const userListRef = ref()

const editRoleUser = (data: Role) => {
  model.value = data
  userListRef.value?.open()
}

const changeRoleUser = (userIds?: string[] | string) => {
  if ((userIds as string[]).join() != (model.value.userIdList?.join() ?? '')) {
    api
      .put(`${baseApi}/${model.value.id}/user`, userIds)
      .then(res => {
        ElMessage.success(res.msg)
        getList()
      })
      .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.updateFailed')))
    model.value = {}
  }
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.name" clearable :placeholder="$t('role.name')" @change="onSearch" />
        <el-input v-model="queryParam.code" clearable :placeholder="$t('role.code')" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="name" :label="$t('role.name')" width="150" />
      <el-table-column prop="code" :label="$t('role.code')" width="150" />
      <el-table-column prop="userNameList" :label="$t('role.userList')" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.userNameList?.join('、') }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" width="165" />
      <el-table-column prop="updateTime" :label="$t('baseField.updateTime')" width="165" />
      <el-table-column :label="$t('operation.label')" width="230" fixed="right">
        <template #default="{ row }">
          <el-space v-if="row.superAdmin === true"> - </el-space>
          <el-space v-else>
            <el-button v-has-permission="'roleUser'" text bg type="primary" size="small" @click="editRoleUser(row)">
              {{ $t('role.userList') }}
            </el-button>
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
                  <el-dropdown-item v-if="deletePermission" @click="remove(row.id, row.name)">
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
      size="small"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />

    <Detail ref="detailRef" />
    <Form ref="formRef" @complete="getList()" />

    <di-selector
      ref="userListRef"
      :model-value="model.userIdList"
      :tree="{ type: 'IamOrg', label: 'name', parent: 'parentId', parentPath: 'parentIdsPath' }"
      :conditions="[{ field: 'status', value: 'A' }]"
      :list="{
        baseApi: '/iam/user',
        relatedKey: 'orgId',
        searchArea: {
          propList: [
            { prop: 'realname', label: $t('user.realname'), type: 'input' },
            { prop: 'userNum', label: $t('user.userNum'), type: 'input' },
            { prop: 'gender', label: $t('user.gender'), type: 'select', loader: 'GENDER' } as Select
          ]
        },
        columns: [
          { prop: 'userNum', label: $t('user.userNum') },
          { prop: 'realname', label: $t('user.realname') },
          { prop: 'genderLabel', label: $t('user.gender') },
          { prop: 'mobilePhone', label: $t('user.mobilePhone') },
          { prop: 'sortId', label: $t('user.sortId') }
        ]
      }"
      multiple
      data-type="IamUser"
      data-label="realname"
      @change="changeRoleUser"
    >
      <div />
    </di-selector>
  </div>
</template>

<style scoped></style>

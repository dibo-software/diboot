<script setup lang="ts" name="Group">
import { Plus, Edit, Search, ArrowDown } from '@element-plus/icons-vue'
import type { Group } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'

import type { Select } from '@/components/di/type'
import { checkPermission } from '@/utils/permission'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()

const baseApi = '/iam/group'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove, batchRemove } =
  useList<Group>({ baseApi, initQueryParam: {} })

const { relatedData, initRelatedData } = useOption({
  load: {
    orgIdOptions: { type: 'IamOrg', label: 'name', parent: 'parentId', lazyChild: false }
  }
})

initRelatedData()

defineExpose({
  refresh: onSearch,
  addCondition: (key: keyof Group, value: any, refresh = false) => {
    queryParam[key] = value
    if (refresh) onSearch()
  }
})

const sortChange = ({ column, prop, order }: { column: { sortBy?: string }; prop: string; order: string }) => {
  let orderBy: 'ASC' | 'DESC' | undefined
  switch (order) {
    case 'ascending':
      orderBy = 'ASC'
      break
    case 'descending':
      orderBy = 'DESC'
      break
    default:
      orderBy = undefined
  }
  pagination.orderBy = orderBy ? `${column.sortBy ?? prop}:${orderBy}` : undefined
  onSearch()
}

const formRef = ref()
const formTitle = ref('')
const submitting = ref(false)
const formVisible = ref(false)
const addSustainably = ref<boolean>(false)

const openForm = (id?: string) => {
  if (id) {
    addSustainably.value = false
    formTitle.value = i18n.t('title.update')
  } else {
    addSustainably.value = true
    formTitle.value = i18n.t('title.create')
  }
  formVisible.value = true

  nextTick(() => formRef.value?.init(id))
}

const closeForm = () => {
  formVisible.value = false
}

const submit = (goOn = false) => {
  formRef.value?.submit().then((result: boolean) => {
    if (result) {
      if (!goOn) {
        formVisible.value = false
      }
      formRef.value?.reset()
    }
  })
}

const detailRef = ref()
const detailVisible = ref(false)
const dataId = ref('')

const openDetail = (id: string) => {
  dataId.value = id
  detailVisible.value = true

  nextTick(() => detailRef.value?.init(id))
}

const closeDetail = () => {
  detailVisible.value = false
}
const closeDetailAndOpenForm = () => {
  detailVisible.value = false
  openForm(dataId.value)
}

const handleOperation = (code: string, value?: string | string[], row?: Group) => {
  switch (code) {
    case 'detail':
      openDetail(value as string)
      break
    case 'create':
    case 'update':
      openForm(value as string)
      break
    case 'remove':
      remove(value as string, row?.name)
      break
    case 'batchRemove':
      batchRemove(value as string[])
      break
    default:
      throw new Error(`不存在的操作编码${code}!`)
  }
}

const refreshData = (haveNewData?: boolean) => {
  haveNewData ? onSearch() : getList()
}

const router = useRouter()

const activated = () => {
  nextTick(() => {
    const query = router.currentRoute.value.query
    for (const queryKey in query) {
      queryParam[queryKey as keyof Group] = query[queryKey] as any
    }

    onSearch()
  })
}

router.currentRoute.value.meta.keepAlive ? onActivated(activated) : activated()
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" type="primary" :icon="Plus" @click="handleOperation('create')">
        {{ $t('operation.create') }}
      </el-button>

      <el-space>
        <el-input v-model="queryParam.name" :placeholder="$t('group.name')" clearable @change="onSearch" />
        <el-tree-select
          v-model="queryParam.orgId"
          :placeholder="$t('group.orgId')"
          :data="relatedData.orgIdOptions"
          filterable
          default-expand-all
          check-strictly
          clearable
          @change="onSearch"
        />
        <di-selector
          v-model="queryParam.members"
          :placeholder="$t('group.members')"
          data-type="IamUser"
          data-label="realname"
          :tree="{ type: 'IamOrg', label: 'name', parent: 'parentId', parentPath: 'parentIdsPath' }"
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
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table
      ref="tableRef"
      v-loading="loading"
      class="list-body"
      :data="dataList"
      height="100%"
      stripe
      row-key="id"
      style="border-top: 1px solid var(--el-border-color-lighter)"
      @row-dblclick="(row: Group) => checkPermission('detail') && handleOperation('detail', row.id)"
      @sort-change="sortChange"
    >
      <el-table-column :label="$t('group.name')" prop="name" show-overflow-tooltip :min-width="100" />
      <el-table-column :label="$t('group.orgId')" prop="orgLabel" show-overflow-tooltip :min-width="100" />
      <el-table-column :label="$t('group.members')" prop="members" show-overflow-tooltip :min-width="180">
        <template #default="{ row }: { row: Group }">
          {{ row.membersLabel?.join('、') }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('group.description')" prop="description" show-overflow-tooltip :min-width="130" />
      <el-table-column :label="$t('baseField.createTime')" prop="createTime" show-overflow-tooltip :width="165" />
      <el-table-column :label="$t('baseField.updateTime')" prop="updateTime" show-overflow-tooltip :width="165" />
      <el-table-column :label="$t('operation.label')" fixed="right" :width="180">
        <template #default="{ row }: { row: Group }">
          <el-space>
            <el-button
              v-has-permission="'detail'"
              type="primary"
              text
              bg
              size="small"
              @click="handleOperation('detail', row.id)"
            >
              {{ $t('operation.detail') }}
            </el-button>
            <el-dropdown
              v-has-permission="['update', 'delete']"
              @command="(code: string) => handleOperation(code, row.id, row)"
            >
              <el-button text bg type="primary" size="small">
                {{ $t('operation.more') }}
                <el-icon :size="16" style="margin-left: 5px">
                  <ArrowDown />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="checkPermission('update')" command="update">
                    <el-button link>{{ $t('operation.update') }}</el-button>
                  </el-dropdown-item>
                  <el-dropdown-item v-if="checkPermission('delete')" command="remove">
                    <el-button link type="danger">{{ $t('operation.delete') }}</el-button>
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
      small
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />

    <el-dialog v-model="formVisible" width="50%" :title="formTitle" draggable @close="closeForm">
      <Form
        ref="formRef"
        @submitting="(val: boolean) => (submitting = val)"
        @complete="(id?: string, isNew?: boolean) => refreshData(isNew)"
      />

      <template #footer>
        <el-button @click="closeForm">{{ $t('button.cancel') }}</el-button>
        <el-button v-show="addSustainably" type="primary" :loading="submitting" @click="submit(true)">
          {{ $t('button.continueAdd') }}
        </el-button>
        <el-button type="primary" :loading="submitting" @click="submit()">{{ $t('button.save') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" width="50%" :title="$t('title.detail')" draggable @close="closeDetail">
      <Detail ref="detailRef" />

      <template #footer>
        <el-button
          v-has-permission="'update'"
          plain
          :icon="Edit"
          type="primary"
          style="position: absolute; left: var(--el-dialog-padding-primary)"
          @click="closeDetailAndOpenForm"
        >
          {{ $t('operation.update') }}
        </el-button>
        <el-button @click="closeDetail">{{ $t('button.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss"></style>

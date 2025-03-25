<script setup lang="ts" name="Client">
import { Plus, Edit, Search, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import type { Client } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'
import { checkPermission } from '@/utils/permission'
import { useI18n } from 'vue-i18n'
import { uuid } from '@/utils/tools'
import LogList from '@/views/system/operation-log/List.vue'

const i18n = useI18n()

const baseApi = '/client'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove, batchRemove } =
  useList<Client>({ baseApi, initQueryParam: {} })

const searchState = ref(false)

defineExpose({
  refresh: onSearch,
  addCondition: (key: keyof Client, value: any, refresh = false) => {
    queryParam[key] = value
    if (refresh) onSearch()
  }
})

const { initRelatedData, relatedData } = useOption({
  dict: ['ACCOUNT_STATUS']
})
initRelatedData()

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

const handleOperation = (code: string, value?: string | string[], row?: Client) => {
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
    case 'updateKey':
      api
        .put(`${baseApi}/${value}`, { id: value, appSecret: uuid() })
        .then(res => {
          ElMessage.success(res.msg)
          refreshData()
        })
        .catch(err => ElMessage.error(err.msg || err.message))
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
      queryParam[queryKey as keyof Client] = query[queryKey] as any
    }

    onSearch()
  })
}

router.currentRoute.value.meta.keepAlive ? onActivated(activated) : activated()

const viewClientIdLogs = ref()
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" type="primary" :icon="Plus" @click="handleOperation('create')">
        {{ $t('operation.create') }}
      </el-button>

      <el-space>
        <span class="search">
          <el-input v-model="queryParam.name" placeholder="名称" clearable @change="onSearch" />
        </span>
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? $t('searchState.up') : $t('searchState.down')"
          @click="searchState = !searchState"
        />
      </el-space>
    </el-space>

    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :md="8" :sm="24">
          <el-form-item prop="appKey" label="AppKey">
            <el-input v-model="queryParam.appKey" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item prop="status" :label="$t('client.status')">
            <el-select v-model="queryParam.status" filterable clearable @change="onSearch">
              <el-option v-for="item in relatedData.accountStatusOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table
      ref="tableRef"
      v-loading="loading"
      class="list-body"
      :data="dataList"
      height="100%"
      stripe
      row-key="id"
      style="border-top: 1px solid var(--el-border-color-lighter)"
      @row-dblclick="(row: Client) => checkPermission('detail') && handleOperation('detail', row.id)"
      @sort-change="sortChange"
    >
      <el-table-column :label="$t('client.name')" prop="name" show-overflow-tooltip />
      <el-table-column label="AppKey" prop="appKey" show-overflow-tooltip />
      <el-table-column label="AppSecret" prop="appSecret" show-overflow-tooltip />
      <el-table-column :label="$t('client.status')" prop="statusLabel" :width="95">
        <template #default="{ row }: { row: Client }">
          <el-tag
            v-if="row.statusLabel"
            effect="dark"
            type="info"
            :color="(row.statusLabel as LabelValue<{ color?: string }>).ext?.color"
          >
            {{ (row.statusLabel as LabelValue).label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('baseField.createBy')" prop="createByLabel" show-overflow-tooltip />
      <el-table-column :label="$t('baseField.updateTime')" prop="updateTime" show-overflow-tooltip />
      <el-table-column :label="$t('operation.label')" fixed="right" :width="280">
        <template #default="{ row }: { row: Client }">
          <el-space>
            <el-button
              v-has-permission="'update'"
              type="warning"
              text
              bg
              size="small"
              @click="handleOperation('updateKey', row.id)"
            >
              {{ $t('client.updateKey') }}
            </el-button>
            <el-button v-has-permission="'viewLogs'" text bg size="small" @click="viewClientIdLogs = row.id">
              {{ $t('client.viewLogs') }}
            </el-button>
            <el-dropdown
              v-has-permission="['detail', 'update', 'delete']"
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
                  <el-dropdown-item v-if="checkPermission('detail')" command="detail">
                    <el-button link>{{ $t('operation.detail') }}</el-button>
                  </el-dropdown-item>
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
      :page-sizes="[10, 15, 20, 30, 50, 100]"
      size="small"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />

    <el-dialog v-model="formVisible" width="60%" :title="formTitle" top="5vh" draggable @close="closeForm">
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

    <el-dialog v-model="detailVisible" width="60%" :title="$t('title.detail')" draggable @close="closeDetail">
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

    <el-dialog
      :model-value="!!viewClientIdLogs"
      :title="$t('client.viewLogs')"
      top="5vh"
      width="80%"
      draggable
      @close="viewClientIdLogs = void 0"
    >
      <LogList v-if="viewClientIdLogs" :user-id="viewClientIdLogs" user-type="Client" style="height: 73vh" />

      <template #footer>
        <el-button @click="viewClientIdLogs = void 0">{{ $t('button.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss"></style>

<script setup lang="ts" name="MobileResource">
import { Plus, Search } from '@element-plus/icons-vue'
import type { Resource } from '../type'
import { useI18n } from 'vue-i18n'
import Form from './Form.vue'

const i18n = useI18n()

const baseApi = '/iam/resource'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove, batchRemove } =
  useList<Resource>({ baseApi, initQueryParam: { appModule: 'mobile' }, deleteCallback: () => refreshData() })

defineExpose({
  refresh: onSearch,
  addCondition: (key: keyof Resource, value: any, refresh = false) => {
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

  nextTick(() => formRef.value?.init(id, true, { parentId: parent.value }))
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

const handleOperation = (code: string, value?: string | string[]) => {
  switch (code) {
    case 'create':
    case 'update':
      openForm(value as string)
      break
    case 'remove':
      remove(value as string)
      break
    case 'batchRemove':
      batchRemove(value as string[])
      break
    default:
      throw new Error(`不存在的操作编码${code}!`)
  }
}

const treeRef = ref()

const parent = ref()
// 监听左树节点变化
watch(
  parent,
  value => {
    queryParam.parentId = value
    onSearch()
  },
  { immediate: true }
)

const refreshData = (haveNewData?: boolean) => {
  // 当左树是当前业务模型时则与列表数据同步刷新
  treeRef.value?.refresh()
  haveNewData ? onSearch() : getList()
}

const router = useRouter()

const activated = () => {
  nextTick(() => {
    const query = router.currentRoute.value.query
    for (const queryKey in query) {
      queryParam[queryKey as keyof Resource] = query[queryKey] as any
    }
    treeRef.value?.refresh()
    onSearch()
  })
}

router.currentRoute.value.meta.keepAlive ? onActivated(activated) : activated()
</script>

<template>
  <div style="height: 100%; display: flex">
    <di-tree
      ref="treeRef"
      type="IamResource"
      label="displayName"
      parent="parentId"
      :lazy-child="false"
      :conditions="[{ field: 'appModule', value: 'mobile' }]"
      @click-node="(id?: string) => (parent = id)"
      @change-order="onSearch()"
    />

    <div class="list-page" style="width: 0">
      <el-space wrap class="list-operation">
        <el-button v-has-permission="'create'" type="primary" :icon="Plus" @click="handleOperation('create')">
          {{ $t('operation.create') }}
        </el-button>

        <el-space>
          <span class="search">
            <el-input
              v-model="queryParam.displayName"
              :placeholder="$t('resource.mobile.displayName')"
              clearable
              @change="onSearch"
            />
          </span>
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
        @sort-change="sortChange"
      >
        <el-table-column :label="$t('resource.mobile.parentId')" prop="parentDisplayName" show-overflow-tooltip />
        <el-table-column :label="$t('resource.mobile.displayName')" prop="displayName" show-overflow-tooltip />
        <el-table-column :label="$t('resource.mobile.resourceCode')" prop="resourceCode" show-overflow-tooltip />
        <el-table-column :label="$t('baseField.updateTime')" prop="updateTime" show-overflow-tooltip />
        <el-table-column :label="$t('operation.label')" fixed="right" :width="180">
          <template #default="{ row }: { row: Resource }">
            <el-space>
              <el-button v-has-permission="'update'" text bg size="small" @click="handleOperation('update', row.id)">
                {{ $t('operation.update') }}
              </el-button>
              <el-button
                v-has-permission="'delete'"
                type="danger"
                text
                bg
                size="small"
                @click="handleOperation('remove', row.id)"
              >
                {{ $t('operation.delete') }}
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="pagination.total"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        size="small"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="getList()"
        @current-change="getList()"
      />

      <el-dialog v-model="formVisible" width="600px" :title="formTitle" draggable @close="closeForm">
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
    </div>
  </div>
</template>

<style scoped lang="scss"></style>

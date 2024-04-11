<script setup lang="ts" name="DiList">
import { Plus, Delete, Search, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { buildOptionProps, buildGetRelatedData } from './utils'
import type { FormConfig, ListConfig, ListOperation, TableColumn } from '@/components/di/type'

interface ListProps extends /* @vue-ignore */ ListConfig {
  // 模型名
  model: string
  // 左树父级ID
  parent?: string
  // 不自动加载列表数据
  notLoadAuto?: boolean

  // vue语法限制导致只能在当前文件中再次定义
  // https://cn.vuejs.org/guide/typescript/composition-api.html#typing-component-props
  baseApi: string
  primaryKey?: string
  searchArea?: FormConfig
  columns: TableColumn[]
  relatedKey?: string
  operation?: ListOperation
}

const props = defineProps<ListProps>()

const { initRelatedData, relatedData, asyncLoading, remoteRelatedDataFilter, lazyLoadRelatedData } = useOption(
  buildOptionProps(props.searchArea?.propList)
)
initRelatedData()

/**
 * 获取选项
 * @param prop
 * @return LabelValue[]
 */
const getRelatedData = buildGetRelatedData(relatedData)

const {
  queryParam,
  dateRangeQuery,
  loading,
  dataList,
  pagination,
  getList,
  onSearch,
  resetFilter,
  buildQueryParam,
  remove,
  batchRemove
} = useList<Record<string, unknown>>({ baseApi: props.baseApi })

if (!props.notLoadAuto) getList()

// 监听左树节点变化
watch(
  () => props.parent,
  value => {
    if (props.relatedKey) {
      queryParam[props.relatedKey] = value
      onSearch()
    }
  },
  { immediate: true }
)

// 搜索区折叠
const searchState = ref(false)

// 排序
const orderBy = (prop: string, order?: 'ASC' | 'DESC') => {
  pagination.orderBy = order ? `${prop}:${order}` : undefined
  onSearch()
}

// 选中的数据 Id 集合
const selectedKeys = ref<string[]>([])

const emit = defineEmits<{
  (e: 'openForm', id?: string): void
  (e: 'openDetail', id: string): void
}>()

const openForm = (id?: string) => emit('openForm', id)
const openDetail = (id: string) => emit('openDetail', id)

defineExpose({
  refresh: getList
})

const createPermission = checkPermission('create', false, false, props.model)
const detailPermission = checkPermission('detail', false, false, props.model)
const importPermission = checkPermission('import', false, false, props.model)
const exportPermission = checkPermission('export', false, false, props.model)
const updatePermission = checkPermission('update', false, false, props.model)
const deletePermission = checkPermission('delete', false, false, props.model)
const updateOrDeletePermission = checkPermission(['update', 'delete'], false, false, props.model)

const multiple = inject<boolean | undefined>(
  'multiple',
  (deletePermission && props.operation?.batchRemove) || exportPermission ? true : undefined
)
</script>

<template>
  <div class="list-page">
    <el-form v-if="searchArea" v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col
          v-for="item in searchArea.propList"
          :key="JSON.stringify(item)"
          :md="searchArea.column ? 24 / searchArea.column : 8"
          :sm="24"
        >
          <di-input
            v-if="['daterange', 'datetimerange'].includes(item.type)"
            v-model="dateRangeQuery[item.prop]"
            :config="item"
            @change="onSearch"
          />
          <di-input
            v-else
            v-model="queryParam[item.prop]"
            :config="item"
            :related-datas="getRelatedData(item)"
            :loading="asyncLoading"
            :lazy-load="async (parentId: string) => await lazyLoadRelatedData(item.prop, parentId)"
            @change="onSearch"
            @remote-filter="(value?: string) => remoteRelatedDataFilter(item.prop, value)"
          />
        </el-col>
      </el-row>
    </el-form>

    <el-space wrap class="list-operation">
      <el-button v-if="createPermission && operation?.create" type="primary" :icon="Plus" @click="openForm()">
        新建
      </el-button>
      <excel-import
        v-if="operation?.importData && importPermission"
        :excel-base-api="`${baseApi}/excel`"
        :attach="relatedKey ? () => ({ [`${relatedKey}`]: parent }) : undefined"
        @complete="onSearch"
      />
      <excel-export
        v-if="operation?.exportData && exportPermission"
        :build-param="() => ({ ...buildQueryParam(), ids: selectedKeys })"
        :export-url="`${baseApi}/excel/export`"
        :table-head-url="`${baseApi}/excel/export-table-head`"
      />
      <el-button
        v-if="operation?.batchRemove && deletePermission"
        plain
        type="danger"
        :icon="Delete"
        :disabled="!selectedKeys.length"
        @click="batchRemove(selectedKeys)"
      >
        批量删除
      </el-button>
      <el-space>
        <span v-if="searchArea?.propList?.length" v-show="!searchState" class="search">
          <template v-for="item in [searchArea.propList[0]]" :key="item.prop">
            <di-input
              v-if="['daterange', 'datetimerange'].includes(item.type)"
              v-model="dateRangeQuery[item.prop]"
              :config="{ ...item, label: '' }"
              @change="onSearch"
            />
            <di-input
              v-else
              v-model="queryParam[item.prop]"
              :config="{ ...item, label: '', placeholder: item.placeholder ?? item.label }"
              :related-datas="getRelatedData(item)"
              :loading="asyncLoading"
              :lazy-load="async (parentId: string) => await lazyLoadRelatedData(item.prop, parentId)"
              @change="onSearch"
              @remote-filter="(value?: string) => remoteRelatedDataFilter(item.prop, value)"
            />
          </template>
        </span>
        <template v-if="searchArea?.propList?.length">
          <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
          <el-button title="重置搜索条件" @click="resetFilter">重置</el-button>
        </template>
        <el-button
          v-if="(searchArea?.propList?.length ?? 0) > 1"
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
      </el-space>
    </el-space>

    <di-table
      :model="model"
      :loading="loading"
      :columns="columns"
      :data-list="dataList"
      :multiple="multiple"
      :primary-key="primaryKey"
      @selected-keys="(v: string[]) => (selectedKeys = v)"
      @order="orderBy"
    >
      <el-table-column
        v-if="operation?.detail || operation?.update || operation?.remove"
        label="操作"
        width="160"
        fixed="right"
      >
        <template #default="{ row }">
          <el-space>
            <el-button
              v-if="operation?.detail && detailPermission"
              text
              bg
              type="primary"
              size="small"
              @click="openDetail(row.id)"
            >
              详情
            </el-button>
            <el-dropdown v-if="(operation?.update || operation?.remove) && updateOrDeletePermission">
              <el-button text bg type="primary" size="small">
                更多
                <el-icon :size="16" style="margin-left: 5px">
                  <ArrowDown />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="operation?.update && updatePermission" @click="openForm(row.id)">
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item v-if="operation?.remove && deletePermission" @click="remove(row.id)">
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </template>
      </el-table-column>

      <template #pagination>
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
      </template>
    </di-table>
  </div>
</template>

<style scoped lang="scss">
.list-search {
  :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}

.list-operation .search :deep(.el-form-item) {
  margin-bottom: 0 !important;
}
</style>

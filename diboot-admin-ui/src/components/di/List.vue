<script setup lang="ts" name="DiList">
import { Search, CircleClose, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { buildOptionProps, buildGetRelatedData } from './utils'
import type { FormConfig, ListConfig, ListOperation, TableColumn } from '@/components/di/type'

interface ListProps extends ListConfig {
  // 左树父级ID
  parent?: string

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
getList()

// 监听左树节点变化
watch(
  () => props.parent,
  value => {
    queryParam[props.relatedKey ?? 'parentId'] = value
    onSearch()
  }
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

const createPermission = checkPermission('create')
const detailPermission = checkPermission('detail')
const importPermission = checkPermission('import')
const exportPermission = checkPermission('export')
const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')
const updateOrDeletePermission = checkPermission(['update', 'delete'])

const multiple = inject<boolean | undefined>(
  'multiple',
  deletePermission && props.operation?.batchRemove ? true : undefined
)
</script>

<template>
  <div class="list-page">
    <el-form v-if="searchArea" v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col
          v-for="item in searchArea.propList"
          :key="item.prop"
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
            @change="onSearch"
            @remote-filter="(value: string) => remoteRelatedDataFilter(value, item.prop)"
            @lazy-load="(parentId: string) => lazyLoadRelatedData(item.prop, parentId)"
          />
        </el-col>
      </el-row>
    </el-form>
    <el-header>
      <el-space wrap class="list-operation" :size="10">
        <el-button v-if="createPermission && operation?.create" type="primary" @click="openForm()"> 新建</el-button>
        <el-button
          v-if="operation?.batchRemove && deletePermission"
          type="danger"
          plain
          :disabled="!selectedKeys.length"
          @click="batchRemove(selectedKeys)"
        >
          批量删除
        </el-button>
        <excel-export
          v-if="operation?.exportData && exportPermission"
          :build-param="buildQueryParam"
          :export-url="`${baseApi}/excel/export`"
          :table-head-url="`${baseApi}/excel/export-table-head`"
        />
        <excel-import
          v-if="operation?.importData && importPermission"
          :excel-base-api="`${baseApi}/excel`"
          :attach="relatedKey ? () => ({ [`${relatedKey}`]: parent }) : undefined"
          @complete="onSearch"
        />
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
                @change="onSearch"
                @remote-filter="(value: string) => remoteRelatedDataFilter(value, item.prop)"
                @lazy-load="(parentId: string) => lazyLoadRelatedData(item.prop, parentId)"
              />
            </template>
          </span>
          <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
          <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
          <el-button
            v-if="searchArea?.propList?.length"
            :icon="searchState ? ArrowUp : ArrowDown"
            :title="searchState ? '收起' : '展开'"
            @click="searchState = !searchState"
          />
        </el-space>
      </el-space>
    </el-header>
    <di-table
      :loading="loading"
      :columns="columns"
      :data-list="dataList"
      :multiple="multiple"
      :primary-key="primaryKey"
      @selected-keys="(v:string[]) => (selectedKeys = v)"
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

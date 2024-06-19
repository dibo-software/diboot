<script setup lang="ts" name="DiTable">
import { Sort } from '@element-plus/icons-vue'
import Draggable from 'vuedraggable'
import type { TableColumn } from './type'
import type { Ref } from 'vue'
import type { ElTable } from 'element-plus'

function getCache<T>(key: string, def?: T): T {
  const cache = localStorage.getItem(key)
  return cache ? JSON.parse(cache) : def
}

const setCache = (key: string, value?: unknown) => {
  if (value) localStorage.setItem(key, JSON.stringify(value))
  else localStorage.removeItem(key)
}

const props = withDefaults(
  defineProps<{
    model: string
    // 主键属性名（默认值：id）
    primaryKey?: string
    columns: TableColumn[]
    loading?: boolean
    dataList?: Record<string, unknown>[]
    multiple?: boolean
  }>(),
  { primaryKey: 'id', dataList: undefined }
)

const emit = defineEmits<{
  (e: 'selectedKeys', ids: string[]): void
  (e: 'rowDblclick', row: Record<string, unknown>): void
  (e: 'order', prop: string, order?: 'ASC' | 'DESC'): void
}>()

// 表配置
interface TableConfig {
  border: boolean
  stripe: boolean
  columns: TableColumn[]
}

// 表列
const getTableConfig = () => {
  const columns = _.cloneDeep(props.columns)
  const tableConfig = getCache<TableConfig>(tableConfigKey, { border: false, stripe: true, columns })
  if (tableConfig.columns !== columns) {
    for (let i = 0; i < columns?.length; i++) {
      const column = tableConfig.columns.find(e => e.prop === columns[i].prop)
      if (column) columns[i] = column
    }
    tableConfig.columns = columns
  }
  return tableConfig
}
const visible = ref<boolean>()
const tableConfigKey = 'table-config-' + props.model
const config = ref<TableConfig>(getTableConfig())
const resetTableConfig = () => {
  localStorage.removeItem(tableConfigKey)
  config.value = { border: false, stripe: true, columns: _.cloneDeep(props.columns) }
}
const saveColumnChange = () => {
  visible.value = false
  setCache(tableConfigKey, config.value)
  nextTick(() => (visible.value = undefined))
}

const selectedRows = inject<Ref<LabelValue[] | undefined>>('selected-rows', ref())
const dataLabel = inject<string>('data-label', 'label')

const single = ref<string>()

// 选中的数据
const selected = (arr: Array<Record<string, unknown>>) => {
  onSelectionChange(arr)
  emit(
    'selectedKeys',
    arr.map(e => `${e[props.primaryKey]}`)
  )
}
// 选中数据变更
const onSelectionChange = (rows: Array<Record<string, unknown>>) => {
  if (!selectedRows.value) return
  const ids = selectedRows.value.map(e => e.value)
  const rowIds = rows.map(e => e[props.primaryKey])
  const delIds = props.dataList?.map(e => e[props.primaryKey] as string).filter(e => !rowIds.includes(e)) ?? []
  const allSelectedRows = selectedRows.value.filter(e => !delIds.includes(e.value))
  allSelectedRows.push(
    ...rows
      .filter(e => !ids.includes(e[props.primaryKey] as string))
      .map(
        item =>
          ({
            value: item[props.primaryKey],
            label: item[dataLabel as keyof typeof item]
          }) as LabelValue
      )
  )
  selectedRows.value = allSelectedRows
}

// 点击行
const rowClick = (row: Record<string, unknown>) => {
  if (!selectedRows.value) return
  const index = selectedRows.value.findIndex(e => e.value === row[props.primaryKey])
  if (index === -1) {
    const item = {
      value: row[props.primaryKey] as string,
      label: row[dataLabel as keyof typeof row] as string
    }
    if (props.multiple) selectedRows.value.push(item)
    else selectedRows.value = [item]
  } else selectedRows.value.splice(index as number, 1)
}

const rowDblclick = (row: Record<string, unknown>) => emit('rowDblclick', row)

const tableRef = ref<InstanceType<typeof ElTable>>()

if (selectedRows.value) {
  if (props.multiple)
    watch(
      () => props.dataList,
      value => {
        const ids = selectedRows.value?.map(e => e.value) ?? []
        tableRef.value?.clearSelection()
        value?.forEach(item => {
          if (ids.includes(item[props.primaryKey] as string)) tableRef.value?.toggleRowSelection(item, true)
        })
      },
      { deep: true, immediate: true }
    )

  watch(
    selectedRows,
    value => {
      const ids = value?.map(e => e.value) ?? []
      if (props.multiple)
        if (ids.length === 0) tableRef.value?.clearSelection()
        else {
          props.dataList?.forEach(item =>
            tableRef.value?.toggleRowSelection(item, ids.includes(item[props.primaryKey] as string))
          )
        }
      else single.value = value?.length ? value[0].value : undefined
    },
    { deep: true, immediate: true }
  )
} else {
  watch(
    () => props.dataList,
    () => tableRef.value?.clearSelection(),
    { deep: true, immediate: true }
  )
}

// 排序
const sortChange = ({ prop, order }: { prop: string; order: string }) => {
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
  emit('order', props.columns.find(e => e.prop === prop)?.column ?? prop, orderBy)
}

const headerDragend = (newWidth: number, oldWidth: number, column: { property: string }) =>
  (config.value.columns.find(e => e.prop === column.property)!.width = Math.trunc(newWidth))
</script>

<template>
  <el-table
    ref="tableRef"
    v-loading="!!loading"
    class="list-body"
    :data="dataList"
    height="100%"
    :row-key="primaryKey"
    v-bind="config"
    @row-click="rowClick"
    @row-dblclick="rowDblclick"
    @selection-change="selected"
    @sort-change="sortChange"
    @header-dragend="headerDragend"
  >
    <el-table-column v-if="multiple === false" type="index" width="55" fixed>
      <template #default="{ row }">
        <el-radio v-model="single" :label="row[primaryKey]" @click.prevent.self><span /></el-radio>
      </template>
    </el-table-column>
    <el-table-column v-else-if="multiple" type="selection" width="55" fixed />

    <slot v-bind="{ openConfig: () => (visible = true) }" />
    <template v-for="(item, index) in config.columns" :key="index">
      <el-table-column
        v-if="!item.hide"
        :column-key="item.prop"
        :label="item.label"
        :prop="item.prop"
        :width="item.width"
        :sortable="item.sortable"
        :fixed="item.fixed"
        :filters="item.filters as any"
        :show-overflow-tooltip="item.showOverflowTooltip ?? true"
      >
        <template v-if="item.number" #header>
          <el-tooltip v-if="item.number" placement="bottom">
            <template #content>
              {{ $t('components.di.table.total') }}：{{
                Number(
                  (dataList ?? [])
                    .map(e => Number(e[item.prop] ?? 0))
                    .reduce((e1, e2) => e1 + e2, 0)
                    .toFixed(item.number)
                )
              }}<br />
              {{ $t('components.di.table.avg') }}：{{
                Number(
                  (
                    (dataList ?? []).map(e => Number(e[item.prop] ?? 0)).reduce((e1, e2) => e1 + e2, 0) /
                    (dataList ?? []).length
                  ).toFixed(item.number + 1)
                )
              }}
            </template>
            {{ item.label }}
          </el-tooltip>
        </template>
        <template #default="scope">
          <slot :name="item.prop" v-bind="scope">
            <span v-if="Array.isArray(scope.row[item.prop])">
              {{ scope.row[item.prop].map((e: LabelValue) => e.label ?? e).join(' 、') }}
            </span>
            <el-tag
              v-else-if="scope.row[item.prop]?.value"
              :color="scope.row[item.prop].ext?.color"
              effect="dark"
              type="info"
            >
              {{ scope.row[item.prop].label }}
            </el-tag>
            <template v-else-if="typeof scope.row[item.prop] === 'boolean'">
              <template v-if="scope.row[item.prop]"> {{ $t('bool.yes') }} </template>
              <template v-else> {{ $t('bool.no') }} </template>
            </template>
            <span v-else>
              {{ scope.row[item.prop] }}
            </span>
          </slot>
        </template>
      </el-table-column>
    </template>
  </el-table>

  <div v-if="$slots.pagination">
    <div>
      <slot name="pagination" />
    </div>
    <el-space v-if="!selectedRows" :size="20" style="height: 38px">
      <slot name="operation" />
    </el-space>
  </div>

  <el-dialog v-model="visible" :width="500" :title="$t('components.di.table.config.title')">
    <table class="sortable-table">
      <thead>
        <tr>
          <th />
          <th style="width: 60px">{{ $t('components.di.table.config.show') }}</th>
          <th style="width: 160px">{{ $t('components.di.table.config.name') }}</th>
          <th style="width: 100px">{{ $t('components.di.table.config.width') }}</th>
          <th style="width: 60px">{{ $t('components.di.table.config.sort') }}</th>
          <th style="width: 60px">{{ $t('components.di.table.config.fixed') }}</th>
        </tr>
      </thead>
      <draggable
        v-model="config.columns"
        tag="tbody"
        item-key="prop"
        ghost-class="sortable-ghost"
        handle=".drag-handle"
      >
        <template #item="{ element }">
          <tr>
            <td>
              <el-button class="drag-handle" plain :icon="Sort" size="small" />
            </td>
            <td>
              <el-switch v-model="element.hide" :active-value="false" :inactive-value="true" />
            </td>
            <td>
              {{ element.label }}
            </td>
            <td>
              <el-input-number v-model="element.width" size="small" :min="50" step-strictly />
            </td>
            <td>
              <el-switch v-model="element.sortable" active-value="custom" :inactive-value="false" />
            </td>
            <td>
              <el-switch v-model="element.fixed" />
            </td>
          </tr>
        </template>
      </draggable>
    </table>
    <template #footer>
      <div style="display: flex; align-items: center; justify-content: space-between">
        <div>
          <el-checkbox v-model="config.border" :label="$t('components.di.table.border')" size="small" />
          <el-checkbox v-model="config.stripe" :label="$t('components.di.table.stripe')" size="small" />
        </div>
        <div>
          <el-button size="small" @click="resetTableConfig">{{ $t('operation.reset') }}</el-button>
          <el-button size="small" type="primary" @click="saveColumnChange"> {{ $t('button.save') }} </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.sortable-table {
  display: block;
  overflow-y: auto;
  text-align: center;
  max-height: calc(100vh - 200px);

  thead {
    border-bottom: solid 1px var(--el-menu-border-color);
    background-color: var(--el-bg-color-overlay);
    position: sticky;
    z-index: 2;
    top: 0;
  }
}

.config {
  .el-form-item {
    margin-bottom: 8px !important;
  }
}
</style>

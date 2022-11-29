<script setup lang="ts" name="DiTable">
import { SetUp, Sort, Setting } from '@element-plus/icons-vue'
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

// 表配置
interface TableConfig {
  size: string
  border: boolean
  stripe: boolean
}

const tableConfigKey = 'table-config'
const config = ref<TableConfig>(getCache(tableConfigKey, { size: '', border: false, stripe: false }))
watch(config, value => setCache(tableConfigKey, value), { deep: true })

const props = withDefaults(
  defineProps<{
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
  (e: 'selectedKeys', ids?: string[]): void
  (e: 'order', prop: string, order?: 'ASC' | 'DESC'): void
}>()

const route = useRoute()

// 表列
const columns = () => {
  const list = _.cloneDeep(props.columns)
  const cacheList = getCache<TableColumn[]>(columnsKey, [])
  for (let i = 0; i < list?.length; i++) {
    const tableColumn = list[i]
    const find = cacheList.find(e => e.prop === tableColumn.prop)
    if (find) list[i] = find
  }
  return list
}
const columnsKey = `table-columns-` + (route?.name ?? +new Date()).toString()
const columnList = ref(columns())
const resetColumnConfig = () => (columnList.value = columns())
const saveColumnChange = () => setCache(columnsKey, columnList.value)

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
  const delIds =
    props.dataList?.filter(e => !rowIds.includes(e[props.primaryKey])).map(e => e[props.primaryKey] as string) ?? []
  const allSelectedRows = selectedRows.value.filter(e => !delIds.includes(e.value))
  allSelectedRows.push(
    ...rows
      .filter(e => !ids.includes(e[props.primaryKey] as string))
      .map(
        item =>
          ({
            value: item[props.primaryKey],
            label: item[dataLabel as keyof typeof item]
          } as LabelValue)
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

const tableRef = ref<InstanceType<typeof ElTable>>()

if (selectedRows.value) {
  if (props.multiple)
    watch(
      () => props.dataList,
      value => {
        const ids = selectedRows.value?.map(e => e.value) ?? []
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
        props.dataList?.forEach(item =>
          tableRef.value?.toggleRowSelection(item, ids.includes(item[props.primaryKey] as string))
        )
      else single.value = value?.length ? value[0].value : undefined
    },
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
  emit('order', prop, orderBy)
}
</script>

<template>
  <el-table
    ref="tableRef"
    v-loading="loading"
    class="list-body"
    :data="dataList"
    height="100%"
    :row-key="primaryKey"
    v-bind="config"
    @row-click="rowClick"
    @selection-change="selected"
    @sort-change="sortChange"
  >
    <el-table-column v-if="multiple === false" type="index" width="55" fixed>
      <template #default="{ row }">
        <el-radio v-model="single" :label="row[primaryKey]">{{ '' }}</el-radio>
      </template>
    </el-table-column>
    <el-table-column v-else-if="multiple" type="selection" width="55" fixed />

    <slot />
    <template v-for="(item, index) in columnList" :key="index">
      <el-table-column
        v-if="!item.hide"
        :column-key="item.prop"
        :label="item.label"
        :prop="item.prop"
        :width="item.width"
        :sortable="item.sortable"
        :fixed="item.fixed"
        :filters="item.filters"
        :show-overflow-tooltip="item.showOverflowTooltip ?? true"
      >
        <template #default="scope">
          <slot :name="item.prop" v-bind="scope">
            <span v-if="Array.isArray(scope.row[item.prop])">
              <el-tag
                v-for="arrItem in scope.row[item.prop]"
                :key="arrItem.value ?? arrItem"
                :color="arrItem.ext?.color"
                effect="dark"
                type="info"
              >
                {{ arrItem.label ?? arrItem }}
              </el-tag>
            </span>
            <el-tag
              v-else-if="scope.row[item.prop]?.value"
              :color="scope.row[item.prop].ext?.color"
              effect="dark"
              type="info"
            >
              {{ scope.row[item.prop].label }}
            </el-tag>
            <span v-else>
              {{ scope.row[item.prop] }}
            </span>
          </slot>
        </template>
      </el-table-column>
    </template>
  </el-table>

  <div style="display: flex; justify-content: space-between">
    <div>
      <slot name="pagination" />
    </div>
    <el-space v-if="!selectedRows" :size="20" style="height: 38px">
      <el-popover :width="500" trigger="click" placement="top-start">
        <template #reference>
          <el-button v-show="columnList" :icon="SetUp" size="default" text bg circle />
        </template>
        <table class="sortable-table">
          <thead>
            <tr>
              <th />
              <th style="width: 60px">显示</th>
              <th style="width: 160px">名称</th>
              <th style="width: 100px">宽度</th>
              <th style="width: 60px">排序</th>
              <th style="width: 60px">固定</th>
            </tr>
          </thead>
          <draggable
            v-model="columnList"
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
        <div style="display: flex; justify-content: flex-end">
          <el-button size="small" @click="resetColumnConfig">重置</el-button>
          <el-button type="primary" size="small" @click="saveColumnChange">保存</el-button>
        </div>
      </el-popover>
      <el-popover :width="250" trigger="click" placement="top-start">
        <template #reference>
          <el-button :icon="Setting" size="default" text bg circle />
        </template>
        <el-form label-width="50px" label-position="left" class="config">
          <el-form-item label="尺寸">
            <el-radio-group v-model="config.size" size="small">
              <el-radio-button label="large">大</el-radio-button>
              <el-radio-button label="default">正常</el-radio-button>
              <el-radio-button label="small">小</el-radio-button>
              <el-radio-button label="">跟随全局</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="样式">
            <el-checkbox v-model="config.border" label="纵向边框" />
            <el-checkbox v-model="config.stripe" label="斑马纹" />
          </el-form-item>
        </el-form>
      </el-popover>
    </el-space>
  </div>
</template>

<style scoped lang="scss">
.sortable-table {
  text-align: center;
  border-bottom: solid 1px var(--el-menu-border-color);
  margin-bottom: 8px;

  thead {
    border-bottom: solid 1px var(--el-menu-border-color);
  }
}

.config {
  .el-form-item {
    margin-bottom: 8px !important;
  }
}
</style>

<script setup lang="ts">
import { Loading, Download } from '@element-plus/icons-vue'
import { ElCheckbox, ElTableColumn } from 'element-plus'
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import type { PropType, VNode } from 'vue'
import type { TableHead } from './type'
import { fileDownload } from '@/utils/file'

const props = defineProps<{
  // 导出数据接口
  exportUrl: string
  // 获取表头接口
  tableHeadUrl?: string
  // 构建参数函数
  buildParam?: () => Record<string, unknown>
  // 自定义列导出
  // 示例：[{'导出用户联系方式':['name','userNum','mobilePhone']}]
  options?: Record<string, string[]>
  // 对话框宽度（默认50%）
  width?: string
}>()

const exportLoadingData = ref(false)
const dialogVisible = ref(false)
const tableLoading = ref(false)
const tableHeadList = ref<TableHead[]>()

// 导出指定列的数据（columns为空或空数组时导出所有列）
const exportData = (columns?: string[]) => {
  if (exportLoadingData.value) return
  exportLoadingData.value = true
  fileDownload(props.exportUrl, { ...(props.buildParam ? props.buildParam() : {}), columns })?.finally(
    () => (exportLoadingData.value = false)
  )
}

// 菜单命令分发
const handleCommand = (columns?: string[]) => {
  if (columns instanceof Array) {
    exportData(columns)
  } else {
    dialogVisible.value = true
    if (!tableHeadList.value && props.tableHeadUrl) {
      tableLoading.value = true
      api
        .get<TableHead[]>(props.tableHeadUrl)
        .then(res => {
          tableHeadList.value = res.data
          tableLoading.value = false
        })
        .catch(res => {
          ElMessage.error(res.msg)
        })
    }
  }
}

// 嵌套表头组件
const TableColumn = defineComponent({
  name: 'TableColumn',
  props: {
    column: { type: Object as PropType<TableHead>, required: true }
  },
  setup(props) {
    const buildColumn = (column: TableHead): VNode => {
      const children = (column.children ?? []).map(e => buildColumn(e))
      return h(
        ElTableColumn,
        { label: column.title, prop: column.key, align: 'center', formatter },
        children.length ? { default: () => children } : {}
      )
    }
    return () => buildColumn(props.column)
  }
})

const checkAll = ref(true)
const isIndeterminate = ref(false)
const checkedList = ref<string[]>([])
const allList = ref<string[]>([])

// 列全选处理
const handleCheckAllChange = (val: string | number | boolean) => {
  checkedList.value = val ? allList.value : []
  isIndeterminate.value = false
}

// 复选框渲染
function formatter(row: TableHead, column: TableColumnCtx<TableHead>) {
  const property = column.property
  if (!allList.value.includes(property)) {
    allList.value.push(property)
    checkedList.value.push(property)
  }
  return h(ElCheckbox, { label: property }, { default: () => '' })
}

// 复选框组变化
const handleCheckedCitiesChange = (value: (string | number | boolean)[]) => {
  const checkedCount = value.length
  checkAll.value = checkedCount === allList.value.length
  isIndeterminate.value = checkedCount > 0 && checkedCount < allList.value.length
}

// 自定义表头确认导出
const confirm = () => {
  handleCommand(checkedList.value)
  dialogVisible.value = false
}
</script>

<template>
  <span>
    <!-- 可选列导出 -->
    <el-dropdown v-if="tableHeadUrl || options" split-button @click="exportData()" @command="handleCommand">
      <el-icon>
        <component :is="exportLoadingData ? Loading : Download" />
      </el-icon>
      导出
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-if="tableHeadUrl" command="select">选择列导出</el-dropdown-item>
          <el-dropdown-item v-for="(value, key, index) in options" :key="index" :divided="index === 0" :command="value">
            {{ key }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 固定模板导出 -->
    <el-button v-else :icon="exportLoadingData ? Loading : Download" type="default" @click="exportData()">
      导出
    </el-button>

    <!-- 导出列选择 -->
    <el-dialog v-model="dialogVisible" :width="width">
      <template #header>
        选择导出列
        <el-checkbox
          v-model="checkAll"
          :indeterminate="isIndeterminate"
          style="margin-left: 25px"
          @change="handleCheckAllChange"
        >
          全选
        </el-checkbox>
      </template>
      <el-checkbox-group v-model="checkedList" @change="handleCheckedCitiesChange">
        <el-table v-loading="tableLoading" border :data="[{}]" style="width: 100%">
          <table-column v-for="(item, index) in tableHeadList" :key="index" :column="item" />
        </el-table>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirm">确 定</el-button>
      </template>
    </el-dialog>
  </span>
</template>

<style scoped lang="scss">
.el-dropdown {
  :deep(.el-button-group) {
    .el-button:first-child {
      border-top-right-radius: 0;
      border-bottom-right-radius: 0;
    }

    .el-button:last-child {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
    }

    .el-button + .el-button {
      margin-left: 0;
    }
  }
}
</style>

<script setup lang="ts">
import { Loading, Download } from '@element-plus/icons-vue'
import type { TableHead } from './type'
import { fileDownload } from '@/utils/file'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
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
  // 对话框宽度（默认 600px）
  width?: string
  // 类型
  type?: string
  // 按钮标题（默认：导出）
  title?: string
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
          const cache = localStorage.getItem(props.tableHeadUrl)
          checkedList.value = cache ? JSON.parse(cache) : tableHeadList.value?.map(e => e.key)
          tableLoading.value = false
        })
        .catch(res => {
          ElMessage.error(res.msg)
        })
    }
  }
}

const checkedList = ref<string[]>([])

// 自定义表头确认导出
const confirm = () => {
  if (!checkedList.value.length) return ElMessage.warning(i18n.t('components.excel.exportColumnNull'))
  localStorage.setItem(props.tableHeadUrl, JSON.stringify(checkedList.value))
  handleCommand(checkedList.value)
  dialogVisible.value = false
}
</script>

<template>
  <span>
    <!-- 可选列导出 -->
    <el-dropdown
      v-if="tableHeadUrl || options"
      split-button
      :type="type"
      @click="exportData()"
      @command="handleCommand"
    >
      <el-icon style="margin-right: 5px">
        <component :is="exportLoadingData ? Loading : Download" />
      </el-icon>
      {{ title ?? $t('operation.export') }}
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-if="tableHeadUrl" command="select">{{
            $t('components.excel.selectColumnExport')
          }}</el-dropdown-item>
          <el-dropdown-item v-for="(value, key, index) in options" :key="index" :divided="index === 0" :command="value">
            {{ key }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 固定模板导出 -->
    <el-button v-else :icon="exportLoadingData ? Loading : Download" :type="type" @click="exportData()">
      {{ title ?? $t('operation.export') }}
    </el-button>

    <!-- 导出列选择 -->
    <el-dialog v-model="dialogVisible" :width="width ?? '600px'" :title="$t('components.excel.selectExportColumn')">
      <el-transfer
        v-model="checkedList"
        :data="tableHeadList"
        :titles="[$t('components.excel.ignoreColumn'), $t('components.excel.exportColumn')]"
        :props="{ key: 'key', label: 'title' }"
      />
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('button.cancel') }}</el-button>
        <el-button type="primary" @click="confirm">{{ $t('button.confirm') }}</el-button>
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

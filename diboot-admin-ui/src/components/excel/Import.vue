<script setup lang="ts">
import { Loading, Download, Upload, View } from '@element-plus/icons-vue'
import { ElTableColumn } from 'element-plus'
import type { UploadUserFile } from 'element-plus'
import type { PropType, VNode } from 'vue'
import type { TableHead, ExcelPreview, ExcelImport } from './type'
import { fileDownload } from '@/utils/file'

const props = defineProps<{ excelBaseApi: string; attach?: () => Record<string, unknown>; width?: string }>()

const emit = defineEmits<{ (e: 'complete'): void }>()

const visible = ref(false)

// 下载示例文件
const downloadLoading = ref(false)
const downloadExample = () => {
  downloadLoading.value = true
  fileDownload(`${props.excelBaseApi}/download-example`)?.finally(() => (downloadLoading.value = false))
}

const uploadDisabled = ref(true)
const previewDisabled = ref(true)
const completeLoading = ref(false)
const description = ref('')
const fileList: UploadUserFile[] = reactive([])

watch(visible, value => {
  if (value) return
  description.value = ''
  removeFile()
})

// 移除文件
const removeFile = () => {
  fileList.length = 0
  data.value = undefined
  errMsg.value = undefined
  uploadDisabled.value = true
  previewDisabled.value = true
}
// 文件列表变化
const fileListChange = (uploadFile: UploadUserFile) => {
  fileList.length = 0
  fileList.push(uploadFile)
  data.value = undefined
  errMsg.value = undefined
  uploadDisabled.value = false
  previewDisabled.value = false
}

const data = ref<ExcelPreview & ExcelImport>()
const errMsg = ref<string>()

// 发送请求
const sendRequest = (url: string, formData: FormData) => {
  formData.append('description', description.value)
  if (props.attach) {
    const attach = props.attach()
    for (const key in attach) {
      formData.append(key, attach[key] as string)
    }
  }
  uploadDisabled.value = true
  previewDisabled.value = true
  completeLoading.value = true
  api
    .upload<ExcelPreview & ExcelImport>(url, formData)
    .then(res => {
      if (!res.data) visible.value = false
      else data.value = res.data
      if (!res.data?.tableHeads) emit('complete')
      uploadDisabled.value = false
    })
    .catch(err => {
      errMsg.value = err.msg
    })
    .finally(() => {
      previewDisabled.value = false
      completeLoading.value = false
    })
}

// 导入预览
const handlePreview = () => {
  const formData = new FormData()
  formData.append('file', fileList[0].raw as Blob)
  sendRequest(`${props.excelBaseApi}/preview`, formData)
}

// 上传数据
const handleUpload = () => {
  const formData = new FormData()
  if (data.value && data.value.id) {
    formData.append('id', data.value.id)
    sendRequest(`${props.excelBaseApi}/preview-save`, formData)
  } else {
    formData.append('file', fileList[0].raw as Blob)
    sendRequest(`${props.excelBaseApi}/upload`, formData)
  }
}

// 导出错误数据
const exportErrorLoading = ref(false)
const exportErrorData = (url?: string) => {
  if (!url) return
  exportErrorLoading.value = true
  fileDownload(url)?.finally(() => (exportErrorLoading.value = false))
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
        { label: column.title, prop: column.key, align: 'center', showOverflowTooltip: true },
        children.length ? { default: () => children } : {}
      )
    }
    return () => buildColumn(props.column)
  }
})
</script>

<template>
  <span>
    <span @click="visible = true">
      <slot>
        <el-button :icon="Upload" type="primary" plain> {{ $t('operation.import') }} </el-button>
      </slot>
    </span>

    <el-drawer v-model="visible" :size="width || '50%'">
      <template #header>
        <span>{{ $t('components.excel.dataUpload') }}</span>
        <el-button
          style="float: right; padding: 0 30px; zoom: 0.9"
          link
          type="primary"
          :icon="downloadLoading ? Loading : Download"
          @click="downloadExample"
        >
          {{ $t('components.excel.downloadExample') }}
        </el-button>
      </template>

      <el-row :gutter="16">
        <el-col :md="6">
          <el-upload
            :key="fileList.length && fileList[0].uid"
            v-loading.fullscreen.lock="completeLoading"
            action=""
            :limti="1"
            accept=".xlsx,.xls,.csv"
            :auto-upload="false"
            :file-list="fileList"
            :on-remove="removeFile"
            :on-change="fileListChange"
          >
            <el-button :icon="Upload"> {{ $t('components.excel.chooseFile') }}</el-button>
          </el-upload>
        </el-col>
        <el-col :md="8">
          <el-input v-model="description" :placeholder="$t('components.excel.description')" />
        </el-col>
        <el-col :md="10">
          <el-button type="primary" :disabled="previewDisabled" :icon="View" @click="handlePreview">{{
            $t('components.excel.previewData')
          }}</el-button>
          <el-button :disabled="uploadDisabled" :icon="Upload" @click="handleUpload">{{
            $t('components.excel.uploadData')
          }}</el-button>
        </el-col>
      </el-row>
      <el-alert
        v-if="errMsg"
        type="error"
        :closable="false"
        :title="$t('components.excel.checkError')"
        :description="errMsg"
      />
      <div v-if="data">
        <el-divider />
        <el-alert type="success" :closable="false">
          {{ $t('components.excel.excelParsePrefix') }} <strong>{{ data.totalCount }}</strong>
          {{ $t('components.excel.data') }}
          <span v-if="Number(data.errorCount ?? 0) > 0">
            ；<strong>{{ Number(data.totalCount) - Number(data.errorCount) }}</strong> {{ $t('components.excel.data') }}
          </span>
          {{ $t('components.excel.canUpload') }}
        </el-alert>
        <el-collapse v-if="Number(data.errorCount ?? 0) > 0" model-value="1">
          <el-collapse-item name="1">
            <template #title>
              <span style="color: red; zoom: 1.2">{{ $t('components.excel.errorMsg', [data.errorCount]) }}</span>
              （{{ $t('components.excel.uploadDataTipPrefix') }}
              <el-button
                link
                :type="data.errorUrl ? 'danger' : ''"
                :icon="exportErrorLoading ? Loading : Download"
                :class="data.errorUrl ? '' : 'shake'"
                :disabled="!data.errorUrl"
                @click.stop="exportErrorData(data?.errorUrl)"
              >
                {{ $t('components.excel.uploadDataTipSuffix') }}
              </el-button>
              ）
            </template>
            <div v-for="error in data.errorMsgs" :key="error" style="color: var(--el-color-danger)">{{ error }}</div>
            <span v-if="Number(data.errorCount ?? 0) > 20">...</span>
          </el-collapse-item>
        </el-collapse>
        <el-table v-if="data.dataList" style="width: 100%" :data="data.dataList" border>
          <table-column v-for="(item, index) in data.tableHeads" :key="index" :column="item" />
        </el-table>
      </div>
    </el-drawer>
  </span>
</template>

<style scoped lang="scss">
.shake:hover {
  animation: shake 800ms ease-in-out;
}

@keyframes shake {
  /* 水平抖动 */
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }
  20%,
  80% {
    transform: translate3d(+2px, 0, 0);
  }
  30%,
  70% {
    transform: translate3d(-3px, 0, 0);
  }
  40%,
  60% {
    transform: translate3d(+3px, 0, 0);
  }
  50% {
    transform: translate3d(-3px, 0, 0);
  }
}
</style>

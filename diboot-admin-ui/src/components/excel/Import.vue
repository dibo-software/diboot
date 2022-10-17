<script setup lang="ts">
import { Loading, Download, Upload, View } from '@element-plus/icons-vue'
import { ElTableColumn } from 'element-plus'
import type { UploadUserFile } from 'element-plus'
import type { PropType, VNode } from 'vue'
import type { TableHead, ExcelPreview, ExcelImport } from './type'
import { fileDownload } from '@/utils/file'

const props = defineProps<{ excelBaseApi: string; attach?: () => Record<string, unknown>; width?: string }>()

const visible = ref(false)

// 下载示例文件
const downloadLoading = ref(false)
const downloadExample = () => {
  downloadLoading.value = true
  fileDownload(`${props.excelBaseApi}/download-example`)?.finally(() => (downloadLoading.value = false))
}

const uploadDisabled = ref(true)
const previewDisabled = ref(true)
const importDataLoading = ref(false)
const description = ref('')
const fileList: UploadUserFile[] = reactive([])

// 移除文件
const removeFile = () => {
  uploadDisabled.value = true
  previewDisabled.value = true
}
// 文件列表变化
const fileListChange = (uploadFile: UploadUserFile) => {
  fileList.splice(0)
  fileList.push(uploadFile)
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
  importDataLoading.value = true
  api
    .upload<ExcelPreview & ExcelImport>(url, formData)
    .then(res => {
      ElMessage.success(res.msg || '上传数据成功')
      data.value = res.data
    })
    .catch(err => {
      if (err.code) errMsg.value = err.msg
      else ElMessage.error(err.message || '上传异常，请稍后重试！')
    })
    .finally(() => {
      uploadDisabled.value = false
      previewDisabled.value = false
      importDataLoading.value = false
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
  if (data.value && data.value.uuid) {
    formData.append('uuid', data.value.uuid)
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
        <el-button :icon="Upload"> 批量导入 </el-button>
      </slot>
    </span>

    <el-drawer v-model="visible" :size="width || '50%'">
      <template #header>
        <span>数据上传</span>
        <el-button
          style="float: right; padding: 0 30px; zoom: 0.9"
          link
          type="primary"
          :icon="downloadLoading ? Loading : Download"
          @click="downloadExample"
        >
          下载示例文件
        </el-button>
      </template>

      <el-row :gutter="16">
        <el-col :md="6">
          <el-upload
            action=""
            :limti="1"
            :auto-upload="false"
            :file-list="fileList"
            :on-remove="removeFile"
            :on-change="fileListChange"
          >
            <el-button :icon="Upload"> 选择文件</el-button>
          </el-upload>
        </el-col>
        <el-col :md="8">
          <el-input v-model="description" placeholder="备注信息" />
        </el-col>
        <el-col :md="10">
          <el-button type="primary" :disabled="previewDisabled" :icon="View" @click="handlePreview">预览数据</el-button>
          <el-button :disabled="uploadDisabled" type="default" :icon="Upload" @click="handleUpload">上传数据</el-button>
        </el-col>
      </el-row>
      <el-row v-if="errMsg">
        <el-col :span="24">
          <br />
          <el-alert type="error">
            <div>
              <b>请检查Excel文件，错误信息: </b><br />
              {{ errMsg }}
            </div>
          </el-alert>
        </el-col>
      </el-row>
      <div v-if="data">
        <el-divider />
        <el-alert type="success" :closable="false">
          Excel文件解析成功，共有 <strong>{{ data.totalCount }}</strong> 条数据
          <span v-if="Number(data.errorCount ?? 0) > 0">
            ；<strong>{{ Number(data.totalCount) - Number(data.errorCount) }}</strong> 条数据
          </span>
          可上传。
        </el-alert>
        <el-collapse v-if="Number(data.errorCount ?? 0) > 0" value="1">
          <el-collapse-item style="background-color: antiquewhite" name="1">
            <template #title>
              <span style="color: red; zoom: 1.2">{{ `共有 ${data.errorCount} 条数据异常` }}</span>
              （上传数据后可
              <el-button
                link
                :type="data.errorUrl ? 'danger' : ''"
                :icon="exportErrorLoading ? Loading : Download"
                :class="data.errorUrl ? '' : 'shake'"
                :disabled="!data.errorUrl"
                @click.stop="exportErrorData(data?.errorUrl)"
              >
                导出错误数据
              </el-button>
              ）
            </template>
            <div v-for="error in data.errorMsgs" :key="error">
              {{ error }}
            </div>
            <span v-if="Number(data.errorCount ?? 0) > 20">...</span>
          </el-collapse-item>
        </el-collapse>
        <el-table v-if="data.dataList" style="width: 100%" :data="data.dataList" border>
          <table-column v-for="(item, index) in data.tableHead" :key="index" :column="item" />
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

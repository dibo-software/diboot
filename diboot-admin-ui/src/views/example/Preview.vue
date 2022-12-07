<script setup lang="ts" name="Preview">
import { print } from '@/utils/print'
import type { UploadRequestOptions } from 'element-plus'

const activeKey = ref('primaryPrint')

const printOne = ref()
const printTwo = ref()
const primaryPrint = () => {
  print(printOne.value, printTwo.value)
}

const fileValue = ref<string | Blob | ArrayBuffer>('')
const httpRequest = async (options: UploadRequestOptions) => {
  const formData = new FormData()
  formData.set('file', options.file)
  api
    .upload<FileRecord>('/file/upload', formData)
    .then(res => {
      if (res.code === 0 && res.data?.accessUrl) {
        fileValue.value = res.data.accessUrl
      }
    })
    .catch(err => ElMessage.error(err.msg || err.message || '上传失败！'))
}

const wordPreview = ref()
const pdfPreview = ref()
</script>

<template>
  <el-main>
    <el-card shadow="never">
      <el-tabs v-model="activeKey" tab-position="top">
        <el-tab-pane label="普通打印" name="primaryPrint">
          <el-button type="primary" @click="primaryPrint">普通打印</el-button>
          <div style="height: 20px" />
          <div ref="printOne" class="printMain">
            <div class="item">
              打印内容1
              <el-icon>
                <el-icon-eleme-filled />
              </el-icon>
              <p class="no-print">忽略打印</p>
            </div>
          </div>
          <div ref="printTwo" class="printMain">
            <div class="item">打印内容2</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="word预览打印" name="wordPreview" lazy>
          <el-upload
            :http-request="httpRequest"
            :show-file-list="false"
            accept=".docx"
            style="display: inline-block; margin-right: 10px"
          >
            <el-button> 上传文件</el-button>
          </el-upload>
          <el-button @click="wordPreview?.print()">打印</el-button>
          <el-button @click="wordPreview?.download()">下载</el-button>
          <preview-word ref="wordPreview" :value="fileValue" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </el-main>
</template>

<style scoped>
.printMain .item {
  padding: 20px;
  border: 1px solid #409eff;
  margin-bottom: 20px;
  background: #ecf5ff;
  border-radius: 4px;
}

.printMain p {
  margin-top: 20px;
  color: #999;
}
</style>

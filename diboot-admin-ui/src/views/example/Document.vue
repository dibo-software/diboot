<script setup lang="ts" name="Document">
import print from '@/utils/print'
import type { UploadFile } from 'element-plus'
import { View } from '@element-plus/icons-vue'

const printOne = ref()
const printTwo = ref()
const primaryPrint = () => {
  print(printOne.value, printTwo.value)
}

const fileValue = ref<string | Blob | ArrayBuffer>()
const fileChange = (uploadFile: UploadFile) => {
  fileValue.value = uploadFile.raw && new Blob([uploadFile.raw])
}

const wordPreview = ref()
const pdfPreview = ref()
</script>

<template>
  <el-main class="fullHeight">
    <el-card shadow="never" class="cardHeight">
      <el-tabs tab-position="top" class="fullHeight">
        <el-tab-pane :label="$t('document.wordPreviewPrint')" lazy class="fullHeight">
          <div class="fullHeight" style="display: flex; flex-direction: column">
            <div>
              <el-upload
                :auto-upload="false"
                :show-file-list="false"
                accept=".docx"
                style="display: inline-block; margin-right: 10px"
                :on-change="fileChange"
              >
                <el-button> {{ $t('document.selectFile') }}</el-button>
              </el-upload>
              <el-button @click="wordPreview?.print()">{{ $t('document.print') }}</el-button>
              <el-button @click="wordPreview?.download()">{{ $t('document.download') }}</el-button>
            </div>
            <document-word ref="wordPreview" :value="fileValue" style="flex: 1" />
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('document.pdfPreviewPrint')" lazy class="fullHeight">
          <div class="fullHeight" style="display: flex; flex-direction: column">
            <div>
              <el-upload
                :auto-upload="false"
                :show-file-list="false"
                accept=".pdf"
                style="display: inline-block; margin-right: 10px"
                :on-change="fileChange"
              >
                <el-button> {{ $t('document.selectFile') }}</el-button>
              </el-upload>
              <el-button @click="pdfPreview?.print()">{{ $t('document.print') }}</el-button>
              <el-button @click="pdfPreview?.download()">{{ $t('document.download') }}</el-button>
            </div>
            <document-pdf ref="pdfPreview" :value="fileValue" style="flex: 1" />
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('document.pageElementPrint')">
          <el-button type="primary" @click="primaryPrint">{{ $t('document.print') }}</el-button>
          <div style="height: 20px" />
          <div ref="printOne" class="printMain">
            <div class="item">
              打印内容1
              <el-icon>
                <View />
              </el-icon>
              <p class="print-ignore">忽略打印</p>
            </div>
          </div>
          <div ref="printTwo" class="printMain">
            <div class="item">打印内容2</div>
          </div>
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

:deep(.el-card__body) {
  height: calc(100% - -50px);
}

:deep(.el-tabs__content) {
  height: calc(100% - 150px);
}

.fullHeight {
  height: 100%;
}

.cardHeight {
  height: 99%;
}
</style>

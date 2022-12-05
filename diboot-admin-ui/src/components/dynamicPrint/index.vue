<script setup lang="ts">
import { defaultOptions, renderAsync } from 'docx-preview'
import * as PdfJs from 'pdfjs-dist/legacy/build/pdf.js'
import { Print } from '@/utils/print'
import printJS from 'print-js'
import type { PDFPageProxy } from 'pdfjs-dist/types/src/display/api'
import type { PDFDocumentProxy } from 'pdfjs-dist/types/src/display/api'
PdfJs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${PdfJs.version}/pdf.worker.js`

const visible = ref(false)

const props = defineProps<{
  handleUpload?: boolean // 是否搜东选择文件
  fileUrl?: string // 后端返回文件url
  btnName?: string
}>()

watch(
  () => visible.value,
  val => {
    if (val && !props.handleUpload) {
      if (props.fileUrl && props.fileUrl?.indexOf('.docx') > -1) {
        api.get<Blob>(props.fileUrl).then(res => {
          isPdf.value = false
          nextTick(() => {
            if (res.data) {
              wordPreview(res.data)
            }
          })
        })
      } else if (props.fileUrl && props.fileUrl?.indexOf('.pdf') > -1) {
        isPdf.value = true
        nextTick(() => {
          loadFile(props.fileUrl || '')
        })
      } else {
        nextTick(() => {
          ElMessage('未找到文件')
        })
      }
    }
  }
)

const chooseFile = () => {
  const fileEle = document.getElementById('file')
  fileEle?.click()
}

/**
 * 打印
 */
const print = () => {
  if (!isPdf.value) {
    Print([document.getElementById('container')])
  } else {
    printJS({ printable: pdfFileUrl || props.fileUrl })
  }
}

const isPdf = ref() // 是否是pdf文件
const container = ref() // 展示word的div
const renderContext = ref(null) // 展示pdf的canvas
let pdfFileUrl = '' // 手动创建的pdf的url
let pdfDoc: PDFDocumentProxy // 保存加载的pdf文件流
const pdfPages = ref(0) // pdf文件的页数
const pdfScale = 1.0 // 缩放比例

/**
 * 上传文件改变通过文件类型选择调用方法
 * @param event
 */
const fileChange = (event: Event) => {
  const file = ref()
  const fileElement = document.getElementById('file') as HTMLInputElement
  if (fileElement && fileElement.files) {
    file.value = fileElement.files[0]
  }
  console.log('file.value', file.value)
  if (file.value.name.indexOf('.pdf') > -1) {
    isPdf.value = true
    nextTick(() => {
      pdfPreview(file.value)
    })
  } else if (file.value.name.indexOf('.docx') > -1) {
    isPdf.value = false
    nextTick(() => {
      wordPreview(file.value)
    })
  } else {
    ElMessage?.error('不支持此类文件')
  }
}

/**
 * 使用docx-preview预览docx文件
 * @param file
 */
const wordPreview = (file: File | Blob) => {
  renderAsync(file, container.value)
}

/**
 * 手动上传创建url
 * @param file
 */
function pdfPreview(file: File) {
  if (window.URL.createObjectURL != undefined) {
    // basic
    pdfFileUrl = window.URL.createObjectURL(file)
  } else if (window.webkitURL != undefined) {
    // webkit or chrome
    pdfFileUrl = window.webkitURL.createObjectURL(file)
  }
  loadFile(pdfFileUrl)
}

/**
 * 加载pdf文件
 * @param url
 */
const loadFile = (url: string): void => {
  const loadingTask = PdfJs.getDocument(url)
  loadingTask.promise.then(pdf => {
    pdfDoc = pdf
    pdfPages.value = pdfDoc.numPages
    nextTick(() => {
      renderPage(1) // 表示渲染第 1 页
    })
  })
}

/**
 * 渲染pdf到canvas
 * @param num
 */
const renderPage = (num: number) => {
  pdfDoc.getPage(num).then((page: PDFPageProxy) => {
    const canvasId = 'pdf-canvas-' + num // 第num个canvas画布的id
    const canvas = document.getElementById(canvasId) as any
    const ctx = canvas.getContext('2d')
    const dpr = window.devicePixelRatio || 1
    const bsr =
      ctx.webkitBackingStorePixelRatio ||
      ctx.mozBackingStorePixelRatio ||
      ctx.msBackingStorePixelRatio ||
      ctx.oBackingStorePixelRatio ||
      ctx.backingStorePixelRatio ||
      1
    const ratio = dpr / bsr
    const viewport = page.getViewport({ scale: pdfScale })
    canvas.width = viewport.width * ratio
    canvas.height = viewport.height * ratio
    canvas.style.width = viewport.width + 'px'
    canvas.style.height = viewport.height + 'px'
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0)
    const renderContext = {
      canvasContext: ctx,
      viewport: viewport
    }
    page.render(renderContext)
    // 在第num页渲染完毕后，递归调用renderPage方法，去渲染下一页，直到所有页面渲染完毕为止
    if (num < pdfPages.value) {
      renderPage(num + 1)
    }
  })
}
</script>

<template>
  <el-drawer v-model="visible" title="文件预览/打印" size="50%" destroy-on-close>
    <div class="docx-preview-wrap">
      <h4>
        <input v-if="props.handleUpload" id="file" type="file" hidden accept=".docx, .pdf" @change="fileChange" />
        <el-button v-if="props.handleUpload" @click="chooseFile">选择文件</el-button>
        <el-button :disabled="isPdf === undefined" @click="print">打印</el-button>
      </h4>
      <div v-if="!isPdf" id="container" ref="container" />
      <div v-if="isPdf" align="center">
        <canvas v-for="pageIndex in pdfPages" :id="`pdf-canvas-` + pageIndex" ref="renderContext" :key="pageIndex" />
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-drawer>
  <el-button @click="visible = true">{{ props.btnName || '动态打印' }}</el-button>
</template>

<style scoped>
/deep/.docx-wrapper {
  padding: 0;
  background: #ffffff;
}
/deep/.docx-wrapper > section.docx {
  box-shadow: 0 0 0;
  padding: 20pt 90pt !important;
}
</style>

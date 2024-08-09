const zhCN = {
  createQRCode: {
    value: '内容',
    size: '尺寸',
    foreground: '颜色',
    placeholder: {
      value: '请输入自定义内容',
      size: '请输入自定义尺寸'
    }
  },
  document: {
    wordPreviewPrint: 'word预览打印',
    pdfPreviewPrint: 'pdf预览打印',
    pageElementPrint: '页面元素打印',
    selectFile: '选择文件',
    print: '打印',
    download: '下载'
  },
  richText: {
    switch: '切换为',
    doc: '文档编辑器',
    rich: '富文本编辑器'
  },
  watermark: {
    createTextWatermark: '创建文字水印',
    createImageWatermark: '创建图片水印',
    watermarkRef: '清除水印',
    name: '名称',
    code: '编码'
  }
}

export type Locale = typeof zhCN

export default zhCN

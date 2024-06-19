const zhCN = {
  components: {
    di: {
      input: {
        uploadFormatError: '请上传{0}格式的文件！',
        fileLarge: '`文件过大，超出了限制{0}MB，请调整您的文件!`',
        uploadFile: '上传文件',
        limit: {
          prefix: '可上传',
          suffix: '文件'
        },
        accept: '类型为 {0} 的文件',
        size: '单文件需小于'
      },
      selector: {
        label: '选择'
      },
      tree: {
        searchFilter: '搜索过滤'
      },
      table: {
        total: '当前页总和',
        avg: '当前页平均',
        config: {
          title: '列表配置',
          show: '显示',
          name: '名称',
          width: '宽度',
          sort: '排序',
          fixed: '固定'
        },
        border: '纵向边框',
        stripe: '斑马纹'
      }
    },
    document: {
      fetchFileFailed: '获取文件失败',
      emptyContent: '文件内容为空'
    },
    download: '下载',
    excel: {
      dataUpload: '数据上传',
      uploadData: '上传数据',
      downloadExample: '下载示例文件',
      chooseFile: '选择文件',
      previewData: '预览数据',
      checkError: '请检查Excel文件，错误信息',
      description: '备注信息',
      excelParsePrefix: 'Excel文件解析成功，共有',
      data: '条数据',
      canUpload: '可上传',
      errorMsg: '共有 {0} 条数据异常`',
      uploadDataTipPrefix: '上传数据后可',
      uploadDataTipSuffix: '导出错误数据',
      exportColumnNull: '导出列不应为空',
      selectColumnExport: '选择列导出',
      selectExportColumn: '选择导出列',
      ignoreColumn: '忽略列',
      exportColumn: '导出列'
    },
    i18n: {
      language: '语言标识',
      config: '国际化配置'
    },
    icon: {
      reelect: '重选',
      choose: '选择',
      clear: '清除',
      selector: '图标选择器'
    },
    numberRange: {
      startPlaceholder: '起始',
      endPlaceholder: '截止'
    },
    rich: {
      placeholder: '请输入内容...',
      uploadError: '上传异常，请稍后重试！'
    },
    watermark: '浏览器不支持水印功能'
  }
}

export type Locale = typeof zhCN

export default zhCN

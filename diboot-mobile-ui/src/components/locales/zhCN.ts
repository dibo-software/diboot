const zhCN = {
  components: {
    select: {
      keyword: '输入关键字',
      searchOption: '搜索选项',
      noOption: '无选项'
    },
    rich: {
      editor: {
        placeholder: '请输入内容...'
      }
    }
  },
  di: {
    input: {
      date: '选择日期',
      time: '选择时间',
      next: '下一步',
      uploadFile: '上传文件',
      fileLarge: '文件大小不能超过 {0}MB'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN

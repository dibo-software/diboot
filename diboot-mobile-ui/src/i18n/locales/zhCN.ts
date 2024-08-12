const zhCN = {
  language: '简体中文',
  operation: {
    submit: '提交',
    detail: '查看详情',
    create: '新建',
    update: '编辑',
    delete: '删除',
    copy: '复制'
  },
  bool: {
    yes: '是',
    no: '否'
  },
  placeholder: {
    select: '请选择',
    input: '请输入'
  },
  rules: {
    notnull: '不能为空',
    validationFailed: '表单校验不通过',
    valueNotUnique: '内容重复，{0} 已存在!'
  },
  msg: {
    noMore: '没有更多了',
    requestErr: '请求失败，点击重新加载',
    loading: '加载中...',
    uploadErr: '上传文件异常，请稍后重试！'
  },
  select: {
    key: '输入关键字',
    search: '搜索选项',
    noOpt: '无选项'
  }
}

export type Locale = typeof zhCN

export default zhCN

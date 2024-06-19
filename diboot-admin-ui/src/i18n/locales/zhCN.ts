const zhCN = {
  language: '简体中文',
  operation: {
    create: '新建',
    add: '添加',
    createTenantAdmin: '管理员配置',
    createTenantResource: '权限配置',
    detail: '详情',
    update: '编辑',
    delete: '删除',
    batchDelete: '批量删除',
    import: '导入',
    export: '导出',
    more: '更多',
    search: '查询',
    reset: '重置',
    label: '操作'
  },
  rules: {
    notnull: '不能为空',
    nonpass: '校验不通过',
    pass: '校验通过'
  },
  button: {
    submit: '提交',
    save: '保存',
    continueAdd: '保存并继续',
    cancel: '取消',
    confirm: '确定',
    select: '选择',
    close: '关闭'
  },
  title: {
    reset: '重置搜索条件',
    detail: '详情',
    update: '更新',
    create: '新建'
  },
  bool: {
    yes: '是',
    no: '否'
  },
  placeholder: {
    select: '请选择',
    input: '请输入',
    filter: '请输入内容过滤'
  },
  baseField: {
    createTime: '创建时间',
    updateTime: '更新时间',
    createBy: '创建人',
    updateBy: '更新人'
  },
  password: {
    strong: '强',
    general: '一般',
    weak: '弱'
  },
  router: {
    dashboard: '工作台',
    personal: '个人中心'
  },
  searchState: {
    up: '收起',
    down: '展开'
  }
}

export type Locale = typeof zhCN

export default zhCN

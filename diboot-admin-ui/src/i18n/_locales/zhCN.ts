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
  },
  hooks: {
    fetchDetailFailed: '获取详情失败',
    saveFailed: '保存失败',
    fetchListFailed: '获取列表数据失败',
    confirmDelete: '确认删除该数据吗？',
    confirmDelete0: '确认删除“ {0} ”吗？',
    delete: '删除',
    deleteFailed: '删除失败',
    deleteSuccess: '删除成功',
    nonChooseData: '未选择数据',
    confirmBatchDelete: '确认删除已选数据吗？',
    batchDelete: '批量删除',
    initOptionFailed: '初始化选项数据失败',
    fetchOptionFailed: '获取选项数据失败',
    sortFailed: '排序失败',
    loginFailed: '登录失败',
    nullSsoLoginUrl: '单点登录方式为空',
    fetchTreeFailed: '获取树列表数据失败',
    addDataFailed: '添加数据失败',
    confirmDeleteNode: '确认删除节点吗？',
    sortSuccess: '排序成功'
  },
  copy: {
    label: '复制',
    success: '复制成功',
    error: '写入剪切板失败，请手动选中复制'
  },
  utils: {
    request: {
      expiredLogin: '登录过期，请重新登录',
      server500: '服务器好像开小差了，重试下吧！',
      server400: '保存数据出错',
      server401: '没有权限',
      server403: '无权访问',
      server404: '请求资源不存在',
      serverError: '网络可能出现问题'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN

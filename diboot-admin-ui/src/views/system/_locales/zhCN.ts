const zhCN = {
  client: {
    name: '客户端名',
    status: '状态',
    permissions: '接口权限',
    updateKey: '重新生成密钥',
    viewLogs: '访问日志',
    namePlaceholder: '请输入客户端名',
    statusPlaceholder: '请选择状态',
    appKeyPlaceholder: '请输入 AppKey',
    appSecretPlaceholder: '自动生成 AppSecret',
    permissionsPlaceholder: '请选择接口权限'
  },
  config: {
    propKey: '属性名',
    propLabel: '属性标签',
    propValue: '属性值',
    category: '类别',
    dataType: '填写类型',
    dataTypeOptions: {
      text: '文本',
      textarea: '文本域',
      number: '数字',
      boolean: '开关'
    },
    fetchException: '获取配置信息异常',
    saveException: '保存配置信息异常',
    ext: '扩展配置'
  },
  dictionary: {
    itemName: '字典名称',
    type: '字典编码',
    description: '字典备注',
    item: {
      label: '字典条目',
      itemName: '条目名称',
      itemValue: '条目编码',
      color: '条目颜色',
      internationalization: '国际化',
      sort: '排序',
      rules: {
        itemName: '请输入条目名称',
        itemValue: '请输入条目编码'
      }
    },
    rules: {
      itemName: '请输入字典名称',
      type: '请输入字典编码'
    },
    paramsError: '参数错误'
  },
  fileRecord: {
    appModule: '业务模块',
    fileName: '文件名称',
    fileType: '文件类型',
    fileSize: '文件大小',
    accessUrl: '访问地址',
    description: '备注',
    editDescription: '编辑备注',
    placeholder: {
      description: '请输入备注'
    }
  },
  i18nConfig: {
    i18nCode: '资源标识',
    translate: '翻译',
    type: '类型',
    content: '翻译内容',
    internationalTranslation: '国际化翻译',
    rules: {
      i18nCode: '只允许字，数字， . ， - ， _'
    },
    fetchDataListError: '获取列表数据失败'
  },
  loginTrace: {
    userTypeId: '姓名',
    authAccount: '用户名',
    ipAddress: '登录IP',
    authType: '登录方式',
    success: '登录状态',
    successStatus: {
      yes: '成功',
      no: '失败'
    },
    onlineStatusLabel: '在线状态',
    onlineStatus: {
      online: '在线',
      logout: '已退出',
      unknown: '-',
      invalid: '已失效'
    },
    browserInfo: '浏览器',
    osInfo: '操作系统',
    createTime: '登录时间',
    logoutTime: '退出时间',
    forceLogout: '强退',
    formLogoutMessage: {
      confirmContent: '确认强制退出该用户？',
      success: '强退成功',
      failed: '强退失败'
    },
    placeholder: {
      start: '开始时间',
      end: '结束时间',
      successStatus: '请选择登录状态'
    }
  },
  message: {
    businessType: '业务类型',
    businessCode: '业务标识',
    title: '标题',
    content: '内容',
    senderName: '发送方',
    receiverName: '接收方',
    channel: '发送通道',
    result: '发送结果',
    status: '消息状态',
    scheduleTime: '定时发送时间',
    createTime: '发送日期',
    placeholder: {
      title: '消息标题',
      channel: '请选择发送通道',
      status: '请选择消息状态',
      createTime: '发送日期'
    }
  },
  messageTemplate: {
    title: '模版标题',
    code: '模版编码',
    appModule: '业务模块',
    content: '内容',
    placeholder: {
      title: '消息模板名称，如：‘催办提醒’',
      code: '模板唯一编码，如：‘CBTX’',
      appModule: '请输入业务模块名称',
      content: '发送日期'
    },
    selectableVariables: '可选变量',
    addVariable: '添加此变量'
  },
  operationLog: {
    business: '业务操作日志',
    client: '接口服务日志',
    exception: '系统异常日志',
    user: '用户',
    userRealname: '用户姓名',
    userTypeAndId: '用户类型:ID',
    userType: '用户类型',
    businessObj: '业务对象',
    operation: '操作事项',
    requestMethod: '请求方式',
    requestUri: '请求URL',
    requestIp: '客户端IP',
    statusCode: '状态码',
    createTime: '操作时间',
    requestParams: '请求参数',
    errorMsg: '错误信息',
    placeholder: {
      requestMethod: '请选择请求方式'
    }
  },
  resource: {
    main: '主菜单资源',
    mobile: {
      title: '移动端菜单资源',
      parentId: '上级',
      parentPlaceholder: '请选择上级',
      displayName: '名称',
      displayNamePlaceholder: '请输入名称',
      resourceCode: '编码',
      resourceCodePlaceholder: '请输入编码'
    },
    newButtonPermission: '新按钮权限',
    parentId: '上级目录',
    parentId0: '顶级目录',
    displayType: '菜单分类',
    displayName: '菜单名称',
    routeMetaIcon: '菜单图标',
    routePath: '路由路径',
    resourceCode: '路由名称',
    componentPath: '组件路径',
    redirectPath: '重定向',
    routeMetaUrl: '外部链接',
    appModule: '应用模块',
    permissionCodes: '菜单权限',
    otherConfig: '其他配置',
    status: '可用',
    hidden: '隐藏',
    keepAlive: '缓存',
    ignoreAuth: '忽略认证',
    buttonPermission: '按钮权限配置',
    buttonPermissionConfig: {
      resourceCode: '按钮权限编码',
      displayName: '按钮权限名称',
      permissionCodes: '按钮权限接口'
    },
    displayTypeOptions: {
      catalogue: '目录',
      menu: '菜单',
      outsideUrl: '外链'
    },
    otherConfigDesc: {
      status: '可用：控制菜单是否生效；',
      hidden: '隐藏：隐藏时菜单栏不会显示，但地址可以访问；',
      keepAlive: '缓存：页面开启keepAlive，缓存当前页面；',
      ignoreAuth: '忽略认证：当前页面访问不需要权限认证。'
    },
    placeholder: {
      displayName: '请输入菜单名称',
      routePath: '路由地址（例：route-path）',
      resourceCode: '路由名称（例：RouteName）',
      redirectPath: '请输入重定向',
      routeMetaUrl: '请输入外部链接',
      permissionCodes: '点击聚焦后在权限列表中选择',
      componentName: '请输入组件名称',
      componentPath: "路由组件路径（例: {'@'}/views/user/List.vue）",
      buttonPermissionConfig: {
        _customCode: '请输入按钮权限编码',
        resourceCode: '请选取当前按钮权限编码',
        displayName: '请输入按钮权限名称'
      }
    },
    selectMenuAndOperate: '选择左侧菜单后操作',
    menuConfig: '菜单配置',
    switchButton: '输入/选择 切换',
    addChild: '添加子菜单',
    componentNameChange: '组件名称变更，以免页面缓存不生效，请重新选择！',
    componentNonExist: '组件不存在，将无法加载菜单，请重新选择！',
    permissionSelect: {
      title: '菜单分类 可配置权限接口',
      permissionApi: '配置权限接口',
      searchPlaceholder: '搜索需要设置的接口：支持标题、权限码、接口地址模糊搜索',
      tip: '选择 应用模块 后配置权限'
    }
  },
  role: {
    name: '名称',
    code: '编码',
    userList: '角色人员',
    description: '备注',
    grantPermission: '授权权限',
    permissionList: '角色授权'
  },
  scheduleJob: {
    jobKey: '定时任务',
    jobKeyAlias: '执行类',
    jobName: '任务名称',
    cron: '定时表达式',
    cronAlias: '定时规则',
    cronExplain: {
      grammar: '格式： * * 1 * * ?',
      implication: '含义: 秒 分 时 日 月 星期 年'
    },
    onlineEditor: '在线编辑器',
    initStrategy: '初始化策略',
    initStrategyAlias: '执行策略',
    paramJson: '参数',
    jobStatus: '状态',
    saveLog: '日志',
    saveLogAlias: '日志状态',
    jobComment: '备注',
    open: '启用',
    close: '停用',
    ready: '准备就绪',
    retry: '稍后重试',
    immediately: '确认立即执行一次吗?'
  },
  scheduleJobLog: {
    startTime: '开始时间',
    startTimeAlias: '执行时间',
    endTime: '结束时间',
    runStatus: '执行状态',
    triggerMode: '触发方式',
    elapsedSeconds: '耗时',
    paramJson: '参数',
    executeMsg: '执行结果信息',
    success: '成功',
    fail: '失败',
    title: '日志',
    triggerModeOptions: {
      auto: '自动',
      manual: '手动'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN

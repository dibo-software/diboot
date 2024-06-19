const zhCN = {
  resource: {
    newButtonPermission: '新按钮权限',
    parentId: '上级目录',
    parentId0: '顶级目录',
    displayType: '菜单分类',
    displayName: '菜单名称',
    routeMetaIcon: '菜单图标',
    routePath: '路由路径',
    resourceCode: '路由名称',
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
      routePath: '请输入路由地址（例：route-path）',
      resourceCode: '请输入路由名称（例：RouteName）',
      redirectPath: '请输入重定向',
      routeMetaUrl: '请输入外部链接',
      permissionCodes: '点击聚焦后在权限列表中选择',
      componentName: '请选择组件',
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
  }
}

export type Locale = typeof zhCN

export default zhCN

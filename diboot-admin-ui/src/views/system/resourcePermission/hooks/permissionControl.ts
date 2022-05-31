import { ResourcePermission } from '@/views/system/resourcePermission/type'
type LabelValue = { label: string; value: string }
export default () => {
  // 按钮权限编码（选择/输入）
  const btnResourceCodeSelect = ref(true)
  // 正在配置的标题
  const configPermissionTitle = ref('菜单页面接口配置')
  // 正在配置的ResourceCode，如：ResourcePermission（菜单code）、detail（按钮权限code）、list等
  const configResourceCode = ref<string>('')
  // 正在配置的后端权限码，如：ResourcePermission:read、ResourcePermission:write
  const configPermissionCodes = ref<string[]>([])

  const resourcePermissionCodeOptions: LabelValue[] = []

  /**
   * 初始化权限编码字典
   * @param options
   */
  const initResourcePermissionCodeOptions = (options: LabelValue[]) => {
    resourcePermissionCodeOptions.length = 0
    resourcePermissionCodeOptions.push(...options)
  }
  /**
   * 更改按钮权限编码触发
   * @param permission 当前正在操作的权限
   * @param value 按钮权限编码
   */
  const changeBtnResourceCode = (permission: ResourcePermission, value: string) => {
    const validOption = resourcePermissionCodeOptions.find(item => item.value === value)
    // 自动补全按钮权限名称
    if (validOption) permission.displayName = validOption.label
    // 触发按钮权限名称更改
    changeBtnPermissionName(permission, permission.displayName || value)
  }

  /**
   * 更改按钮权限名称触发配置
   * @param permission 当前正在操作的权限
   * @param value 按钮权限名称
   */
  const changeBtnPermissionName = (permission: ResourcePermission, value: string) => {
    configPermissionTitle.value = `${value || permission.displayName || permission.resourceCode} 按钮权限接口配置`
    configResourceCode.value = permission.resourceCode ?? ''
    configPermissionCodes.value = permission.permissionCodes ?? []
  }

  /**
   * 切换按钮权限输入/选择
   * @param permission 当前正在配置的权限
   */
  const toggleBtnResourceCodeSelect = (permission: ResourcePermission) => {
    btnResourceCodeSelect.value = !btnResourceCodeSelect.value
    permission.resourceCode = ''
    permission.displayName = ''
  }

  return {
    btnResourceCodeSelect,
    configPermissionTitle,
    configResourceCode,
    configPermissionCodes,
    initResourcePermissionCodeOptions,
    changeBtnResourceCode,
    changeBtnPermissionName,
    toggleBtnResourceCodeSelect
  }
}

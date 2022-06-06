import { ResourcePermission } from '@/views/system/resourcePermission/type'
import type { RestPermission } from '../type'

type LabelValue = { label: string; value: string }
export default () => {
  // 按钮权限编码（选择/输入）
  const btnResourceCodeSelect = ref(true)
  // 正在配置的标题
  const configPermissionTitle = ref('菜单页面接口配置')
  // 正在配置的ResourceCode，如：ResourcePermission（menu）、detail（按钮权限code）、list等
  const configResourceCode = ref<string>('menu')
  // 正在配置的后端权限码，如：ResourcePermission:read、ResourcePermission:write
  const configPermissionCodes = ref<string[]>([])
  // 前端按钮权限资源编码
  const resourcePermissionCodeOptions: LabelValue[] = []
  // 后端权限资源
  const restPermissions: RestPermission[] = reactive([])

  /**
   * 初始化权限编码字典
   * @param options
   */
  const initResourcePermissionCodeOptions = (options: LabelValue[]) => {
    resourcePermissionCodeOptions.length = 0
    resourcePermissionCodeOptions.push(...options)
  }

  /**
   * 初始化接口权限
   * @param options
   */
  const initRestPermissions = (restAPi: string) => {
    restPermissions.length = 0
    api.get<RestPermission[]>(restAPi).then(res => {
      if (res.code === 0) {
        restPermissions.push(...(res.data ?? []))
      } else {
        ElMessage?.error(res.msg)
      }
    })
  }

  /**
   * 初始化响应式数据
   * @param options
   */
  const initReactiveData = (permissionCodes: string[]) => {
    btnResourceCodeSelect.value = true
    configPermissionTitle.value = '菜单 页面接口配置'
    configResourceCode.value = 'menu'
    configPermissionCodes.value = permissionCodes
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

  /**
   * 打开配置指定resourceCode权限
   * @param resourceCode
   * @param permission
   */
  const clickConfigPermission = (permission: ResourcePermission, isBtnPermission = true) => {
    configResourceCode.value = isBtnPermission ? permission.resourceCode ?? '' : 'menu'
    configPermissionCodes.value = permission.permissionCodes ?? []
    configPermissionTitle.value = !isBtnPermission
      ? '菜单 页面接口配置'
      : `${permission.displayName || permission.resourceCode} 按钮权限接口配置`
  }

  return {
    btnResourceCodeSelect,
    configPermissionTitle,
    configResourceCode,
    configPermissionCodes,
    restPermissions,
    initRestPermissions,
    initResourcePermissionCodeOptions,
    initReactiveData,
    changeBtnResourceCode,
    changeBtnPermissionName,
    toggleBtnResourceCodeSelect,
    clickConfigPermission
  }
}

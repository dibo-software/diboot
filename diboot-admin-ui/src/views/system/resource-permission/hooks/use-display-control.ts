/**
 * 控制字段显示
 */
export interface ControlField {
  selectResourceCode: boolean
  redirectPath: boolean
  permissionList: boolean
  permissionCodes: boolean
  appModule: boolean
}
// 默认配置
const defaultFalseConfig: ControlField = {
  selectResourceCode: false,
  redirectPath: false,
  permissionList: false,
  permissionCodes: false,
  appModule: false
}
const displayFieldsMap = {
  CATALOGUE: Object.assign(_.clone(defaultFalseConfig), { redirectPath: true }),
  MENU: {
    selectResourceCode: true,
    redirectPath: false,
    permissionList: true,
    permissionCodes: true,
    appModule: true
  },
  OUTSIDE_URL: defaultFalseConfig,
  IFRAME: defaultFalseConfig
}
export type MenuType = keyof typeof displayFieldsMap
export default () => {
  // 存储展示的字段
  const displayFields = ref<ControlField>()
  // 切换type 更换需要展示的字段
  const changeDisplayType = (type?: MenuType) => {
    displayFields.value = displayFieldsMap[type ?? 'MENU']
  }
  return {
    displayFields,
    changeDisplayType
  }
}

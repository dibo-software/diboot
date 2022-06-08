export interface ControlField {
  selectResourceCode: boolean
  redirectPath: boolean
  permissionList: boolean
  permissionCodes: boolean
}
const defaultTrueConfig: ControlField = {
  selectResourceCode: true,
  redirectPath: true,
  permissionList: true,
  permissionCodes: true
}
const defaultFalseConfig: ControlField = {
  selectResourceCode: false,
  redirectPath: false,
  permissionList: false,
  permissionCodes: false
}
const displayFieldsMap = {
  CATALOGUE: Object.assign(defaultFalseConfig, { redirectPath: true }),
  MENU: Object.assign(defaultTrueConfig, { redirectPath: false }),
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

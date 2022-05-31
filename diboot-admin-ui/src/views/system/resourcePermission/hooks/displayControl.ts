export interface ControlField {
  selectResourceCode: boolean
  redirectPath: boolean
  permissionList: boolean
}
export type MenuType = 'CATALOGUE' | 'MENU' | 'OUTSIDE_URL' | 'IFRAME'
const displayFieldsMap: { [menu in string]: ControlField } = {
  CATALOGUE: {
    selectResourceCode: false,
    redirectPath: true,
    permissionList: false
  },
  MENU: {
    selectResourceCode: true,
    redirectPath: false,
    permissionList: true
  },
  OUTSIDE_URL: {
    selectResourceCode: false,
    redirectPath: false,
    permissionList: false
  },
  IFRAME: {
    selectResourceCode: false,
    redirectPath: false,
    permissionList: false
  }
}

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

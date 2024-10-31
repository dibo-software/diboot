import useAuthStore from '@/store/auth'
import router from '@/router'

/**
 * 角色权限校验
 *
 * @param {string | Array} value 校验值
 * @param {boolean} not 取反
 * @param {boolean} all 全部
 * @returns {Boolean}
 */
export function checkRole(value: string | Array<string>, not = false, all = false) {
  if (value && value.length) {
    const roles = (useAuthStore().roles ?? []).map(role => role.code)
    const permissionRoles = value instanceof Array ? value : [value]
    const findFn = (role: string) => roles.includes(role)
    const exist = all ? permissionRoles.every(findFn) : permissionRoles.some(findFn)
    return not ? !exist : exist
  } else {
    console.error(`need roles!`)
    return false
  }
}

/**
 * 字符权限校验
 *
 * @param {string | Array} value 校验值
 * @param {boolean} not 取反
 * @param {boolean} all 全部
 * @param {string} routeName 路由名称(可选，默认当前路由)
 * @returns {Boolean}
 */
export function checkPermission(value: string | Array<string>, not = false, all = false, routeName?: string) {
  if (value && value.length) {
    let _router = useRouter()
    if (_router == null) _router = router
    const permissions = routeName
      ? (_router.getRoutes().find(e => e.name === routeName)?.meta?.permissions ?? [])
      : (_router.currentRoute.value.meta?.permissions ?? [])
    const permissionList = value instanceof Array ? value : [value]
    const findFn = (permission: string) => permissions.includes(permission)
    const exist = all ? permissionList.every(findFn) : permissionList.some(findFn)
    return not ? !exist : exist
  } else {
    console.error(`need permissions!`)
    return false
  }
}

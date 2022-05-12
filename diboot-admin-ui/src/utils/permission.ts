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
    const roles = useAuthStore().roles
    const permissionRoles = value instanceof Array ? value : [value]
    const findFn = (role: string) => permissionRoles.includes(role)
    const exist = all ? roles.every(findFn) : roles?.some(findFn)
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
 * @returns {Boolean}
 */
export function checkPermission(value: string | Array<string>, not = false, all = false) {
  if (value && value.length) {
    const permissions = router.currentRoute.value.meta?.permissions ?? []
    const permissionList = value instanceof Array ? value : [value]
    const findFn = (permission: string) => permissionList.includes(permission)
    const exist = all ? permissions.every(findFn) : permissions.some(findFn)
    return not ? !exist : exist
  } else {
    console.error(`need permissions!`)
    return false
  }
}

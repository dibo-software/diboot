/**
 * 是否为外部链接
 *
 * @param {string} path
 * @returns {Boolean}
 */
export const isExternal = (path: string) => /^(https?:|mailto:|tel:|\/\/)/.test(path)

/**
 * 有效网站
 *
 * @param {string} url
 * @returns {Boolean}
 */
export const validURL = (url: string) => {
  const reg =
    /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

/**
 * 有效网站
 * @param {string} email
 * @returns {Boolean}
 */

export const validEmail = (email: string) => {
  const reg =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(email)
}

/**
 * 是否为字符串
 *
 * @param {string} str
 * @returns {Boolean}
 */
export const isString = (str: unknown) => {
  return typeof str === 'string' || str instanceof String
}

/**
 * 是否为数组
 *
 * @param {Array} arg
 * @returns {Boolean}
 */
export function isArray(arg: unknown) {
  if (typeof Array.isArray === 'undefined') {
    return Object.prototype.toString.call(arg) === '[object Array]'
  }
  return Array.isArray(arg)
}

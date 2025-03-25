/**
 * 驼峰 转为 蛇形
 *
 * @param value
 * @param between default '_'
 */
export const hump2Line = (value: string, between = '_') => value.replace(/(.)([A-Z])/g, `$1${between}$2`).toLowerCase()

/**
 * 蛇形 转为 驼峰
 *
 * @param value
 * @param between default '_'
 */
export const line2Hump = (value: string, between = '_') =>
  value.toLowerCase().replace(RegExp(`${between}\\w`, 'g'), str => str.charAt(1).toUpperCase())

/**
 * 首字母大写
 *
 * @param value
 */
export const capitalize = (value: string) => value.charAt(0).toUpperCase() + value.slice(1)

/**
 * 插入字符
 *
 * @param str 原字符串
 * @param char 插入字符
 * @param index 插入位置
 */
export const insertCharacter = (str: string, char: string, index: number) =>
  str.slice(0, index) + (str.length > index ? char + str.slice(index) : '')

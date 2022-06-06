/**
 *  document相关工具类
 */

/**
 * 获取窗口高度
 */
export const getWindowHeight = () =>
  window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
/**
 * 获取元素高度
 */
export const getElementHeight = (selector: string) => {
  return document.querySelector(selector)?.clientHeight || 0
}

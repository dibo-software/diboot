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
  return document.querySelector(selector)?.clientHeight ?? 0
}
/**
 * 获取元素绝对位置
 */
export const getElementAbsoluteLocation = (
  element: HTMLElement | null
): { absoluteTop: number; absoluteLeft: number; offsetWidth: number; offsetHeight: number } | null => {
  if (!element) return null
  let offsetTop = element.offsetTop,
    offsetLeft = element.offsetLeft
  const offsetWidth = element.offsetWidth,
    offsetHeight = element.offsetHeight
  // 向上查父元素
  while ((element = element.offsetParent ? (element.offsetParent as HTMLElement) : null)) {
    offsetTop += element.offsetTop
    offsetLeft += element.offsetLeft
  }
  return { absoluteTop: offsetTop, absoluteLeft: offsetLeft, offsetWidth, offsetHeight }
}

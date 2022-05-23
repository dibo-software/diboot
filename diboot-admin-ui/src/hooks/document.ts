/**
 *  document相关hooks
 */

/**
 * 获取窗口高度
 */
export const useWindowHeight = (): number =>
  window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight

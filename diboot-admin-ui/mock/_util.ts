import type { Recordable } from 'vite-plugin-mock'

// 分页
export const pagination = <T = unknown>(pageNo: number, pageSize: number, array: T[]): T[] => {
  const offset = (pageNo - 1) * Number(pageSize)
  return offset + Number(pageSize) >= array.length
    ? array.slice(offset, array.length)
    : array.slice(offset, offset + Number(pageSize))
}

/**
 * 接口请求
 */
export interface ApiRequest<B = Recordable, Q = B, H = Recordable> {
  url: string
  body: B
  query: Q
  headers: H
}

const resultJson = (code: number, msg: string, data?: unknown, ext = {}) => ({
  ...ext,
  code,
  data,
  msg
})

/**
 * 通用数据返回
 */
export const JsonResult = {
  /**
   * 操作成功
   */
  OK(data?: unknown, msg = '操作成功') {
    return resultJson(0, msg, data)
  },

  /**
   * 数据分页
   */
  PAGINATION<T = unknown>(page: number, pageSize: number, list: T[] = []) {
    page = page ? page : 1
    pageSize = pageSize ? pageSize : 20
    return resultJson(0, '操作成功', pagination(page, pageSize, list), {
      page: {
        pageIndex: Number(page),
        pageSize: Number(pageSize),
        totalCount: list.length
      }
    })
  },

  /**
   * 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
   */
  WARN_PARTIAL_SUCCESS(msg?: string) {
    return resultJson(1001, '部分成功' + (msg ? `:${msg}` : ''))
  },

  /**
   * 有潜在的性能问题
   */
  WARN_PERFORMANCE_ISSUE(msg?: string) {
    return resultJson(1002, '潜在的性能问题' + (msg ? `:${msg}` : ''))
  },

  /**
   * 传入参数不对
   */
  FAIL_INVALID_PARAM(msg?: string) {
    return resultJson(4000, '请求参数不匹配' + (msg ? `:${msg}` : ''))
  },

  /**
   * Token无效或已过期
   */
  FAIL_INVALID_TOKEN(msg?: string) {
    return resultJson(4001, 'Token无效或已过期' + (msg ? `:${msg}` : ''))
  },

  /**
   * 没有权限执行该操作
   */
  FAIL_NO_PERMISSION(msg?: string) {
    return resultJson(4003, '无权执行该操作' + (msg ? `:${msg}` : ''))
  },

  /**
   * 请求资源不存在
   */
  FAIL_NOT_FOUND(msg?: string) {
    return resultJson(4004, '请求资源不存在' + (msg ? `:${msg}` : ''))
  },

  /**
   * 数据校验不通过
   */
  FAIL_VALIDATION(msg?: string) {
    return resultJson(4005, '数据校验不通过' + (msg ? `:${msg}` : ''))
  },

  /**
   * 操作执行失败
   */
  FAIL_OPERATION(msg?: string) {
    return resultJson(4006, '操作执行失败' + (msg ? `:${msg}` : ''))
  },

  /**
   * 请求连接超时
   */
  FAIL_REQUEST_TIMEOUT(msg?: string) {
    return resultJson(4008, '请求连接超时' + (msg ? `:${msg}` : ''))
  },

  /**
   * 认证不通过（用户名密码错误等认证失败场景）
   */
  FAIL_AUTHENTICATION(msg?: string) {
    return resultJson(4009, '认证不通过' + (msg ? `:${msg}` : ''))
  },

  /**
   * 系统异常
   */
  FAIL_EXCEPTION(msg?: string) {
    return resultJson(5000, '系统异常' + (msg ? `:${msg}` : ''))
  }
}

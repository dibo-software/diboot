/**
 * 登录记录
 */
export interface LoginTrace {
  id: string
  // 用户类型
  userType: string
  // 用户ID
  userId?: string
  // 认证方式
  authType: string
  // 用户名
  authAccount: string
  // 是否成功
  isSuccess: boolean
  // IP地址
  ipAddress: string
  // 客户端信息
  userAgent: string
  // 登录时间
  createTime: string
  // 退出时间
  logoutTime: string
}

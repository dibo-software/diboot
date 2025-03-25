/**
 * 客户端
 */
export interface Client {
  // 唯一标识
  id?: string
  // 名称
  name?: string
  // AppKey
  appKey?: string
  // AppSecret
  appSecret?: string
  // 状态
  status?: string
  statusLabel?: LabelValue<{ color?: string }>
  // 权限
  permissions?: string
  // 创建人
  createBy?: string
  createByLabel?: string
  // 创建时间
  createTime?: string
  // 更新人
  updateBy?: string
  updateByLabel?: string
  // 更新时间
  updateTime?: string
}

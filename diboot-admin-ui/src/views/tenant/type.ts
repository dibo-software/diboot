/**
 * 租户
 */
export interface Tenant {
  // ID
  id?: string
  // 租户名称
  name?: string
  // 租户编码
  code?: string
  // 有效开始日期
  startDate?: string
  // 有效结束日期
  endDate?: string
  // 负责人
  manager?: string
  // 联系电话
  phone?: string
  // 描述
  description?: string
  // 租户状态
  status?: string
  statusLabel?: LabelValue
  // 创建人
  createBy?: string
  createByLabel?: string
  // 创建时间
  createTime?: string
  // 更新时间
  updateTime?: string
}

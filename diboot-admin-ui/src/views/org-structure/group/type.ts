/**
 * 用户组
 */
export interface Group {
  // 唯一标识
  id: string
  // 名称
  name: string
  // 组织
  orgId?: string
  orgLabel?: string
  // 成员
  members: string[]
  membersLabel?: string[]
  // 备注
  description?: string
  // 创建时间
  createTime: string
  // 更新时间
  updateTime?: string
}

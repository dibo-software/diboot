import type { Role } from '@/views/system/role/type'
import type { Position, UserPosition } from '@/views/org-structure/position/type'

export interface UserModel {
  id?: string
  orgId: string
  username?: string
  roleList?: Role[]
  positionList?: Position[]
  userPositionList?: UserPosition[]
  password?: string
  accountStatus?: string
  accountStatusLabel?: string
  realname: string
  userNum: string
  gender: string
  genderLabel?: LabelValue<{ color?: string }>
  birthday?: string
  mobilePhone?: string
  email?: string
  sortId?: number
  status: string
  statusLabel?: LabelValue<{ color?: string }>
  createTime?: string
  avatarUrl?: string
}

export interface AccountInfo {
  // 用户名
  authAccount: string
  authType?: string
  // 账号状态
  status: string
}

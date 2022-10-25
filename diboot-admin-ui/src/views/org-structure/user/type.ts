import type { Role } from '@/views/system/role/type'
import type { Position } from '@/views/org-structure/position/type'

export interface UserModel {
  id?: string
  orgId: string
  username?: string
  roleList?: Role[]
  positionList?: Position[]
  password?: string
  realname: string
  userNum: string
  gender: string
  genderLabel?: string
  birthday?: string
  mobilePhone?: string
  email?: string
  status: string
  statusLabel?: string
  createTime?: string
}

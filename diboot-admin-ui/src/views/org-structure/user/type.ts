import type { Role } from '@/views/system/role/type'

export interface UserModel {
  id?: string
  orgId: string
  username?: string
  roleList?: Role[]
  isSysAccount?: boolean
  hidePassword?: boolean
  userType?: string
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

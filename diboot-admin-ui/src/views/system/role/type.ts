import type { Resource } from '@/views/system/resource/type'

export interface Role {
  id: string
  name: string
  code: string
  userIdList?: string[]
  userNameList?: string[]
  permissionList?: Resource[]
  description: string
  createTime: string
  updateTime: string
}

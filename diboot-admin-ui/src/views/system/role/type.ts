import type { ResourcePermission } from '@/views/system/resourcePermission/type'

export interface Role {
  id: string
  name: string
  code: string
  permissionList?: ResourcePermission[]
  description: string
  createTime: string
  updateTime: string
}

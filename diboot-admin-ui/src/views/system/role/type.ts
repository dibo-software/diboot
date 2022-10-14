import type { ResourcePermission } from '@/views/system/resource-permission/type'

export interface Role {
  id: string
  name: string
  code: string
  permissionList?: ResourcePermission[]
  description: string
  createTime: string
  updateTime: string
}

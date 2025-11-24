import type {UserModel} from '@/views/org-structure/user/type'

export interface OrgModel {
  id?: string
  parentId: string
  topOrgId: string
  name: string
  type: string
  code: string
  sortId?: string
  managerId: string
  managerUser: UserModel
  depth: number
  orgComment?: string
  createTime: string
  updateTime: string
  children?: OrgModel[]
}

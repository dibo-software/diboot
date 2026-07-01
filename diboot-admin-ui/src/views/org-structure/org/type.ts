
export interface OrgModel {
  id?: string
  parentId: string
  topOrgId: string
  name: string
  type: string
  code: string
  area?: string
  sortId?: string
  managerId: string
  managerName: string
  managerAvatarUrl: string
  managerGender: string
  depth: number
  orgComment?: string
  createTime: string
  updateTime: string
  children?: OrgModel[]
}

export interface OrgModel {
  id?: string
  parentId: string
  topOrgId: string
  name: string
  shortName: string
  type: string
  code: string
  managerId: string
  depth: number
  orgComment?: string
  createTime: string
  children?: OrgModel[]
}

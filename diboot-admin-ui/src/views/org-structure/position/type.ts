export interface Position {
  id: string
  name: string
  code: string
  gradeName: string
  gradeValue: string
  dataPermissionType: string
  dataPermissionTypeLabel: LabelValue<{ color?: string }>
  isVirtual: boolean
  description: string
  createTime: string
  updateTime: string
}

export interface UserPosition {
  id?: string
  userType?: string
  userId: string
  positionId: string
  positionName?: string
  orgId: string
  orgName?: string
  isPrimaryPosition: boolean
  createTime?: string
}

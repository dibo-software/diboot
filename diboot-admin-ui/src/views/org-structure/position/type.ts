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
  orgId: string
  isPrimaryPosition: boolean
  createTime?: string
}

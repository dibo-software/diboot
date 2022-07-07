import type { RouteMeta } from 'vue-router'

export interface ResourcePermission {
  id?: string
  parentId: string
  appModule?: string
  parentDisplayName?: string
  displayType?: string
  displayName?: string
  routePath?: string
  redirectPath?: string
  resourceCode?: string
  permissionCodes?: Array<string>
  routeMeta: Partial<RouteMeta>
  sortId?: number
  status?: string
  children?: ResourcePermission[]
  permissionList?: ResourcePermission[]
  [key: string]: unknown
}

export interface ApiUri {
  method: string
  uri: string
  label: string
}
export interface ApiPermission {
  code: string
  label: string
  apiUriList: Array<ApiUri>
}
export interface RestPermission {
  name: string
  code: string
  apiPermissionList: Array<ApiPermission>
}
export interface SelectOption {
  title: string
  permissionCode: string
}
export interface FusePermission {
  title: string
  permissionGroup: string
  permissionCode: string
  permissionCodeLabel: string
}

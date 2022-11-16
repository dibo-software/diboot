import type { RouteMeta } from 'vue-router'

export interface Resource {
  id?: string
  parentId: string
  appModule?: string
  parentDisplayName?: string
  displayType?: string
  displayName?: string
  displayNameI18n?: string
  routePath?: string
  resourceCode?: string
  permissionCodes?: Array<string>
  routeMeta: Partial<RouteMeta & { redirectPath: string }>
  sortId?: string
  status?: string
  children?: Resource[]
  permissionList?: Resource[]
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

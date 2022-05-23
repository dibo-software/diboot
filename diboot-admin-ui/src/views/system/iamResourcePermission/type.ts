import { RouteMeta } from 'vue-router'

export interface Tree {
  id: number
  label: string
  children?: Tree[]
}

export interface ResourcePermission {
  parentId: number
  displayType: string
  displayName: string
  routePath: string
  redirectPath: string
  resourceCode: string
  permissionCode: Array<string>
  metaConfig: RouteMeta
  sort_id: number
}

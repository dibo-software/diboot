import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    module?: string
    componentPath?: string
    url?: string
    iframe?: boolean
    hidden?: boolean
    permissions?: Array<string>
    sort?: number
    affixTab?: boolean
    ignoreAuth?: boolean
    keepAlive?: boolean
    openNewWindow?: boolean
    hideBreadcrumb?: boolean
  }
}

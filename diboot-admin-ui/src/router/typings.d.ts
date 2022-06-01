import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    module?: string
    componentPath?: string
    url?: string
    iframe?: boolean
    permissions?: Array<string>
    sort?: number
    affixTab?: boolean
    ignoreAuth?: boolean
    keepAlive?: boolean
    hidden?: boolean
    hollow?: boolean
    borderless?: boolean
    openNewWindow?: boolean
    hideBreadcrumb?: boolean
  }
}

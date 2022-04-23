import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    module?: string
    component?: string
    sort?: number
    ignoreAuth?: boolean
    keepAlive?: boolean
    hidden?: boolean
    hollow?: boolean
    hideFooter?: boolean
    borderless?: boolean
    openNewWindow?: boolean
    hideBreadcrumb?: boolean
  }
}

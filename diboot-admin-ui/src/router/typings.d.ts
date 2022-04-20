import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    module?: string
    ignoreAuth?: boolean
    keepAlive?: boolean
    hidden?: boolean
    hollow?: boolean
    hideFooter?: boolean
    borderless?: boolean
    hideBreadcrumb?: boolean
  }
}

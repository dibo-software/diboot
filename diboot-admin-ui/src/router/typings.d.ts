import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    module?: string
    componentName?: string
    sort?: number
    affixTab?: boolean
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

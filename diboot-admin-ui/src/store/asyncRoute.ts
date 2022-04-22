import { Router, RouteRecordRaw, RouterView } from 'vue-router'
import Layout from '@/layout/index.vue'

export interface IRouteState {
  menus: RouteRecordRaw[]
}

export default defineStore({
  id: 'async-route',
  state: (): IRouteState => ({
    menus: []
  }),
  actions: {
    async generateRoutes(router: Router) {
      // 加载异步路由
      const res = await api.get<Array<any>>('/auth/menu')
      if (res.data?.length) {
        const asyncRoutes = filterAsyncRouter(res.data)
        asyncRoutes.forEach(e => router.addRoute(e))
        this.menus = filterAsyncMenu(asyncRoutes)
      }
    }
  }
})

// 加载所有页面
const pages = import.meta.globEager('@/views/**/*.vue')
// 获取组件
const resolveComponent = (name: any) => {
  const importPage = pages[`../views/${name}.vue`]

  if (!importPage) {
    throw new Error(`Unknown page ${name}. Is it located under Pages with a .vue extension?`)
  }

  return importPage.default
}

/**
 * <h2>过滤异步路由</h2>
 * 加载component及自动丢弃无用路由
 * @param asyncRoutes
 * @param parentPath
 */
function filterAsyncRouter(asyncRoutes: RouteRecordRaw[], parentPath?: string) {
  return asyncRoutes.filter(route => {
    const fullPath = buildFullPath(route.path, parentPath)
    if (route.children?.length && (route.children = filterAsyncRouter(route.children, fullPath)).length) {
      const component = route.meta?.component
      if (component?.toLowerCase() === 'layout' || component?.toLowerCase() === 'layout/index') {
        route.component = Layout
      } else if (!component) {
        route.component = RouterView
      } else {
        route.component = resolveComponent(component)
      }
      // 父级目录重定向首个子菜单
      route.redirect = buildFullPath(route.children[0].path, fullPath)
    } else {
      delete route.children
      if (route.meta?.component) {
        route.component = resolveComponent(route.meta?.component)
      } else return false
    }
    return true
  })
}

/**
 * 构建全路径
 *
 * @param path
 * @param parentPath
 */
const buildFullPath = (path: string, parentPath = '/') =>
  /^\//.exec(path) ? path : `${parentPath === '/' ? '' : parentPath}/${path}`

/**
 * 过滤菜单
 *
 * @param asyncRoutes
 * @param parent
 */
function filterAsyncMenu(asyncRoutes: RouteRecordRaw[], parent?: RouteRecordRaw) {
  const routes: RouteRecordRaw[] = []
  for (const route of asyncRoutes) {
    if (route.meta?.hidden) continue
    if (parent) route.path = buildFullPath(route.path, parent.path)
    if (!route.children) {
      routes.push(route)
    } else if (!route.meta?.title && route.children.length === 1) {
      const child = route.children[0]
      child.path = route.redirect as string
      routes.push(child)
    } else {
      route.children = filterAsyncMenu(route.children, route)
      route.children.length && routes.push(route)
    }
  }
  return routes
}

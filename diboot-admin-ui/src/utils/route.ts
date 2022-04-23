import { RouteRecord, RouteRecordName, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const RouterView = defineComponent({
  name: 'RouteView',
  render: () => h('router-view')
})

/**
 * 构建异步路由
 *
 * @param asyncRoutes
 */
export const buildAsyncRoutes = (asyncRoutes: RouteRecordRaw[]) => {
  // 加载所有页面
  const pages = import.meta.globEager('@/views/**/*.vue')
  // 获取组件
  const resolveComponent = (name: any) => {
    const importPage = pages[`../views/${name}.vue`]

    if (importPage) return importPage.default

    throw new Error(`Unknown page ${name}. Is it located under Pages with a .vue extension?`)
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
   * 构建路由
   *
   * @param routes
   * @param parentPath
   */
  function buildRouter(routes: RouteRecordRaw[], parentPath?: string) {
    return routes.filter(route => {
      const fullPath = buildFullPath(route.path, parentPath)
      if (route.children?.length && (route.children = buildRouter(route.children, fullPath)).length) {
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

  return buildRouter(asyncRoutes)
}

/**
 * 构建菜单Tree
 *
 * @param routes 路由列表
 */
export const buildMenuTree = (routes: RouteRecord[]) => {
  const routeTree: RouteRecord[] = []

  const findByNameAndCollect = (name: RouteRecordName | undefined, arr: RouteRecord[]) => {
    if (!name) return
    const index = routeTree.findIndex(e => e.name === name)
    const find = index > -1 ? routeTree.splice(index, 1)[0] : routes.find(e => e.name === name)
    find && arr.push(find)
  }

  for (const route of routes) {
    const children = route.children
    if (!route.meta.title && children.length === 1) {
      findByNameAndCollect(children[0].name, routeTree)
    } else if (children.length) {
      const newChildren: RouteRecord[] = []
      children.forEach(child => findByNameAndCollect(child.name, newChildren))
      route.children = newChildren
      routeTree.push(route)
    }
  }

  // 菜单排序
  const menuSort = (e1: RouteRecordRaw, e2: RouteRecordRaw) => (e1.meta?.sort ?? 0) - (e2.meta?.sort ?? 0)

  // 过滤菜单（隐藏菜单、无 title 时减少层级）
  function filterMenu(routeTree: RouteRecordRaw[]) {
    const routes: RouteRecordRaw[] = []
    for (const route of routeTree.sort(menuSort)) {
      if (route.meta?.hidden) continue
      if (!route.children || route.children.length === 0) {
        routes.push(route)
      } else if (!route.meta?.title && route.children.length === 1) {
        const child = route.children[0]
        child.path = route.redirect as string
        routes.push(child)
      } else {
        route.children = filterMenu(route.children)
        route.children.length && routes.push(route)
      }
    }
    return routes
  }

  return filterMenu(routeTree)
}

import { RouteRecord, RouteRecordName, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

const RouterView = defineComponent({
  name: 'RouteView',
  render: () => h('router-view')
})

/**
 * 视图组件
 */
export const viewComponents = Object.values(import.meta.globEager('@/views/**/*.{vue,tsx,jsx}'))
  .map(e => e.default)
  .filter(e => e.name)
  .reduce(
    (components, view) => {
      components[view.name] = view
      return components
    },
    { Layout }
  )

/**
 * 构建异步路由
 *
 * @param asyncRoutes
 */
export const buildAsyncRoutes = (asyncRoutes: RouteRecordRaw[]) => {
  // 获取组件
  const resolveComponent = (name: string) => {
    if (viewComponents[name]) return viewComponents[name]
    throw new Error(`Unknown component '${name}'. Is it located under 'views' with extension?`)
  }

  // 构建完整路径
  const buildFullPath = (path: string, parentPath = '/') =>
    /^\//.exec(path) ? path : `${parentPath === '/' ? '' : parentPath}/${path}`

  // 路由排序
  const routeSort = (e1: RouteRecordRaw, e2: RouteRecordRaw) => (e1.meta?.sort ?? 0) - (e2.meta?.sort ?? 0)

  /**
   * 构建路由
   *
   * @param routes
   * @param parentPath
   */
  function buildRouter(routes: RouteRecordRaw[], parentPath?: string) {
    return routes.filter(route => {
      const fullPath = buildFullPath(route.path, parentPath)
      if (route.children?.length && (route.children = buildRouter(route.children.sort(routeSort), fullPath)).length) {
        const componentName = route.meta?.componentName
        if (componentName) {
          route.component = resolveComponent(componentName)
        } else {
          route.component = RouterView
        }
        // 父级目录重定向首个子菜单
        route.redirect = buildFullPath(route.children[0].path, fullPath)
      } else {
        delete route.children
        if (route.meta?.componentName) {
          route.component = resolveComponent(route.meta?.componentName)
        } else return false
      }
      return true
    })
  }

  return buildRouter(asyncRoutes)
}

/**
 * 获取菜单Tree
 */
export const getMenuTree = () => {
  const routes = useRouter().getRoutes()
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
    } else {
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

import type { RouteRecord, RouteRecordName, RouteRecordRaw } from 'vue-router'
import type { DefineComponent, VNode } from 'vue'
import { h } from 'vue'
import useViewTabs from '@/store/view-tabs'
const Layout = () => import('@/layout/index.vue')
// 加载所有组件
const modules = import.meta.glob<Record<string, DefineComponent>>('@/views/**/*.{vue,tsx,jsx}')

// 异步组件映射
const views = Object.keys(modules).reduce(
  (pages: Record<string, () => Promise<Record<string, DefineComponent>>>, path) => {
    pages[path.replace(/\/src(.*)/, '@$1')] = modules[path]
    return pages
  },
  {}
)

/**
 * 构建视图名称路径映射
 */
export const buildViewMap = async () => {
  const map: Record<string, string> = {}
  await Promise.all(
    Object.keys(views).map(async path => {
      const view = (await views[path]()).default
      if (view.name) map[view.name] = path
    })
  )
  return map
}

// 动态渲染组件
const renderComponent = (name: string, callback: (cachedViews: string[]) => VNode) =>
  defineComponent({
    name,
    setup() {
      const viewTabs = useViewTabs()
      return () => callback(viewTabs.cachedViews)
    }
  })

// 路由排序
const routeSort = (e1: RouteRecordRaw, e2: RouteRecordRaw) => (e1.meta?.sort ?? 0) - (e2.meta?.sort ?? 0)

/**
 * 构建异步路由
 *
 * @param asyncRoutes
 */
export const buildAsyncRoutes = (asyncRoutes: RouteRecordRaw[]) => {
  // 构建完整路径
  const buildFullPath = (path: string, parentPath = '/') =>
    /^\//.exec(path) ? path : `${parentPath === '/' ? '' : parentPath}/${path}`

  /**
   * 构建路由
   *
   * @param routes
   * @param parentPath
   * @param level 层级
   */
  function buildRouter(routes: RouteRecordRaw[], parentPath?: string, level = 1) {
    return routes.sort(routeSort).filter(route => {
      // 一级路由转换绝对路由
      if (level == 1) route.path = buildFullPath(route.path)

      const fullPath = buildFullPath(route.path, parentPath)
      if (route.children?.length && (route.children = buildRouter(route.children, fullPath, level + 1)).length) {
        if (level == 1) {
          // 一级路由添加布局容器
          route.component = Layout
        } else {
          // 视图嵌套
          // route.component = renderComponent('ParentView', cachedViews =>
          //   h(RouterView, ({ Component }: { Component: DefineComponent }) =>
          //     h(KeepAlive, { include: cachedViews }, Component)
          //   )
          // )
        }
        // 父级目录重定向首个子菜单
        if (!route.redirect) route.redirect = buildFullPath(route.children[0].path, fullPath)
      } else {
        delete route.children
        if (route.meta?.componentPath) {
          const componentPath = route.meta?.componentPath
          const component = views[componentPath]
          if (component) route.component = component
          else {
            console.error(`Unknown page ${componentPath}. Is it located under 'src/views' ?`)
            return false // 未找到组件，不添加路由
          }
        } else if (route.meta?.url) {
          const url = route.meta?.url
          if (route.meta?.iframe) {
            /// iframe
            const urlInst = new URL(url)
            route.component = renderComponent((route.name ?? '').toString(), () =>
              h('iframe', {
                src: url,
                id: `iframe_${urlInst.origin.replace('://', '_').replace(':', '_').replace(/\./g, '_')}`,
                style: { border: 0, width: '100%', height: `calc(100% - 4px)` }
              })
            )
          } else {
            // 外部链接（打开新窗口；阻止路由）
            route.beforeEnter = () => {
              window.open(url)
              return false
            }
          }
        } else return false // 无法识别的路由阻止添加

        // 单层路由包装布局容器
        if (level == 1) {
          route.children = [_.cloneDeep(route)]
          route.component = Layout
          route.name = Symbol(route.name as string)
          route.meta = undefined
        }
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

  // 过滤菜单（隐藏菜单、无 title 时减少层级）
  function filterMenu(routeTree: RouteRecordRaw[]) {
    const routes: RouteRecordRaw[] = []
    for (const route of routeTree.sort(routeSort)) {
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

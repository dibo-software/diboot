import type { RouteLocationNormalized, Router } from 'vue-router'

export type IViewTabsStore = {
  tabList: RouteLocationNormalized[] // 页签
}

// 是否存在激活的 tab
const isPresenceActivation = (list: RouteLocationNormalized[], router: Router) => {
  const currentRouteName = router.currentRoute.value.name
  return list.some(item => item.name === currentRouteName)
}

// 跳转到指定 tab，或最后一个 tab 或 Home
const goTabOrLastOrHome = (list: RouteLocationNormalized[], router: Router, index = -1) => {
  const length = list.length
  if (index > -1 && index < length) router.push(list[index].fullPath).finally()
  else if (length === 0) router.push({ name: 'Home' }).finally()
  else router.push(list[length - 1].fullPath).finally()
}

// 查找 Tab 函数
const findTabFu = (route: RouteLocationNormalized) => (item: RouteLocationNormalized) => item.name == route.name

export default defineStore({
  id: 'view-tabs',
  state: (): IViewTabsStore => ({
    tabList: []
  }),
  getters: {
    // 缓存的视图组件名称列表
    cachedViews(): string[] {
      return [
        ...(this.tabList.filter(e => e.meta.keepAlive && e.name).map(e => e.name) as string[]),
        'ParentView' // 父级视图组件名，用于路由深度嵌套的视图的缓存
      ]
    }
  },
  actions: {
    // 初始化标签页
    initTabs(routes: RouteLocationNormalized[]) {
      this.tabList = routes
    },
    // 添加(存在则替换)
    addTab(route: RouteLocationNormalized) {
      const index = this.tabList.findIndex(findTabFu(route))
      if (index === -1) this.tabList.push(route)
      else this.tabList.splice(index, 1, route)
      if (!route.meta.title) route.meta.title = 'no-title'
      return index === -1 ? this.tabList.length - 1 : index
    },
    // 更新
    updateTabTitle(route: RouteLocationNormalized, title: string) {
      const find = this.tabList.find(findTabFu(route))
      if (find) find.meta.title = title
      else return false
    },
    // 关闭指定前页
    closeTab(route: RouteLocationNormalized, router: Router, autoTab = true) {
      const findIndex = this.tabList.findIndex(findTabFu(route))
      this.tabList.splice(findIndex, 1)
      if (router.currentRoute.value.name === route.name && autoTab) goTabOrLastOrHome(this.tabList, router, findIndex)
    },
    // 关闭左侧
    closeLeftTabs(route: RouteLocationNormalized, router: Router, autoTab = true) {
      const discard = this.tabList.splice(0, this.tabList.findIndex(findTabFu(route)))
      if (isPresenceActivation(discard, router) && autoTab) goTabOrLastOrHome(this.tabList, router)
    },
    // 关闭右侧
    closeRightTabs(route: RouteLocationNormalized, router: Router, autoTab = true) {
      const discard = this.tabList.splice(this.tabList.findIndex(findTabFu(route)) + 1)
      if (isPresenceActivation(discard, router) && autoTab) goTabOrLastOrHome(this.tabList, router)
    },
    // 关闭其他
    closeOtherTabs(route: RouteLocationNormalized, router: Router) {
      if (router.currentRoute.value.name === route.name) router.push(route.fullPath).finally()
      this.tabList = [route]
    },
    // 关闭全部
    closeAllTabs(router: Router, autoTab = true) {
      let isPresenceActivation = false
      const currentRouteName = router.currentRoute.value.name
      //保留固定路由
      this.tabList = this.tabList.filter(item => {
        if (!isPresenceActivation) isPresenceActivation = item.name === currentRouteName
        return item.meta.affixTab ?? false
      })
      if (isPresenceActivation && autoTab) goTabOrLastOrHome(this.tabList, router)
    }
  }
})

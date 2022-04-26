import { LocationQuery, RouteLocationNormalized, RouteParams, Router, RouteRecordName } from 'vue-router'

export type IViewTabsStore = {
  tabList: RouteLocationNormalized[] // 页签
}

interface IViewTab {
  fullPath: string

  path: string

  title: string

  name?: RouteRecordName

  query?: LocationQuery

  params?: RouteParams

  keepAlive?: boolean

  componentName?: string
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
      return this.tabList
        .filter(e => e.meta.keepAlive != false && e.meta.componentName)
        .map(e => e.meta.componentName) as string[]
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
    },
    // 更新
    updateTabTitle(route: RouteLocationNormalized, title: string) {
      const find = this.tabList.find(findTabFu(route))
      if (find) find.meta.title = title
      else return false
    },
    // 关闭指定前页
    closeTab(route: RouteLocationNormalized, router: Router) {
      const findIndex = this.tabList.findIndex(findTabFu(route))
      const find = this.tabList.splice(findIndex, 1)[0]
      if (router.currentRoute.value.name === find.name) {
        const length = this.tabList.length
        router.push(this.tabList[length > findIndex ? findIndex : length - 1].fullPath).finally()
      }
    },
    // 关闭左侧
    closeLeftTabs(route: RouteLocationNormalized) {
      this.tabList.splice(0, this.tabList.findIndex(findTabFu(route)))
    },
    // 关闭右侧
    closeRightTabs(route: RouteLocationNormalized) {
      this.tabList.splice(this.tabList.findIndex(findTabFu(route)) + 1)
    },
    // 关闭其他
    closeOtherTabs(route: RouteLocationNormalized) {
      this.tabList = this.tabList.filter(findTabFu(route))
    },
    // 关闭全部
    closeAllTabs(router: Router) {
      let presenceActivation = false
      const currentRouteName = router.currentRoute.value.name
      //保留固定路由
      this.tabList = this.tabList.filter(item => {
        if (!presenceActivation) presenceActivation = item.name === currentRouteName
        return item.meta.affixTab ?? false
      })
      // 跳转至最后一个 tab 或 Home
      const length = this.tabList.length
      if (presenceActivation)
        if (length >= 0) router.push(this.tabList[length - 1].fullPath).finally()
        else router.push({ name: 'Home' }).finally()
    }
  }
})

import { isNavigationFailure, Router } from 'vue-router'
import nProgress from 'nprogress'
import useAuthStore from '@/store/auth'
import useTabsViewStore from '@/store/tabsView'
import { buildAsyncRoutes } from '@/utils/route'
import auth from '@/utils/auth'

/**
 * 路由守卫函数
 * @param router - 路由实例
 */
export function createRouterGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    nProgress.start()
    // 未登录
    if (auth.getToken()) {
      const userStore = useAuthStore()
      // 已加载完基本数据
      if (userStore.info) {
        next()
        return
      }

      try {
        await userStore.getInfo()

        // 加载异步路由
        const res = await api.get<Array<any>>('/auth/menu')
        if (res.data?.length) {
          buildAsyncRoutes(res.data).forEach(e => router.addRoute(e))
        }

        const redirectPath = ((to.name === '404' && to.query.path) || from.query.redirect || to.path) as string
        const redirect = decodeURIComponent(redirectPath)
        const nextData = to.path === redirect ? { ...to, replace: true } : { path: redirect }
        next(nextData)
        return
      } catch (e) {
        // 获取数据异常
        console.log('动态加载授权路由失败', e)
      }
    }

    // You can access without permissions. You need to set the routing meta.ignoreAuth to true
    if (to.meta.ignoreAuth) {
      next()
      return
    }

    // redirect login page
    const redirectData = {
      name: 'Login',
      replace: true,
      query: to.query
    }
    redirectData.query.redirect = to.path
    next(redirectData)
  })

  router.afterEach((to, from, failure) => {
    if (isNavigationFailure(failure)) {
      nProgress.remove()
      return
    }
    nProgress.done()
    useTabsViewStore().addView(to)
  })

  router.onError(error => {
    console.log('路由错误', error)
  })
}

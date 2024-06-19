import type { Router, RouteRecordRaw } from 'vue-router'
import { isNavigationFailure } from 'vue-router'
import nProgress from 'nprogress'
import useAuthStore from '@/store/auth'
import { buildAsyncRoutes } from '@/utils/route'
import auth from '@/utils/auth'

/**
 * 路由守卫函数
 * @param router - 路由实例
 */
export function createRouterGuard(router: Router) {
  router.beforeEach(async (to, from) => {
    nProgress.start()

    if (auth.getToken()) {
      const userStore = useAuthStore()
      // 已加载完基本数据
      if (userStore.info) {
        return
      }

      try {
        await userStore.getInfo()
        // 加载异步路由
        const res = await api.get<Array<RouteRecordRaw>>('/auth/route')
        if (res.data?.length) {
          buildAsyncRoutes(res.data).forEach(e => router.addRoute(e))
        }

        const redirectPath = ((to.name === '404' && to.query.path) || from.query.redirect || to.path) as string
        const redirect = decodeURIComponent(redirectPath)
        return to.path === redirect ? to : { path: redirect, query: { ...to.query, path: undefined } }
      } catch (e) {
        // 获取数据异常
        console.log('动态加载授权路由失败', e)
      }
    }

    // You can access without permissions. You need to set the routing meta.ignoreAuth to true
    if (to.meta.ignoreAuth && to.name !== '404') {
      return
    }

    // redirect login page
    const redirectData = {
      name: 'Login',
      replace: true,
      query: to.query
    }
    if (to.name === '404') return redirectData
    redirectData.query.redirect = to.path
    return redirectData
  })

  router.afterEach((to, from, failure) => {
    if (isNavigationFailure(failure)) {
      nProgress.done()
      return
    }
    nProgress.done()
  })
}

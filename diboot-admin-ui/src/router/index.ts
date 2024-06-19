import { createRouter, createWebHashHistory, RouterView } from 'vue-router'
import type { RouteRecordRaw, RouteRecordName } from 'vue-router'
import { createRouterGuard } from '@/router/router-guards'
import auth from '@/utils/auth'
import i18n from '@/i18n'
/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect/:path(.*)*',
    name: 'Redirect',
    meta: { hidden: true, ignoreAuth: true },
    redirect: to => {
      const path = to.params.path
      return { path: `/${Array.isArray(path) ? path.join('/') : path}`, query: to.query, replace: true }
    }
  },
  {
    path: '/exception',
    name: 'Exception',
    redirect: '/exception/404',
    component: RouterView,
    meta: { title: 'Exception', hidden: true, ignoreAuth: true },
    children: [
      {
        path: '404',
        name: '404',
        component: () => import('@/views/exception/404.vue'),
        meta: { title: '404', ignoreAuth: true }
      },
      {
        path: '500',
        name: '500',
        component: () => import('@/views/exception/500.vue'),
        meta: { title: '500', ignoreAuth: true }
      }
    ]
  },
  {
    path: '/:path(.*)*',
    name: 'ErrorPage',
    meta: { hidden: true, ignoreAuth: true },
    redirect: to => {
      return auth.getToken()
        ? { name: '404', query: { path: to.path, ...to.query }, replace: true }
        : { name: 'Login', query: { redirect: to.path, ...to.query }, replace: true }
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true, ignoreAuth: true }
  },
  {
    path: '/',
    name: 'Home',
    redirect: '/dashboard',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: i18n.global.t('router.dashboard'), icon: 'Element:Odometer', affixTab: true, sort: -1 }
      }
    ]
  },
  {
    path: '/personal',
    name: 'Personal',
    redirect: '/personal',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: '/personal',
        name: 'Personal',
        component: () => import('@/views/personal/index.vue'),
        meta: { title: i18n.global.t('router.personal'), hidden: true }
      }
    ]
  }
]

/**
 * 创建路由
 */
const createAppRouter = () =>
  createRouter({
    history: createWebHashHistory(import.meta.env.BASE_URL), // hash 模式
    // history: createWebHistory(import.meta.env.BASE_URL), // HTML5 模式
    routes: constantRoutes
  })

const router = createAppRouter()

// 添加路由守卫等
createRouterGuard(router)

export default router

// 重置路由
export const resetRouter = () => {
  router
    .getRoutes()
    .map(e => e.name as RouteRecordName)
    .forEach(router.removeRoute)
  constantRoutes.forEach(router.addRoute)
}

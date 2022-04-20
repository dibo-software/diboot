import { createRouter, createWebHashHistory, RouteRecordRaw, RouterView } from 'vue-router'
import Layout from '@/layout/index.vue'

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect',
    name: 'Redirect',
    component: Layout,
    children: [
      {
        path: ':path(.*)*', // '/redirect/:path(.*)*'
        name: 'Redirect',
        redirect: to => {
          const path = to.params.path
          return { path: `/${Array.isArray(path) ? path.join('/') : path}`, query: to.query, replace: true }
        }
      }
    ]
  },
  {
    path: '/exception',
    name: 'Exception',
    component: RouterView,
    meta: { title: 'Exception' },
    children: [
      {
        path: '404',
        name: '404',
        component: () => import('@/views/exception/404.vue'),
        meta: { title: '404' }
      },
      {
        path: '500',
        name: '500',
        component: () => import('@/views/exception/500.vue'),
        meta: { title: '500' }
      }
    ]
  },
  {
    path: '/:path(.*)*',
    name: 'ErrorPage',
    redirect: to => {
      return { name: '404', query: { path: to.path }, replace: true }
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true }
  }
]

/**
 * 创建路由
 */
const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL), // hash 模式
  // history: createWebHistory(import.meta.env.BASE_URL), // HTML5 模式
  routes: constantRoutes
})

// home route
const homeRoute: RouteRecordRaw = {
  path: '/',
  name: 'Home',
  component: Layout,
  redirect: '/dashboard',
  meta: { title: '首页' },
  children: [
    {
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index.vue'),
      meta: { title: '仪表盘', ignoreAuth: true }
    }
  ]
}

router.addRoute(homeRoute)

export default router

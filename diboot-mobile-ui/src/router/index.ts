import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/redirect/:path(.*)*',
      name: 'Redirect',
      redirect: to => {
        const path = to.params.path
        return { path: `/${Array.isArray(path) ? path.join('/') : path}`, query: to.query, replace: true }
      }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '',
      component: Layout,
      children: [
        {
          path: '',
          name: 'CrudList',
          component: () => import('@/views/crud/list.vue'),
          meta: { title: 'CRUD-列表页', showTabbar: true }
        },
        {
          path: '/form/:id',
          name: 'CrudForm',
          component: () => import('@/views/crud/form.vue'),
          meta: { title: 'CRUD-表单页', showTabbar: true }
        },
        {
          path: '/detail/:id',
          name: 'CrudDetail',
          component: () => import('@/views/crud/detail.vue'),
          meta: { title: 'CRUD-详情页', showTabbar: true }
        }
      ]
    },
    {
      path: '/mine',
      component: Layout,
      children: [
        {
          path: '',
          name: 'Mine',
          component: () => import('@/views/mine/index.vue'),
          meta: { title: '我的', showTabbar: true }
        }
        // 我的 相关页面 (相对路由)
      ]
    }
  ]
})

export default router

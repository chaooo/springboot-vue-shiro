import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
// 静态路由
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard' }
    }]
  }
]

// 动态路由
export const asyncRoutes = [
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    name: 'User',
    meta: { title: '用户管理', icon: 'example' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list'),
        meta: { title: '用户列表', icon: 'nested' }
      },
      {
        path: 'edit',
        name: 'UserEdit',
        component: () => import('@/views/user/form'),
        meta: { title: '添加用户', icon: 'form' }
      }
    ]
  },

  {
    path: '/admin',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form1',
        component: () => import('@/views/test/index'),
        meta: { title: '管理员角色测试', icon: 'form', roles: ['admin'] }
      }
    ]
  },

  {
    path: '/editor',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form2',
        component: () => import('@/views/test/index'),
        meta: { title: '编辑角色测试', icon: 'form', roles: ['editor'] }
      }
    ]
  },

  {
    path: '/form',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form3',
        component: () => import('@/views/test/index'),
        meta: { title: '用户角色测试', icon: 'form', roles: ['user'] }
      }
    ]
  },

  {
    path: '/nested',
    component: Layout,
    redirect: '/nested/menu3',
    name: 'Nested',
    meta: { title: '子菜单权限测试', icon: 'form' },
    children: [
      {
        path: 'menu1',
        component: () => import('@/views/test/index'), // Parent router-view
        name: 'Menu1',
        meta: { title: '管理员可见', roles: ['admin'] }
      },
      {
        path: 'menu2',
        component: () => import('@/views/test/index'), // Parent router-view
        name: 'Menu1',
        meta: { title: '编辑者可见', roles: ['editor'] }
      },
      {
        path: 'menu3',
        component: () => import('@/views/test/index'), // Parent router-view
        name: 'Menu1',
        meta: { title: '普通用户可见', roles: ['user'] }
      }
    ]
  },

  /* {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://panjiachen.github.io/vue-element-admin-site/#/',
        meta: { title: 'External Link', icon: 'link' }
      }
    ]
  },*/

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

import router, { constantRoutes } from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login'] // 白名单

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)

  // determine whether the user has logged in
  const hasToken = getToken()

  if (hasToken) {
    // 如果已经登录
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const hasRole = store.getters.role
      if (hasRole) {
        next()
      } else {
        try {
          // 获取用户角色 ['admin'] 或,['developer','editor']
          const { roles } = await store.dispatch('user/getInfo')
          // 动态根据 角色 算出其对应有权限的路由
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)
          // 动态挂载路由
          router.addRoutes(accessRoutes)
          // addRouter是让挂载的路由生效，但是挂载后'router.options.routes'并未刷新(应该是个bug)
          // 所以还需要手动将路由加入'router.options.routes'
          router.options.routes = constantRoutes.concat(accessRoutes)

          next()
        } catch (error) {
          // remove token and go to login page to re-login
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    /* has no token*/
    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})

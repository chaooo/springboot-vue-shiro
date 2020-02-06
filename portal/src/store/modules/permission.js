import { asyncRoutes, constantRoutes } from '@/router'

/**
 * 判断用户是否拥有此路由的权限
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * 递归组装路由表，返回符合用户角色权限的路由列表
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []
  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        // 递归调用
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [], // 所有路由,包括静态路由和动态路由
  addRoutes: [] // 动态路由
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    // 合并路由
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  // 生成动态路由
  generateRoutes({ commit }, roles) {
    return new Promise(resolve => {
      let accessedRoutes
      if (roles.includes('admin')) {
        // '超级管理员'拥有所有的路由，这样判断节省加载时间
        accessedRoutes = asyncRoutes || []
      } else {
        // 筛选出该角色有权限的路由
        accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      }
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

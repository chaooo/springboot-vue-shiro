import store from '@/store'

/**
 * @param {String} value
 * @returns {Boolean}
 */
export default function checkPermission(value) {
  const permissions = store.getters.permissions
  return permissions.indexOf(value) > -1
}

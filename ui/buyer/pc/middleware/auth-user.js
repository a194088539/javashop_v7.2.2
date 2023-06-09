/**
 * 路由鉴权
 * @param store
 * @param redirect
 * @param route
 * @returns {*}
 */
export default function ({ store, redirect, route }) {
  // if (isStatic) return true
  if (!store.getters.user) {
    return redirect(`/login?forward=${route.fullPath}`)
  }
}

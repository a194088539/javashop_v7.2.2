// 在登录之后保存需要持久化的数据到localStorage。
// 因为生产静态页的时候，在没有登录的情况下，一些页面是不会被访问到的。
// 所以生产的静态页里不会有Store数据
// 这里之所以不用Storage，是因为Storage用cookie，cookie存不了导航和分类的数据，太大了。
// 而且这些数据也不用带到服务端去。
export default function (state, keys ) {
  if (process.client && localStorage) {
    const saveKeys = keys || ['categories', 'hotKeywords', 'env', 'navList', 'site', 'referer']
    const saveObject = {}
    Object.keys(state).forEach(key => {
      if (saveKeys.some(item => item === key)) {
        saveObject[key] = state[key]
      }
    })
    localStorage.setItem('save_state', JSON.stringify(saveObject))
  }
}

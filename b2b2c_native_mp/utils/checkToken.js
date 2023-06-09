const app = getApp()
const request = require('./request')

/**
 * 检查token：
 * 1. user/accessToken/refreshToken都不存在。
 *    表示用户没有登录，不能放行需要登录的API
 * 2. 不存在accessToken，但是user/refreshToken存在。
 *    表示accessToken过期，需要重新获取accessToken。
 *    如果重新获取accessToken返回token失效错误，说明已被登出。
 * @param options
 * @returns {Promise<any>}
 */
export default function checkToken(options) {
  // user
  const user = wx.getStorageSync('user')
  // 访问Token
  const accessToken = wx.getStorageSync('access_token')
  // 刷新Token
  const refreshToken = wx.getStorageSync('refresh_token')
  // 返回异步方法
  return new Promise((resolve, reject) => {
    /**
     * 如果accessToken、user、refreshToken都存在。
     * 说明必要条件都存在，可以直接通过，并且不需要后续操作。
     */
    let checkLock

    if (accessToken && user && refreshToken) {
      resolve()
      return
    }
    /**
     * 如果需要Token，但是refreshToken或者user没有。
     * 说明登录已失效、或者cookie有问题，需要重新调用自动登录方法。
     */
    if (options.needToken && (!refreshToken)) {
      resolve()
      return
    }
    /**
     * 如果不需要Token，并且没有。
     * 但是如果有refreshToken或user，说明只是Token过期。需要到下一步去获取新的Token
     * 但是有accessToken并且有，说明
     */
    if (!options.needToken && !accessToken && (!user || !refreshToken)) {
      resolve()
      return
    }
    /**
     * 不存在accessToken，但是user/refreshToken存在。
     * 说明用户已登录，只是accessToken过期，需要重新获取accessToken。
     * 如果没有needToken，说明不需要等待获取到新的accessToken后再请求。
     * 否则，需要等待
     */
    if (!accessToken && refreshToken) {
      /**
       * 如果没有刷新token锁，需要刷新token。
       * 如果有刷新token锁，则进入循环检测。
       */
      if (!app.__refreshTokenLock__) {
        // console.log(options.url + ' | 检测到accessToken失效，这个请求需要等待刷新token。')
        // 如果不需要Token，则不需要等拿到新的Token再请求。
        if (!options.needToken) resolve()
        // 开始请求新的Token，并加锁。
        app.__refreshTokenLock__ = request({
          url: `passport/token`,
          method: 'post',
          needToken: true,
          params: { refresh_token: refreshToken }
        }).then(response => {
          wx.setStorageSync('access_token', response.accessToken)
          wx.setStorageSync('refresh_token', response.refreshToken)
          app.__refreshTokenLock__ = null
          console.log(options.url + ' | 已拿到新的token。')
          options.needToken && resolve()
        }).catch(() => {
          app.__refreshTokenLock__ = undefined
          wx.removeStorageSync('cart'); // 清除购物车数据
          wx.removeStorageSync('user');
          wx.removeStorageSync('refresh_token');
          wx.removeStorageSync('refresh_token');
        })
      } else {
        if (!options.needToken) {
          console.log(options.url + ' | 不需要Token，直接通过...')
          resolve()
          return
        }
        console.log('进入循环检测...')
        // 循环检测刷新token锁，当刷新锁变为null时，说明新的token已经取回。
        checkLock = function () {
          setTimeout(() => {
            const __RTK__ = app.__refreshTokenLock__
            console.log(options.url + ' | 是否已拿到新的token：', __RTK__ === null)
            if (__RTK__ === undefined) {
              // 登录失效 调用自动登录
              if (getApp()) getApp().toAutoLogin()
              return
            }
            __RTK__ === null
              ? resolve()
              : checkLock()
          }, 500)
        }
        checkLock()
      }
      return
    }
    resolve()
  })
}

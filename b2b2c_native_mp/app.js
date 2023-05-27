import './lib/wxPromise.min.js'
import regeneratorRuntime from './lib/wxPromise.min.js'
import uuidv1 from './lib/uuid/uuid.modified'
import * as API_Connect from './api/connect'
import * as API_Member from './api/members'
import * as API_Trade from './api/trade'
import * as API_Common from './api/common'
// 小程序自动登录
App({
  onLaunch() {
    // 生成uuid
    if (!wx.getStorageSync('uuid')) {
      wx.setStorageSync('uuid', uuidv1.v1())
      this.globalData.uuid = wx.getStorageSync('uuid')
    }
    // 小程序自动登录
    if (wx.getStorageSync('uuid') && !wx.getStorageSync('refresh_token') && wx.getStorageSync('wxauth')) {
      this.toAutoLogin()
    }
    // 获取站点信息
    API_Common.getSiteData().then(response => {
      this.globalData.projectName = response.site_name
    })
  },
  async toAutoLogin() {
    // 检测是否登录 如果已经登录 或者登录结果为账户未发现 则不再进行自动登录
    if (wx.getStorageSync('refresh_token') || wx.getStorageSync('login_result') === 'account_not_found') return

    let final = {}

    const { code } = await wx.login({ timeout: 8000 })

    const uuid = wx.getStorageSync('uuid')
    try {
      let result = await API_Connect.loginByAuto({ code, uuid })
      // 如果已经进行过用户授权但是没有获取到unionID
      if (result.reson === 'unionid_not_found') {
        const { encryptedData, iv } = await wx.getUserInfo({ withCredentials: true, lang: 'zh_CN' })

        final = await API_Connect.accessUnionID({ code, uuid, encrypted_data: encryptedData, iv })
      }
      wx.setStorageSync('login_result', result.reson)

      const { access_token, refresh_token, uid } = result.access_token ? result : final
      // 如果登录成功 存储token(access_token refresh_token) uid 获取用户数据 获取购物车数据
      if (access_token && refresh_token && uid) {
        this.globalData.hasAuth = true
        wx.setStorageSync('access_token', access_token)
        wx.setStorageSync('refresh_token', refresh_token)
        wx.setStorageSync('uid', uid)
        // 获取用户信息 获取购物车信息
        const user = await API_Member.getUserInfo()
        wx.setStorageSync('user', user)
        // 获取购物车信息
        const { cart_list } = await API_Trade.getCarts()
        wx.setStorageSync('shoplist', cart_list)
        //刷新当前页面的数据
        if (getCurrentPages().length != 0) {
          getCurrentPages()[getCurrentPages().length - 1].onLoad() 
          getCurrentPages()[getCurrentPages().length - 1].onShow()
        }
      }
    } catch (e) {
      console.log("错误信息",e)
      wx.setStorageSync('login_result', false)
    }
  },
  globalData: {
    // uuid
    uuid: '',
    // 站点名称
    projectName: ''
  },
  setComputed(page) {
    let data = page.data
    let dataKeys = Object.keys(data)
    dataKeys.forEach(dataKey => {
      this.observeComputed(page, page.data, dataKey, page.data[dataKey])
    })
  },
  observeComputed(data, key, val, fn) {
    var dataVal = data[key];
    if (dataVal != null && typeof dataVal === 'object') {
      Object.keys(dataVal).forEach(childKey => { // 遍历val对象下的每一个key
        this.observeComputed(page, dataVal, childKey, dataVal[childKey])
      })
    }
    var that = this;
    Object.defineProperty(data, key, {
      configurable: true,
      enumerable: true,
      get: function () {
        return val
      },
      set: function (newVal) {
        val = newVal
        let computed = page.computed //computed对象，每个值都是函数 
        let keys = Object.keys(computed)
        let firstComputedObj = keys.reduce((prev, next) => {
          page.data.$target = function () {
            page.setData({ [next]: computed[next].call(page) })
          }
          prev[next] = computed[next].call(page)
          page.data.$target = null
          return prev
        }, {})
        page.setData(firstComputedObj)
      }
    })
  }
})
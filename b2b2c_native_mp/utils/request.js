import checkToken from './checkToken'
import { api,env } from '../config/config'
import { md5 } from '../lib/md5'
import { Foundation } from '../ui-utils/index.js'
import uuidv1 from '../lib/uuid/uuid.modified'
const app = getApp()
class Ajax {
  NODE_ENV = env
  header = {
    timeOffset: 8000,
    'content-type': 'application/x-www-form-urlencoded'
  }
  // 响应是否成功 1成功 0 失败
  RESPONSE_STATUS = 1
  constructor(header, NODE_ENV, RESPONSE_STATUS) {
    this.header = header
    this.NODE_ENV = NODE_ENV
    this.RESPONSE_STATUS = RESPONSE_STATUS
  }
  // 请求拦截器
  request(config) {
    /** 处理url */
    if (/^\/base\/.*/.test(config.url)) {
      let url = config.url.replace('/base/','/')
      config.url = `${api.base}${url}`
    } else if(/^\/zhibo\/.*/.test(config.url)){
      let url = config.url.replace('/zhibo/','/')
      config.url = `${api.zhibo}${url}`
    }else {
      config.url = `${api.buyer}${config.url}`
    }
    /** 配置全屏加载 */
    if (config.loading) {
      wx.showLoading({title: '加载中...',mask:true})
    }
    /** query参数传递 只适用于get*/
    if (config.params) {
      config.data = config.params
    }
    // uuid
    const uuid = wx.getStorageSync('uuid')
    config = {
      ...config,
      header: {
        uuid,
        timeOffset: 8000,
        'content-type': 'application/x-www-form-urlencoded'
      }
    }
    // json 提交
    if (config.isJson) {
      config = {
        ...config,
        header: {
          uuid,
          timeOffset: 8000,
          "content-type": 'application/json'
        }
      }
    }
    // 获取访问Token
    let accessToken = wx.getStorageSync('access_token')
    if (accessToken) {
      config = {
        ...config,
        header: {
          ...config.header,
          "Authorization": accessToken
        }
      }
    }
    return config
  }

  // 响应拦截器
  response (response) {
    if(this.RESPONSE_STATUS) { // 成功
      wx.hideLoading()
      return response.data
    } else { // 失败
      wx.hideLoading()
      const error_response = response.data || {}
      // 403 --> 没有登录|登录状态失效 重新自动登录
      if (error_response.code === 403 || error_response.code === '403') {
        if (!wx.getStorageSync('refresh_token')) return
        //清除缓存
        wx.clearStorageSync()
        wx.showModal({
          title: '提示',
          confirmColor: '#f42424',
          content: '登录已失效，请重新授权登录',
          success(res) {
            if (res.confirm) {
              wx.setStorageSync('uuid', uuidv1.v1())
              wx.checkSession({
                success: function (res) {
                  console.log("登录未过期", res)
                  wx.setStorageSync('wxauth', true)
                  getApp().toAutoLogin().then(()=>{
                    if (wx.getStorageSync('refresh_token')) {
                      wx.showToast({ title: '自动登录成功' })
                    } else {
                      wx.navigateTo({ url: '/pages/auth/login/login' })
                    }
                  })
                },
                fail: function (res) {
                  console.log("登录过期了", res)
                  wx.navigateTo({ url: '/pages/auth/login/login' })
                }
              })
            }else if(res.cancel){
              wx.switchTab({ url: '/pages/home/home' })
            }
          }
        })
        return response.data
      }
      if (error_response.message !== false) {
        let _message = response.code === 'ECONNABORTED' ? '连接超时，请稍候再试！' : '网络错误，请稍后再试！'
        // 错误提示
        wx.showToast({ title: error_response.message || _message, icon: 'none' })
      }
      return response.data
    }
  }

  ajax(options = {}) {
    return new Promise((resolve, reject) => {
      checkToken(options).then(() => {
        // 拦截 请求
        const config = this.request(options)
        // 处理url
        wx.request({
          url: config.url,
          data: config.data,
          header: config.header || this.header,
          method: config.method || 'get',
          success: res => { // 1 为成功 0 为失败
            if (res.statusCode === 200) {
              // 拦截 响应
              this.RESPONSE_STATUS = 1
              const _res = this.response(res)
              resolve(_res)
            } else {
              this.RESPONSE_STATUS = 0
              const _res = this.response(res)
              reject(_res)
            }
          },
          fail: res => {// 失败
            this.RESPONSE_STATUS = 0
            const _res = this.response(res)
            reject(_res)
          }
        })
      })
    })
  }

  get({
    url = 'localhost',
    data = {},
    success = null,
    fail = null,
    complete = null,
    callback = null,
    that = null,
  } = {}) {
    return this.ajax({
      url,
      data,
      method: 'GET',
      success,
      fail,
      complete,
      callback,
      that
    });
  }

  post({
    url = 'localhost',
    data = {},
    success = null,
    fail = null,
    that = null,
    complete = null,
    callback = null
  } = {}) {
    return this.ajax({
      url,
      data,
      method: 'POST',
      success,
      fail,
      that,
      complete,
      callback
    })
  }

  put({
    url = 'localhost',
    data = {},
    success = null,
    fail = null,
    that = null,
    complete = null,
    callback = null
  } = {}) {
    return this.ajax({
      url,
      data,
      method: 'POST',
      success,
      fail,
      that,
      complete,
      callback
    })
  }

}

export default new Ajax();
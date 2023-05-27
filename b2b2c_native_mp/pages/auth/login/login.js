import * as API_Trade from "../../../api/trade";
import regeneratorRuntime from '../../../lib/wxPromise.min.js'
import uuidv1 from '../../../lib/uuid/uuid.modified'
import { Foundation, RegExp } from '../../../ui-utils/index.js'
const app = getApp();
import * as API_Connect from '../../../api/connect'
import * as API_Common from '../../../api/common'
import * as API_Member from '../../../api/members'
import * as API_Passport from '../../../api/passport'
import * as API_Article from '../../../api/article'
let WxParse = require('../../../lib/wxParse/wxParse.js')
const beh = require('../../../utils/behavior.js')

Page({
  data: {
    uuid: '',
    captcha_url: '',
    // 默认不显示
    showWxAuth: true,
    login_type:'account',
    accountForm:{
      scene: 'LOGIN',
      username: '',
      password: '',
      captcha: '',
    },
    quickForm:{
      scene: 'LOGIN',
      mobile:'',
      captcha:'',
      sms_code:''
    },
    initTip: '发送验证码',
    endTip: '重新获取',
    sufTip: '秒后重新获取',
    count_time: 60,
    disabled: false,
    message: '',
    interval: null,
    projectName: '',
    policy: ''
  },
  behaviors: [beh],
  computed: {
    // 普通登录按钮 是否禁用
    login_disabled_account() {
      const { username, password, captcha } = this.data.accountForm
      if(this.data.validator_type === 'ALIYUN') {
        return !(username && password && this.data.accountAfsSuccess)
      } else {
        return !(username && password && captcha)
      }
    },
    // 短信登录按钮 是否禁用
    login_disabled_quick() {
      const { captcha, mobile, sms_code } = this.data.quickForm
      if(this.data.validator_type === 'ALIYUN') {
        return !(this.data.accountAfsSuccess && mobile && sms_code)
      } else {
        return !(captcha && mobile && sms_code)
      }
    },
  },
  async onLoad(options) {
    // 如果未授权 进行授权
    if (!wx.getStorageSync('wxauth')) {
      this.setData({ showWxAuth: false })
    }
    if (wx.getStorageSync('uuid')){
      this.setData({ uuid: wx.getStorageSync('uuid')})
    }else{
      wx.setStorageSync('uuid', uuidv1.v1())
      this.setData({ uuid: wx.getStorageSync('uuid') })
    }
    this.handleChangeCaptchalUrl()
    API_Article.getArticleDetail('115').then(response => {
      WxParse.wxParse('policy', 'html', response.content, this)
    })
    this.setData({ projectName: app.globalData.projectName })
  },
  handleTabs(e){
    let index = parseInt(e.currentTarget.dataset.index)
    if(index === 0){
      this.setData({ login_type: 'account'})
    }else{
      this.setData({ login_type: 'quick' })
    }
  },
  verifyResult() {
    this.setData({accountAfsSuccess: true})
  },
  //检测授权登录
  wxLoginAuth() {
    var that = this
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userInfo']) {
          wx.setStorageSync('wxauth', true)
          //自动登录
          app.toAutoLogin().then(()=>{
            if (wx.getStorageSync('refresh_token')) {
              wx.navigateBack()
            } else {
              that.setData({ showWxAuth:true})
            }
          })
        }
      }
    })
  },
  //图片验证码
  handleChangeCaptchalUrl() {
    this.setData({
      captcha_url: API_Common.getValidateCodeUrl(this.data.uuid, 'LOGIN'),
      'accountForm.captcha':'',
      'quickForm.captcha':''
    })
  },
  //发送短信验证码
  sendValidMobileSms(){
    return new Promise((resolve, reject) => {
      const params = JSON.parse(JSON.stringify(this.data.quickForm))
      const { mobile, captcha, scene } = this.data.quickForm
      if (!RegExp.mobile.test(mobile)) {
        wx.showToast({ title: '手机号码格式有误！', icon: 'none' })
        return false
      }
      if (!captcha) {
        wx.showToast({ title: '请输入图片验证码！', icon: 'none' })
        return false
      }
      API_Connect.sendMobileLoginSms(mobile, captcha, this.data.uuid, scene).then(() => {
        wx.showToast({ title: '验证码发送成功' })
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  startCountDown() {
    this.setData({ disabled: true })
    this.data.interval = setInterval(() => {
      if (this.data.count_time > 1) {
        this.data.count_time--
        this.data.message = this.data.count_time + this.data.sufTip
        this.setData({
          count_time: this.data.count_time,
          message: this.data.message
        })
      } else {
        clearInterval(this.data.interval)
        this.setData({
          message: '重新发送',
          count_time: 60,
          disabled: false
        })
      }
    }, 1000)
  },
  //登录
  accountLogin() {
    const login_type = this.data.login_type
    const form = login_type === 'quick' ? this.data.quickForm : this.data.accountForm
    if(login_type === 'quick'){
      if (!form.mobile || !RegExp.mobile.test(form.mobile) || !form.sms_code){
        wx.showToast({title: '表单填写有误，请检查！',icon:'none'})
        return false
      }
    } else {
      if (!form.username || !form.password || !form.captcha) {
        wx.showToast({ title: '表单填写有误，请检查！', icon: 'none' })
        return false
      }
    }
    const params = JSON.parse(JSON.stringify(form))
    params.uuid = this.data.uuid
    if (wx.getStorageSync('wxauth')){
      if (login_type === 'quick') {
        API_Connect.loginByMobileConnect(this.data.uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
      } else {
        API_Connect.loginBindConnectByAccount(this.data.uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
      }
      async function loginCallback(response) {
        if (response.result === 'bind_success') {
          const { access_token, refresh_token, uid } = response
          wx.setStorageSync('access_token', access_token)
          wx.setStorageSync('refresh_token', refresh_token)
          wx.setStorageSync('uid', uid)
          // 获取用户信息
          const user = await API_Member.getUserInfo()
          wx.setStorageSync('user', user)
          wx.navigateBack({ delta: 1 })
        } else {
          wx.showToast({
            title: '当前用户已绑定其他账号，请先解绑',
            icon: "none"
          })
        }
      }
    }else{
      if (login_type === 'quick') {
        const { mobile } = params
        API_Passport.loginByMobile(mobile, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
      } else {
        params.uuid = this.data.uuid
        API_Passport.login(params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
      }
      async function loginCallback(response) {
        const { access_token, refresh_token, uid } = response
        wx.setStorageSync('access_token', access_token)
        wx.setStorageSync('refresh_token', refresh_token)
        wx.setStorageSync('uid', uid)
        // 获取用户信息
        const user = await API_Member.getUserInfo()
        wx.setStorageSync('user', user)
        wx.navigateBack({ delta: 1 })
      }
    }
  },
  bindUsernameInput(e) { this.setData({ 'accountForm.username': e.detail.value }) },
  bindPasswordInput(e) { this.setData({ 'accountForm.password': e.detail.value }) },
  bindCodeInput(e) { this.setData({ 'accountForm.captcha': e.detail.value }) },
  syncMobile(e) { this.setData({ "quickForm.mobile": e.detail.value })},
  syncMobilImgCode(e) { this.setData({ "quickForm.captcha": e.detail.value })},
  syncMobilSmsCode(e) { this.setData({ "quickForm.sms_code": e.detail.value })},
  handleShowPolicy() { this.selectComponent('#bottomFrame').showFrame() }
})
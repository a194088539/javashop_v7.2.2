const App = getApp()
import * as API_Common from '../../../api/common'
import * as API_Passport from '../../../api/passport'
import * as API_Connect from '../../../api/connect.js'
import * as API_Article from '../../../api/article'
import * as API_Members from '../../../api/members.js'
import { Foundation, RegExp } from '../../../ui-utils/index.js'
import regeneratorRuntime from '../../../lib/wxPromise.min.js'
let WxParse = require('../../../lib/wxParse/wxParse.js')

Page({
  data: {
    step:1,
    uuid:'',
    registerForm:{
      scene: 'REGISTER',
      mobile:'',
      captcha:'',
      sms_code:'',
      password: '',
      rep_password: ''
    },
    valid_code_url:'',
    showAgreement:true,//是否显示注册协议
    agreement:'', // 注册协议
    agreed:false, // 同意注册协议
    initTip: '发送验证码',
    endTip: '重新获取',
    sufTip: '秒后重新获取',
    count_time: 60,
    disabled: false,
    message: '',
    interval: null
  },
  onLoad: function (options) {
    const uuid = wx.getStorageSync('uuid')
    this.setData({uuid : uuid})
    this.getValidImgUrl()
    /**
     * 获取用户协议
     */
    API_Article.getArticleByPosition('REGISTRATION_AGREEMENT').then(response=>{
      WxParse.wxParse('agreement', 'html', response.content, this)
    })
  },
  //同意协议
  handleAgreement(e){
    const agreed = JSON.parse(e.currentTarget.dataset.agreed)
    if (!agreed){
      wx.switchTab({url: '/pages/home/home'})
    }else{
      this.setData({
        showAgreement:false,
        agreed:agreed
      })
    }
  },
  getValidImgUrl(){
    this.setData({
      valid_code_url: API_Common.getValidateCodeUrl(this.data.uuid,'REGISTER')
    })
  },
  syncMobile(e){
    this.setData({"registerForm.mobile":e.detail.value})
  },
  syncMobilImgCode(e){
    this.setData({ "registerForm.captcha": e.detail.value })
  },
  syncMobilSmsCode(e){
    this.setData({ "registerForm.sms_code": e.detail.value })
  },
  syncPassword(e){
    this.setData({"registerForm.password":e.detail.value})
  },
  syncRepPassword(e){
    this.setData({"registerForm.rep_password":e.detail.value})
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
  //获取验证码
  sendValidMobileSms(){
    return new Promise((resolve, reject) => {
      const { mobile, captcha, scene } = this.data.registerForm
      if (!RegExp.mobile.test(mobile)) {
        wx.showToast({ title: '手机号码格式有误！', icon: 'none' })
        return false
      }
      if (!captcha) {
        wx.showToast({ title: '请输入图片验证码！', icon: 'none' })
        return false
      }
      API_Passport.sendRegisterSms(mobile, captcha,scene).then(() => {
        wx.showToast({ title: '验证码发送成功' })
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  //下一步
  handleNextStep(){
    const { mobile, sms_code} = this.data.registerForm
    API_Passport.validMobileSms(mobile, 'REGISTER', sms_code).then(()=>{
      this.setData({step:2})
    })
  },
  //立即注册
  handleConfirmRegister(){
    const { mobile,password,rep_password} = this.data.registerForm
    if(!password){
      wx.showToast({ title: '请输入密码！',icon:'none' })
      return false
    }
    if (!RegExp.password.test(password)){
      wx.showToast({ title: '密码应为6-20位英文或数字！',icon:'none' })
      return false
    }
    if(!rep_password){
      wx.showToast({ title: '请输入重复密码！',icon:'none' })
      return false
    }
    if(password !== rep_password){
      wx.showToast({ title: '两次密码不一样',icon:'none' })
      return false
    }
    // 获取用户信息
    wx.getUserInfo({
      lang:'zh_CN',
      success:(res)=>{
        const userInfo = res.userInfo
        const params = {
          nick_name: userInfo.nickName,
          face: userInfo.avatarUrl,
          sex: userInfo.gender === 1 ? 1 : 0,
          mobile: mobile,
          password: password
        }
        // 注册绑定
        API_Connect.registerBindConnect(this.data.uuid,params).then((res) => {
          const { access_token, refresh_token, uid } = res
          wx.setStorageSync('access_token', access_token)
          wx.setStorageSync('refresh_token', refresh_token)
          wx.setStorageSync('uid', uid)
          API_Members.getUserInfo().then((response)=>{
            wx.setStorageSync('user', response)
            wx.switchTab({ url: '/pages/ucenter/index/index' })
          })
        })
      }
    })  
  }
})
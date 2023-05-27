import * as API_Common from '../../../api/common'
import * as API_Passport from '../../../api/passport'
import { Foundation,RegExp } from '../../../ui-utils/index.js'

Page({
  data: {
    uuid: '',
    step:1,
    // 校验账户信息 表单
    validAccountForm: {
      scene: 'FIND_PASSWORD'
    },
    // 校验手机 表单
    validMobileForm: {
      scene: 'FIND_PASSWORD'
    },
    valid_img_url: '',//图片验证码URL
    // 修改密码 表单
    changePasswordForm: {
      password: '',
      rep_password: ''
    },
    initTip: '发送验证码',
    endTip: '重新获取',
    sufTip: '秒后重新获取',
    count_time: 60,
    disabled: false,
    message: '',
    interval: null
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    // 页面渲染完成
    const uuid = wx.getStorageSync('uuid')
    const user = wx.getStorageSync('user')
    this.setData({
      uuid: uuid,
      mobile: Foundation.secrecyMobile(user.mobile),
    })
    this.getValidImgUrl()
  },
  //获取图片验证码URL
  getValidImgUrl() {
    const uuid = this.data.step === 1 ? this.data.uuid : this.data.validMobileForm.uuid
    this.setData({
      valid_img_url: API_Common.getValidateCodeUrl(uuid, 'FIND_PASSWORD')
    })
  },
  synCaccount(e){
    this.setData({ 'validAccountForm.account':e.detail.value})
  },
  syncImgCode(e){
    this.setData({ 'validAccountForm.img_code': e.detail.value})
  },
  syncMobilImgCode(e){
    this.setData({ 'validMobileForm.img_code': e.detail.value })
  },
  syncMobilSmsCode(e){
    this.setData({ 'validMobileForm.sms_code': e.detail.value })
  },
  syncPassword(e){
    this.setData({'changePasswordForm.password':e.detail.value})
  },
  syncRePassword(e){
    this.setData({'changePasswordForm.rep_password':e.detail.value})
  },
  //验证账户信息
  handleValidAccount(){
    const params = JSON.parse(JSON.stringify(this.data.validAccountForm))
    params.uuid = this.data.uuid
    params.captcha = params.img_code
    API_Passport.validAccount(params).then((response)=>{
      this.setData({
        'validMobileForm.mobile' : response.mobile,
        'validMobileForm.uname' : response.uname,
        'validMobileForm.uuid' : response.uuid,
        step:2
      })
      this.getValidImgUrl()
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
  //发送验证码
  sendValidMobileSms(){
    return new Promise((resolve, reject) => {
      const params = JSON.parse(JSON.stringify(this.data.validMobileForm))
      params.captcha = params.img_code
      if (!params.img_code) {
        wx.showToast({ title: '请填写图片验证码！', icon: 'none'})
        return false
      }
      API_Passport.sendFindPasswordSms(params).then(() => {
        wx.showToast({ title: '验证码发送成功' })
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  //下一步
  handleNextStep(){
    const { uuid, sms_code } = this.data.validMobileForm
    API_Passport.validFindPasswordSms(uuid,sms_code).then(()=>{
      this.setData({step:3})
      this.getValidImgUrl()
    })
  },
  //确认修改
  submitChangeForm(){
    const { password, rep_password } = this.data.changePasswordForm
    const uuid = this.data.validMobileForm.uuid
    if (!password) { 
      wx.showToast({ title: '请输入新密码！', icon: 'none' }) 
      return false
    } 
    if (!RegExp.password.test(password)) {
      wx.showToast({ title: '密码格式不正确！', icon: 'none' })
      return false
    }
    if (!rep_password) {
      wx.showToast({ title: '请确认新密码！', icon: 'none' })
      return false
    }
    if (password !== rep_password) {
      wx.showToast({ title: '两次密码输入不一致！', icon: 'none' })
      return false
    }
    API_Passport.changePassword(uuid,password).then(()=>{
      wx.navigateBack({ delta: 1 })
      wx.showToast({ title: '密码找回成功！', duration: 2000  })
    })
  }
})
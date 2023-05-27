import * as API_Common from '../../../../api/common'
import * as API_Safe from '../../../../api/safe'
import { Foundation, RegExp } from '../../../../ui-utils/index.js'

Page({
  /**
   * 页面的初始数据
   */
  data: {
    uuid: '',
    mobile: '',
    step: 1,
    //效验手机号表单
    validMobileForm: {
      captcha: '',
      sms_code: '',
      scene: 'VALIDATE_MOBILE'
    },
    valid_img_url: '',//图片验证码URL
    //修改密码表单
    changePasswordForm: {
      password: '',
      rep_password: '',
      captcha: '',
      scene: 'MODIFY_PASSWORD',
    },
    initTip: '发送验证码',
    endTip: '重新获取',
    sufTip: '秒后重新获取',
    count_time: 60,
    disabled: false,
    message: '',
    interval: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const uuid = wx.getStorageSync('uuid')
    const user = wx.getStorageSync('user')
    this.setData({
      uuid: uuid,
      mobile: Foundation.secrecyMobile(user.mobile),
    })
    this.getValidImgUrl()
  },
  //手机校验表单 图片验证码变化时触发
  syncImgCode(e) {
    this.setData({'validMobileForm.captcha': e.detail.value})
  },
  //手机校验表单 短信验证码变化时触发
  syncSmsCode(e) {
    this.setData({'validMobileForm.sms_code': e.detail.value})
  },
  //新密码
  syncPassword(e){
    this.setData({'changePasswordForm.password': e.detail.value})
  },
  //确认密码
  syncResPassword(e){
    this.setData({'changePasswordForm.rep_password':e.detail.value})
  },
  //图片验证码
  syncImgCodeModify(e){
    this.setData({'changePasswordForm.captcha': e.detail.value})
  },
  //获取图片验证码URL
  getValidImgUrl() {
    this.setData({
      valid_img_url: API_Common.getValidateCodeUrl(this.data.uuid, this.data.step === 1 ? 'VALIDATE_MOBILE' : 'MODIFY_PASSWORD')
    })
  },
  // 短信验证码时间
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
  //效验 发送手机验证码
  sendValidMobileSms() {
    let that = this
    return new Promise((resolve, reject) => {
      const params = JSON.parse(JSON.stringify(that.data.validMobileForm))
      params.uuid = this.data.uuid
      delete params.sms_code
      if (!params.captcha) {
        wx.showToast({ title: '请填写图片验证码！', icon: 'none' })
        return false
      }
      API_Safe.sendMobileSms(params).then(() => {
        wx.showToast({ title: '验证码发送成功' })
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  //提交校验
  submitValMobileForm() {
    let that = this
    if (that.data.validMobileForm.captcha.length < 1 || that.data.validMobileForm.sms_code.length < 1) {
      wx.showToast({ title: '请输入图片验证码和短信验证码', icon: 'none' })
      return false;
    }
    const sms_code = this.data.validMobileForm.sms_code
    API_Safe.validChangePasswordSms(sms_code).then(() => {
      that.setData({step: 2})
      that.getValidImgUrl()
    })
  },
  //确认修改密码
  submitChangeForm(){
    const params = JSON.parse(JSON.stringify(this.data.changePasswordForm))
    params.uuid = this.data.uuid
    if(!params.password){
      wx.showToast({ title: '请填写新密码！', icon: 'none' })
      return false
    }
    if(!RegExp.password.test(params.password)){
      wx.showToast({ title: '密码格式不正确', icon: 'none' })
      return false
    }
    if (!params.rep_password) {
      wx.showToast({ title: '请再次填写新密码！', icon: 'none' })
      return false
    }
    if(params.password !== params.rep_password){
      wx.showToast({ title: '两次密码输入不一致', icon: 'none' })
      return false
    }
    if(!params.captcha){
      wx.showToast({ title: '请填写图片验证码！', icon: 'none' })
      return false
    }
    API_Safe.changePassword(params).then(()=>{
      wx.navigateBack()
      wx.showToast({ title: '密码修改成功!' })
    })
  }
})
import * as API_Common from '../../../../api/common'
import * as API_Deposit from '../../../../api/deposit'
import { RegExp } from '../../../../ui-utils/index.js'

Page({
  /**
   * 页面的初始数据
   */
  data: {
    step: 1,
    //效验手机号表单
    validMobileForm: {
      mobile: '',
      uuid: '',
      captcha: '',
      sms_code: '',
      scene: 'SET_PAY_PWD'
    },
    valid_img_url: '',//图片验证码URL
    //修改密码表单
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

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getMemberAccount()
  },
  // 获取用户信息
  getMemberAccount() {
    API_Deposit.getMemberAccount().then(response => {
      this.data.validMobileForm.mobile = response.mobile
      this.data.validMobileForm.uuid = response.uuid
      this.setData({ validMobileForm: this.data.validMobileForm })
      this.getValidImgUrl()
    })
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
  //获取图片验证码URL
  getValidImgUrl() {
    this.setData({
      valid_img_url: API_Common.getValidateCodeUrl(this.data.validMobileForm.uuid, 'SET_PAY_PWD')
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
      const params = JSON.parse(JSON.stringify(this.data.validMobileForm))
      delete params.mobile
      if (!params.captcha) {
        wx.showToast({ title: '请填写图片验证码！', icon: 'none' })
        return false
      }
      API_Deposit.sendMobileSms(params).then(() => {
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
    API_Deposit.validChangePasswordSms(sms_code).then(() => {
      that.setData({step: 2})
      that.getValidImgUrl()
    })
  },
  //确认修改密码
  submitChangeForm(){
    const { uuid, sms_code  } = this.data.validMobileForm
    const { password, rep_password } = this.data.changePasswordForm
    const params = {
      uuid,
      sms_code,
      password
    }
    if(!password){
      wx.showToast({ title: '请填写新密码！', icon: 'none' })
      return false
    }
    if(!RegExp.Integer.test(password) || password.length < 6){
      wx.showToast({ title: '密码应为6位数字！', icon: 'none' })
      return false
    }
    if (!rep_password) {
      wx.showToast({ title: '请再次填写新密码！', icon: 'none' })
      return false
    }
    if(password !== rep_password){
      wx.showToast({ title: '两次密码输入不一致', icon: 'none' })
      return false
    }
    API_Deposit.setPaymentPassword(params).then(()=>{
      wx.navigateBack()
      wx.showToast({ title: '密码修改成功!' })
    })
  }
})
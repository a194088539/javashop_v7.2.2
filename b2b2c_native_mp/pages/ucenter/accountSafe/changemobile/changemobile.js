import * as API_Common from '../../../../api/common'
import * as API_Safe from '../../../../api/safe'
import { Foundation, RegExp } from '../../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    uuid:'',
    mobile:'',
    new_phone:'',
    step:1,
    //效验手机号表单
    validMobileForm: {
      captcha: '',
      sms_code: '',
      scene: 'VALIDATE_MOBILE',
    },

    valid_img_url: '',//图片验证码URL
    //更换手机号表单
    changeMobileForm: {
      mobile: '',
      captcha: '',
      sms_code: '',
      scene: 'BIND_MOBILE'
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
      uuid:uuid,
      mobile: Foundation.secrecyMobile(user.mobile),
    })
    this.getValidImgUrl()
  },
  //手机校验表单 图片验证码变化时触发
  syncImgCode(e){
    this.setData({
      'validMobileForm.captcha':e.detail.value
    })
  },
  //手机校验表单 短信验证码变化时触发
  syncSmsCode(e){
    this.setData({
      'validMobileForm.sms_code': e.detail.value
    })
  },
  //更换手机表单 新手机号变化触发
  syncNewPhone(e) {
    this.setData({
      'changeMobileForm.mobile': e.detail.value
    })
  },
  //更换手机表单 图片验证码变化触发
  syncNewImgCode(e) {
    this.setData({
      'changeMobileForm.captcha': e.detail.value
    })
  },
  //更换手机表单 短信验证码变化触发
  syncNewSmsCode(e) {
    this.setData({
      'changeMobileForm.sms_code': e.detail.value
    })
  },
  //获取图片验证码URL
  getValidImgUrl() {
    this.setData({
      valid_img_url : API_Common.getValidateCodeUrl(this.data.uuid, this.data.step === 1 ? 'VALIDATE_MOBILE' : 'BIND_MOBILE')
    })
  },
  // 验证码倒计时
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
      params.uuid = that.data.uuid
      if (!params.captcha) {
        wx.showToast({title: '请填写图片验证码！',icon:'none'})
        return false
      }
      API_Safe.sendMobileSms(params).then(() => {
        wx.showToast({ title: '验证码发送成功'})
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  //提交校验更换手机号验证码
  submitValMobileForm(){
    let that = this
    if (that.data.validMobileForm.captcha.length < 1 || that.data.validMobileForm.sms_code.length < 1) {
      wx.showToast({ title: '请输入图片验证码和短信验证码', icon: 'none' })
      return false;
    }
    const sms_code  = this.data.validMobileForm.sms_code
    API_Safe.validChangeMobileSms(sms_code).then((res) => {
      clearInterval(this.data.interval)
      that.setData({
        step: 2, 
        count_time: 60,
        disabled: false,
        message: '',
        interval: null
      })
      that.getValidImgUrl()
    })
  },
  //修改手机号 发送验证码
  sendChangeMobileSms(){
    let that = this
    return new Promise((resolve, reject) => {
      const params = JSON.parse(JSON.stringify(that.data.changeMobileForm))
      params.uuid = that.data.uuid
      if (!RegExp.mobile.test(params.mobile)) {
        wx.showToast({ title: '手机号码格式有误！', icon: 'none' })
        return false
      }
      if (!params.captcha) {
        wx.showToast({ title: '请填写图片验证码！', icon: 'none' })
        return false
      }
      API_Safe.sendBindMobileSms(params.mobile, params).then(() => {
        wx.showToast({ title: '验证码发送成功' })
        this.startCountDown()
        resolve()
      }).catch(reject)
    })
  },
  //确认修改
  submitChangeForm(){
    let that = this
    const { mobile, sms_code } = this.data.changeMobileForm
    if (!RegExp.mobile.test(mobile)) {
      wx.showToast({ title: '手机号码格式有误！', icon: 'none' })
      return false
    }
    API_Safe.changeMobile(mobile, sms_code).then(() => {
      wx.showToast({ title: '更换成功！' })
      that.setData({
        new_phone: Foundation.secrecyMobile(that.data.changeMobileForm.mobile),
        step:3
      })
      let _mobile = wx.getStorageSync('user')
      _mobile.mobile = mobile
      wx.setStorageSync('user', _mobile)
    })
  }
})
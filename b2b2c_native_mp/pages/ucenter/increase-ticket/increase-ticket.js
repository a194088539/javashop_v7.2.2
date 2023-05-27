import * as API_Member from '../../../api/members.js'
import { Foundation, RegExp } from '../../../ui-utils/index.js'
import regeneratorRuntime from '../../../lib/wxPromise.min.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    notice: '1）注意有效增值税发票开票资质仅为一个。2)本页面信息仅供增值税专用发票 - 资质审核使用，切勿进行支付相关业务，谨防上当受骗。',
    marqueePace: 1,//滚动速度
    marqueeDistance: 10,//初始滚动距离
    size: 12,
    informationForm:{},//增票资质表单
    invoiceInfo:{},//增票资质申请相关信息
    agreed: false,//是否同意 增票资质确认书
    showCertificateBook: false,//是否显示 增票资质确认书
    canOpera: true //是否可以进行保存操作
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let length = this.data.notice.length * this.data.size; //文字长度
    let windowWidth = wx.getSystemInfoSync().windowWidth; // 屏幕宽度
    this.setData({ length, windowWidth });
    this.run1() // 水平一行字滚动完了再按照原来的方向滚动
    this.getInvoiceInfo()
  },
  run1: function () {
    let that = this;
    let interval  = setInterval(function () {
      if (-that.data.marqueeDistance < that.data.length) {
        that.setData({
          marqueeDistance: that.data.marqueeDistance - that.data.marqueePace,
        });
      } else {
        clearInterval(interval);
        that.setData({
          marqueeDistance: that.data.windowWidth
        });
        that.run1();
      }
    }, 30);
  },
  getInvoiceInfo(){
    API_Member.queryInvoiceInfo().then(response=>{
      const { company_name,taxpayer_code,register_address,register_tel,bank_name,bank_account } = response
      this.data.informationForm = {
        company_name,
        taxpayer_code,
        register_address,
        register_tel,
        bank_name,
        bank_account
      }
      if(response.status){
        if (response.status === 'NEW_APPLY') { this.setData({ canOpera: false })}
      }
      this.setData({
        agreed:false,
        invoiceInfo:response,
        informationForm:this.data.informationForm
      })
    })
  },
  //删除
  handleDeleteInvoice(){
    let that = this
    wx.showModal({
      title: '提示',
      content: '您选择的增值税发票资质将被删除，删除后将不可修复。是否确认？',
      confirmColor:'#f42424',
      success(res){
        if(res.confirm){
          API_Member.deleteInvoiceInfo(that.data.invoiceInfo.id).then(()=>{
            wx.showToast({title: '删除成功'})
            that.getInvoiceInfo()
          })
        }
      }
    })
  },
  syncCompanyName(e) { this.setData({'informationForm.company_name':e.detail.value}) },
  syncTaxpayerCode(e) { this.setData({ 'informationForm.taxpayer_code': e.detail.value }) },
  syncRegisterAddress(e) { this.setData({ 'informationForm.register_address': e.detail.value }) },
  syncRegisterTel(e) { this.setData({ 'informationForm.register_tel': e.detail.value }) },
  syncBankName(e) { this.setData({ 'informationForm.bank_name': e.detail.value }) },
  syncBankAccount(e) { this.setData({ 'informationForm.bank_account': e.detail.value }) },
  // 保存
  saveInvoiceInfo(){
    if(!this.data.informationForm.company_name){
      wx.showToast({title: '请输入单位名称',icon:'none'})
      return
    }
    if(!this.data.informationForm.taxpayer_code){
      wx.showToast({ title: '请填写纳税人识别号', icon: 'none' })
      return
    } else if (!RegExp.TINumber.test(this.data.informationForm.taxpayer_code)){
      wx.showToast({ title: '纳税人识别号不正确', icon: 'none' })
      return
    }
    if(!this.data.informationForm.register_address){
      wx.showToast({ title: '请输入注册地址', icon: 'none' })
      return
    }
    if(!this.data.informationForm.register_tel){
      wx.showToast({ title: '请输入注册电话', icon: 'none' })
      return
    } else if (!RegExp.TEL.test(this.data.informationForm.register_tel) && !RegExp.mobile.test(this.data.informationForm.register_tel)){
      wx.showToast({ title: '请输入正确的注册电话', icon: 'none' })
      return
    }
    if(!this.data.informationForm.bank_name){
      wx.showToast({ title: '请输入开户银行', icon: 'none' })
      return
    }
    if(!this.data.informationForm.bank_account){
      wx.showToast({ title: '请输入银行账号', icon: 'none' })
      return
    } else if (!RegExp.Integer.test(this.data.informationForm.bank_account)) {
      wx.showToast({ title: '银行账号必须为数字', icon: 'none' })
      return
    }
    if(!this.data.agreed){
      wx.showToast({ title: '请先同意协议', icon: 'none' })
      return
    }
    let params = { ...this.data.informationForm }
    if(!this.data.invoiceInfo){
      params = { ...params,status:'NEW_APPLY' }
    }else if(this.data.invoiceInfo.status){
      params = { ...this.data.invoiceInfo,...params }
    }

    Promise.all([this.data.invoiceInfo ? API_Member.changeTicketInformation(params) : API_Member.ticketInformationApply(params)]).then(()=>{
      this.getInvoiceInfo()
      wx.showToast({title: '提交成功，请耐心等待审核',icon:'none'})
    })
  },
  //同意
  handAgreed(){
    this.setData({ agreed: !this.data.agreed })
  },
  //显示增票资质确认书
  showCertificateBook() {
    this.selectComponent('#bottomFrame').showFrame();
  }
})
import { Foundation, RegExp } from '../../../../ui-utils/index.js'
import * as API_AfterSale from '../../../../api/after-sale'
import * as API_Order from '../../../../api/order'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    /** 订单编号 */
    order_sn: '',
    /** 是否允许申请售后 */
    applyService: false,
    /** 是否已收货 */
    rog: 'NO',
    /** 是否支持原路退款 */
    isRetrace: false,
    /** 预存款退款 */
    isRetraceBalance: false,
    /** 申请取消订单参数 */
    refund_info: {
      reason: '请选择取消原因',
      mobile: '',
      account_type: '',
      return_account: '',
      refund_price: 0.00
    },
    /** 是否展示取消原因下拉框 */
    reasonSelectShow: false,
    /** 取消原因下拉框数据 */
    reasonSelectActions: [
      { name: '商品无货' },
      { name: '配送时间问题' },
      { name: '不想要了' },
      { name: '商品信息填写错误' },
      { name: '地址信息填写错误' },
      { name: '商品降价' },
      { name: '货物破损已拒签' },
      { name: '订单无物流跟踪记录' },
      { name: '非本人签收' },
      { name: '其他' }
    ],
    /** 账户类型下拉框选中的值 */
    accountTypeText: '请选择账户类型',
    /** 是否展示账户类型下拉框 */
    accountTypeSelectShow: false,
    /** 账户类型下拉框数据 */
    accountTypeSelectActions: [
      { name: '支付宝', value: 'ALIPAY' },
      { name: '微信', value: 'WEIXINPAY' },
      { name: '银行卡', value: 'BANKTRANSFER' },
      { name: '预存款', value: 'BALANCE' }
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({ order_sn:options.order_sn})
    this.getOrderCancelDetail()
  },
  handleApplyServiceCheck(e){
    const index = e.currentTarget.dataset.index
    if(index == 0){
      this.setData({ rog: 'NO' })
    }else if(index == 1){
      this.setData({rog:'YES'})
    }
  },
  handleReturnAccount(e) { this.setData({ 'refund_info.return_account': e.detail.value }) },
  handleBankName(e) { this.setData({ 'refund_info.bank_name': e.detail.value }) },
  handleBankDepositAame(e) { this.setData({ 'refund_info.bank_deposit_name': e.detail.value }) },
  handleBankaccountName(e) { this.setData({ 'refund_info.bank_account_name': e.detail.value }) },
  handleBankAccountNumber(e) { this.setData({ 'refund_info.bank_account_number': e.detail.value }) },
  handleMobile(e) { this.setData({ 'refund_info.mobile': e.detail.value }) },
  handleShowReason() { this.setData({ reasonSelectShow:true})},
  handleReasonSelectHide() { this.setData({ reasonSelectShow:false})},
  handleReaseSelect(e) { this.setData({ 'refund_info.reason': e.currentTarget.dataset.name, reasonSelectShow: false }) },
  handleShowAccountType() { this.setData({ accountTypeSelectShow:true})},
  handleAccountTypeSelectHide() { this.setData({ accountTypeSelectShow:false})},
  handleAccountTypeSelect(e) { this.setData({ accountTypeText: e.currentTarget.dataset.name,'refund_info.account_type':e.currentTarget.dataset.value, accountTypeSelectShow:false})},
  //申请售后
  handleApplyService() {
    API_Order.confirmReceipt(this.data.order_sn).then(() => {
      wx.redirectTo({url: '/pages/ucenter/after-sale/after-sale'})
    })
  },
  //取消
  handleCancelSubmit(){wx.navigateBack() },
  //校验参数
  handleCheckParams() {
    // 取消原因校验
    if (!this.data.refund_info.reason || this.data.refund_info.reason === '请选择取消原因') {
      wx.showToast({ title: '请选择取消原因！',icon:'none'})
      return false
    }
    // 如果不支持原路退款
    if (!this.data.isRetrace && !this.data.isRetraceBalance) {
      // 账户类型校验
      if (!this.data.refund_info.account_type) {
        wx.showToast({title: '请选择账户类型！',icon:'none'})
        return false
      }

      // 如果账户类型为银行卡
      if (this.data.refund_info.account_type === 'BANKTRANSFER') {
        if (!this.data.refund_info.bank_name) {
          wx.showToast({title: '请输入银行名称！',icon:'none'})
          return false
        }
        // 银行开户行校验
        if (!this.data.refund_info.bank_deposit_name) {
          wx.showToast({title: '请输入银行开户行！',icon:'none'})
          return false
        }
        // 银行开户名校验
        if (!this.data.refund_info.bank_account_name) {
          wx.showToast({title: '请输入银行开户名！',icon:'none'})
          return false
        }
        // 银行账号校验
        if (!this.data.refund_info.bank_account_number) {
          wx.showToast({title: '请输入银行账号！',icon:'none'})
          return false
        }
      } else if (this.data.refund_info.account_type === 'ALIPAY' || this.data.refund_info.account_type === 'WEIXINPAY') {
        // 退款账号校验
        if (!this.data.refund_info.return_account) {
          wx.showToast({title: '请输入退款账号！',icon:'none'})
          return false
        }
      }
    }
    // 联系方式校验
    if (!this.data.refund_info.mobile || !RegExp.mobile.test(this.data.refund_info.mobile)) {
      wx.showToast({ title: '请输入正确格式的手机号码！', icon: 'none' })
      return false
    }
    return true
  },
  //提交
  handleSubmitApply(){
    if (!this.handleCheckParams()) {
      return false
    }
    this.setData({'refund_info.order_sn':this.data.order_sn})
    API_AfterSale.applyCancelOrder(this.data.refund_info).then(() => {
      this.handleCancelSubmit()
      wx.showToast({ title: '提交成功' })
    })
  },
  //获取订单详情信息
  getOrderCancelDetail() {
    API_Order.getOrderDetail(this.data.order_sn).then(response => {
      this.setData({
        isRetrace:response.is_retrace,
        isRetraceBalance: response.balance > 0,
        'refund_info.refund_price': Foundation.formatPrice(response.order_price),
        applyService:response.order_status === 'SHIPPED' && response.ship_status === 'SHIP_YES'
      })
    })
  }
})
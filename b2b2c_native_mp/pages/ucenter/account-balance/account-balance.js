import * as API_Deposit from '../../../api/deposit'
import { Foundation, RegExp } from '../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    account_balance: '', //账户余额
    recharge_amount: '', //充值金额
    activeTab: 0,
    params: {
      page_no: 1,
      page_size: 10
    },
    finished: false,
    scrollHeight: '',
    balanceLogList: [],
    rechargeDetailsList: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getDepositBalance()
    this.getDepositLogsList()
    this.getRechargeList()
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight - 267
    })
  },
  // 充值金额
  handleRechargeAmount(e) {
    this.setData({recharge_amount: e.detail.value})
  },
  // 立即支付
  handlePayment() {
    const value = this.data.recharge_amount
    if(!value) {
      wx.showToast({title: '充值金额不能为空且不可为0', icon: 'none'})
      return
    } else if (!RegExp.money.test(value)) {
      wx.showToast({title: '请输入正整数或者两位小数', icon: 'none'})
      return
    } else if (parseInt(value) < 1) {
      wx.showToast({title: '充值金额最少1元', icon: 'none'})
      return
    }
    API_Deposit.getRecharge(value).then(response => {
      const that = this
      const sn = response.recharge_sn
      const client_type = 'MINI'
      const pay_mode = 'normal'
      const payment_plugin_id = 'weixinPayPlugin'
      API_Deposit.getRechargeInitiatePay(sn,{client_type,pay_mode,payment_plugin_id}).then(res => {
        wx.requestPayment(res).then(res => {
          wx.showModal({
            title: '支付提示',
            content: '请确认支付是否完成',
            confirmText: '支付成功',
            confirmColor:'#f42424',
            cancelText: '重新支付',
            success(res) {
              if (res.confirm) {
                that.setData({recharge_amount: ''})
                that.getDepositBalance()
                that.getDepositLogsList()
                that.getRechargeList()
              } else if (res.cancel) { // 用户点击取消
              }
            }
          })
        })
      })
    })
  },
  handleSwitchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index)
    if (index !== this.data.showType) {
      this.setData({
        activeTab: index
      })
    }
  },
  // 获取账户余额
  getDepositBalance() {
    API_Deposit.getDepositBalance().then(response => {
      response = Foundation.formatPrice(response)
      this.setData({account_balance: response})
    })
  },
  typeFilter(val) {
    switch (val) {
      case 'PAY_YES': return '已支付'
      case 'PAY_NO': return '未支付'
    }
  },
  // 消费明细
  getDepositLogsList() {
    API_Deposit.getDepositLogsList(this.data.params).then(response => {
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.money = Foundation.formatPrice(key.money)
          key.time = Foundation.unixToDate(key.time)
        })
        this.data.balanceLogList.push(...data)
        this.setData({ balanceLogList: this.data.balanceLogList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  // 充值记录
  getRechargeList() {
    API_Deposit.getRechargeList(this.data.params).then(response => {
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.recharge_money = Foundation.formatPrice(key.recharge_money)
          key.recharge_time = Foundation.unixToDate(key.recharge_time)
          key.pay_status = this.typeFilter(key.pay_status)
        })
        this.data.rechargeDetailsList.push(...data)
        this.setData({ rechargeDetailsList: this.data.rechargeDetailsList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  loadBalanceLogListMore() {
    this.setData({ "params.page_no": this.data.params.page_no + 1 })
    if (this.data.activeTab === 0) {
      if (!this.data.finished){
        this.getDepositLogsList()
      }
    } else {
      if (!this.data.finished) {
        this.getRechargeList()
      }
    }
  },
  
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})
const app = getApp()
const middleware = require('../../utils/middleware.js')
import regeneratorRuntime from '../../lib/wxPromise.min.js'
import * as API_Trade from '../../api/trade'
import * as API_Deposit from '../../api/deposit'
import { Foundation } from '../../ui-utils/index'

Page(middleware.identityFilter({
    data: {
      trade_sn: '',
      order_sn: '',
      // 支付方式
      payment: '',
      // 订单详情
      order: '',
      //是否使用预存款支付
      isCheckPreDeposit: false,
      //支付密码
      password: '',
      payFocus: false
    },
    onLoad(options) {
      this.setData({
        order_sn: options.order_sn || '',
        trade_sn: options.trade_sn || ''
      })
    },
    async onShow() {
      const responses = await Promise.all([
        this.data.trade_sn
          ? API_Trade.getCashierData({ trade_sn: this.data.trade_sn })
          : API_Trade.getCashierData({ order_sn: this.data.order_sn }),
        API_Trade.getPaymentList('MINI')
      ])
      responses[0].deposite.balance = Foundation.formatPrice(responses[0].deposite.balance)
      this.setData({
        order: {
          ...responses[0],
          need_pay_price: Foundation.formatPrice(responses[0].need_pay_price)
        },
        payment: responses[1][0]
      })
    },
    // 预存款支付
    handlePreDeposit() {
      this.setData({isCheckPreDeposit:!this.data.isCheckPreDeposit,password: ''})
    },
    getFocus() {this.setData({payFocus:true})},
    inputPwd(e) {
      this.setData({ password: e.detail.value })
      if (this.data.password.length >= 6){
        const trade_type = this.data.trade_sn ? 'TRADE' : 'ORDER'
        const sn = this.data.trade_sn || this.data.order_sn
        const password = this.data.password
        const params = { sn, trade_type, password }
        wx.showLoading({title: '加载中'})
        API_Deposit.getBalancePay(trade_type, sn, params).then(response => {
          wx.hideLoading()
          if(response.need_pay === 0) {
            wx.redirectTo({url: '/pages/payment-complete/payment-complete'})
          } else {
            this.setData({
              isCheckPreDeposit: false,
              password: '',
              payFocus: false
            })
            this.onShow()
            wx.showToast({title: '还需在线支付' + response.need_pay + '元' ,icon: 'none'})
          }
        }).catch(error => {
          this.setData({password: ''})
        })
      }
    },
    /** 发起支付 */
    initiatePay() {
      const that = this
      that.setData({isCheckPreDeposit:false})
      const trade_type = that.data.trade_sn ? 'TRADE' : 'ORDER'
      const sn = that.data.trade_sn || that.data.order_sn
      const client_type = 'MINI'
      const pay_mode = 'normal'
      const payment_plugin_id = that.data.payment.plugin_id
      API_Trade.initiatePay(trade_type, sn, {
        client_type,
        pay_mode,
        payment_plugin_id
      }).then(response => {
        wx.pro.requestPayment(response).then(res => {
          wx.showModal({
            title: '支付提示',
            content: '请确认支付是否完成',
            confirmText: '支付成功',
            confirmColor:'#f42424',
            cancelText: '重新支付',
            success(res) {
              if (res.confirm) {
                wx.redirectTo({url: '/pages/payment-complete/payment-complete'})
              } else if (res.cancel) { // 用户点击取消
              }
            }
          })
        })
      })
    },
    // 查看订单
    handleLookOrder() { 
      let pages = getCurrentPages();
      //获取上级页面url地址
      let pageRouter = pages[pages.length - 2].route
      if (pageRouter === 'pages/ucenter/order/order'){
        wx.navigateBack()
      }else{
        wx.redirectTo({url: '/pages/ucenter/order/order'})
      }
    },
    // 回到首页
    handleToHome() {wx.switchTab({url: '/pages/home/home'})},
    handleRecharge() {wx.navigateTo({url: '/pages/ucenter/account-balance/account-balance'})},
    handlePassword() {wx.navigateTo({url: '/pages/ucenter/accountSafe/accountSafe'})}
  })
)
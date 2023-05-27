const app = getApp()
import * as API_Members from '../../../api/members.js'

Page({
    data: {
      defaultFace:'../../../static/images/icon-noface.jpg',
      userInfo: {},
      // 默认不显示
      showWxAuth: false,
      scrollHeight: '',
      scrollTop: 0,//滚动高度
      showGoTop: false//显示返回顶部按钮
    },
    onLoad(){
      this.setData({scrollHeight: wx.getSystemInfoSync().windowHeight})
    },
    onShow() {
      const userInfo = wx.getStorageSync('user')
      if(userInfo.face === 'null'){userInfo.face = null}
      this.setData({ userInfo: userInfo})
      if (wx.getStorageSync('access_token') && wx.getStorageSync('user')) {
        this.setData({ showWxAuth: true })
      } else {
        this.setData({ showWxAuth: false })
      }
    },
    isLogin() {
      if (!wx.getStorageSync('refresh_token')) {
        wx.navigateTo({ url: '/pages/auth/login/login' })
      } else {
        return true
      }
    },
    goUserInfo() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/my-profile/my-profile" })
    },
    // 我的资料
    goProfile(){
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/my-profile/my-profile" })
    },
    // 全部订单
    goOrder() {
      if(!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/order/order" })
    },
    payment(){
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/order/order?order_status=WAIT_PAY" })
    },
    shipped() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/order/order?order_status=WAIT_SHIP" })
    },
    received() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/order/order?order_status=WAIT_ROG" })
    },
    commented(){
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/order/order?order_status=WAIT_COMMENT" })
    },
    // 账户余额
    goAccountBalance() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/account-balance/account-balance" })
    },
    // 我的优惠券
    goCoupon() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/my-coupon/my-coupon" })
    },
    // 我的收藏
    goCollect() {
      if (!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/my-collect/my-collect" })
    },
    // 我的积分
    goPoints(){
      if (!this.isLogin()) return
      wx.navigateTo({url: '/pages/ucenter/my-points/my-points'})
    },
    // 站内消息
    goMessage(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/site-message/site-message' })
    },
    //分销管理
    goDistribution(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/distribution/distribution' })  
    },
    //账户安全
    goAccountSafe(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/accountSafe/accountSafe"})
    },
    // 地址管理
    goAddress() {
      if(!this.isLogin()) return
      wx.navigateTo({ url: "/pages/ucenter/address/address" })
    },
    //售后
    goAftersale(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/after-sale/after-sale'})
    },
    // 我的足迹
    goFootprint(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/my-footprint/my-footprint'})
    },
    //资询管理
    goAsk(){
      if (!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/my-ask/my-ask' })
    },
    //我的评论
    goComments(){
      if(!this.isLogin()) return
      wx.navigateTo({url: '/pages/ucenter/my-comments/my-comments'})
    },
    //我的发票
    goReceipt(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/my-receipt/my-receipt'})
    },
    //交易投诉
    goComplaint(){
      if(!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/complaints/complaints'})
    },
    //增票资质
    goIncreaseTicket(){
      if (!this.isLogin()) return
      wx.navigateTo({ url: '/pages/ucenter/increase-ticket/increase-ticket'})
    }, 
    // 退出登录
    exitLogin() {
      API_Members.getUserInfo().then(response =>{
        if(response){
          wx.showModal({
            title: '提示',
            confirmColor: '#f42424',
            content: '确定要退出登录吗？这将会解绑您的微信！',
            success(res) {
              if (res.confirm) {
                API_Members.logout().then(() => {
                  wx.removeStorageSync('access_token')
                  wx.removeStorageSync('refresh_token')
                  wx.removeStorageSync('uid')
                  wx.removeStorageSync('user')
                  wx.removeStorageSync('login_result')
                  wx.switchTab({ url: '/pages/home/home' })
                }).catch()
              }
            }
          })
        }
      })
    },
    scroll: function (e) {
      let that = this
      if (e.detail.scrollTop > 200) {
        that.setData({ showGoTop: true })
      } else {
        that.setData({ showGoTop: false })
      }
    },
    //返回顶部
    goTop: function () { this.setData({ scrollTop: 0 }) }
  })

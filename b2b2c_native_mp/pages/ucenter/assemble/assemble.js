import * as API_Order from '../../../api/order.js'
import * as API_Trade from '../../../api/trade.js'
// import * as API_Share from '../../../api/share.js'
import { Foundation } from '../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    order_sn:'',
    assemble: {},//拼团详情
    assemblePeople: [],//拼团人
    maybelikegoods: [],//您可能喜欢
    timer: null,//倒计时计时器
    day:0,
    hours:0,
    minutes:0,
    seconds:0,
    showWxAssShare: false,//是否显示分享遮罩
    isFinshed: false,//是否已完成拼团
    isShared:false,//是否是被分享的
    signatureUrl: '',//分享授权链接
    shareParams:{},//分享参数
    showSpecsPopup: false,//拼团规格弹框
    pintuan_id:'', //拼团id
    pintuan_bg: '../../../static/images/background-pt-share_01.png',
    isPay: false //是否支付
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      order_sn: options.order_sn || '',
      isShared:!!(options.from_nav && options.from_nav === 'assemble'),
      isPay: options.pay_status === 'PAY_YES'
    })
    this.getAssembleOrder()
  },
  async getAssembleOrder(){
    const responses = await Promise.all([API_Order.getAssembleOrderDetail(this.data.order_sn), API_Order.getAssembleGuest(this.data.order_sn)])
    responses[0].savePrice = Foundation.formatPrice(responses[0].origin_price - responses[0].sales_price)
    responses[0].origin_price = Foundation.formatPrice(responses[0].origin_price)
    if(responses[1]){responses[1].forEach(key => key.origin_price = Foundation.formatPrice(key.origin_price))}
    this.setData({
      assemble:responses[0],
      maybelikegoods:responses[1],
      pintuan_id: responses[0].pintuan_id,
      pintuan_bg: responses[0].order_status === 'formed' ? '../../../static/images/background-pt-share_03.png' : (this.data.isPay ? '../../../static/images/background-pt-share_02.png' : this.data.pintuan_bg)
    })
    this.data.assemble.left_time && this.contShareDown(this.data.assemble.left_time)
    if(this.data.assemble.required_num - this.data.assemble.offered_num <= 0){
      this.setData({isFinshed: true})
    } else {
      this.setData({isFinshed: false})
    }
  },
  /** 开启倒计时 */
  contShareDown(times) {
    let end_ime = times
    this.data.timer = setInterval(() => {
      if (end_ime <= 0) {
        clearInterval(this.data.timer)
      } else {
        const time = Foundation.countTimeDown(end_ime)
        this.setData({
          day:parseInt(time.day),
          hours: time.hours,
          minutes:time.minutes,
          seconds:time.seconds
        })
        end_ime--
      }
    }, 1000)
  },
  /**  所选规格发生变化 sku变化 */
  onSkuChanged(sku) {
    if (this.data.sku_id && this.data.from_nav === 'assemble') {
      this.setData({ is_assemble: true })
      /** 查询获取某个拼团的sku详情 */
      const { sku_id } = sku.detail
      API_Promotions.getAssembleDetail(sku_id).then(res => {
        this.setData({ assemble: res })
      })
    }
    sku.detail.price = Foundation.formatPrice(sku.detail.price)
    this.setData({ selectedSku: sku.detail })
  },
  /** 所选规格数量发生变化 */
  onNumChanged(num) {
    this.setData({ buyNum: num.detail })
  },
  /** 参与分享来的拼团 或者一键发起拼团 */
  toAssembleBuyNow() {
    if (!this.isLogin()) return
    const { buyNum } = this.data
    const { sku_id } = this.data.selectedSku
    API_Trade.addToAssembleCart(sku_id, buyNum).then((response) => {
      const link = !this.data.isFinshed ? `/pages/shopping/checkout/checkout?order_id=${this.data.assemble.order_id}&from_nav=assemble` : `/pages/shopping/checkout/checkout?from_nav=assemble`
      wx.navigateTo({ url: link })
    })
  },
  // 登录校验
  isLogin() {
    if (!this.data.selectedSku) {
      wx.showToast({
        title: '请选择商品规格！',
        icon: 'none',
        duration: 1500
      })
      this.setData({ showSpecsPopup: true })
      return false
    }
    if (!wx.getStorageSync('refresh_token')) { // 如果没有登录
      wx.showModal({
        title: '提示',
        content: '您还未登录，要现在去登录吗？',
        confirmColor:'#f42424',
        success(res) {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/auth/login/login' })
          }
        }
      })
      return false
    } else {
      return true
    }
  },
  /** 我也要参团  一键发起拼团 */
  handleAssemble(){
    this.setData({showSpecsPopup:true})
  },
  /** 邀请好友 */
  handleInviteFriends(){
    this.setData({ showWxAssShare:true})
  },
  /** 去支付 */
  handlePay(e) {
    const order_sn = e.currentTarget.dataset.order_sn
    wx.redirectTo({url: '/pages/cashier/cashier?order_sn=' + order_sn})
  },
  /** 去逛逛 */
  goShopping() { wx.reLaunch({url: '/pages/home/home'})},
  /** 分享 */
  onShareAppMessage: function () {
    var that = this;
    return {
      title: that.data.assemble.goods_name,
      path: '/pages/ucenter/assemble/assemble?order_sn=' + that.data.order_sn + '&goods_id=' + that.data.assemble.goods_id + '&sku_id=' + that.data.assemble.sku_id + '&from_nav=assemble',
      imageUrl: that.data.assemble.thumbnail
    }
  }
})
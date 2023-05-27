const app = getApp()
import regeneratorRuntime from '../../lib/wxPromise.min.js'
import * as API_promotions from '../../api/promotions'
import * as API_Members from '../../api/members'

Page({
  data: {
    finished: false,
    params: {
      page_no: 1,
      page_size: 10
    },
    couponsList: [],
    // 页面高度
    height: ''
  },
  onLoad() {
    this.setData({ height: wx.getSystemInfoSync().windowHeight})
    this.GET_Coupons()
  },
  /** 加载数据 */
  loadData() {
    this.setData({ "params.page_no": this.data.params.page_no += 1})
    if (!this.data.finished) { this.GET_Coupons()}
  },
  /** 领取优惠券 */
  receiveCoupon(e) {
    const coupon = e.currentTarget.dataset.coupon
    if(!wx.getStorageSync('refresh_token')) {
      wx.showModal({
        title: '提示',
        content: '您还未登录，要现在去登录吗？',
        confirmColor:'#f42424',
        success(res) {
          if(res.confirm) {
            wx.navigateTo({ url: '/pages/auth/login/login' })
            return false
          }
        }
      })
    } else {
      API_Members.receiveCoupons(coupon.coupon_id).then(() => {
        wx.showToast({ title: '领取成功!'})
      })
    }
  },
  /** 获取优惠券列表 */
  GET_Coupons() {
    API_promotions.getAllCoupons(this.data.params).then(response => {
      const { data } = response
      if (data && data.length) {
        this.data.couponsList.push(...data)
        this.setData({ couponsList: this.data.couponsList })
      } else {
        this.setData({ finished: true })
      }
    })
  }
})
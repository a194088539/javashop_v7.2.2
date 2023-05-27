/**
 * 结算页
 * 优惠券组件
 */
import * as API_Trade from '../../../../../api/trade'
import regeneratorRuntime from '../../../../../lib/wxPromise.min.js'
Component({
  properties: {
    inventories: Array,
    couponList: Array,
    show: Boolean,
    way: String
  },
  observers: {
    show(newVal) {
      if (newVal) { this.selectComponent('#bottomFrame').showFrame() }
    }
  },
  methods: {
    /** 关闭弹窗 */
    handleCouponsClose() {
      this.selectComponent('#bottomFrame').hideFrame();
    },
    handleShop(e) {
      wx.navigateTo({
        url: '/pages/shop/shop_id/shop_id?id=' + e.currentTarget.dataset.shopid,
      })
      this.selectComponent('#bottomFrame').hideFrame();
    },
    /** 使用优惠券 */
    async useCoupon(e) {
      const coupon = e.currentTarget.dataset.coupon
      if (coupon.enable === 0) {
        wx.showToast({ title: coupon.error_msg, icon: 'none' })
        return
      }
      const used = coupon.selected === 1
      if (used) {
        await API_Trade.useCoupon(coupon.seller_id, 0, this.data.way)
      } else {
        await API_Trade.useCoupon(coupon.seller_id, coupon.member_coupon_id, this.data.way)
      }
      this.triggerEvent('changed', used ? '' : coupon)
      this.selectComponent('#bottomFrame').hideFrame();
    }
  }
})

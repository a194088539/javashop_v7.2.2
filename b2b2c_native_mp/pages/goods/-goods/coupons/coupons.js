import * as API_Members from '../../../../api/members'
import * as API_Promotions from '../../../../api/promotions'
import { Foundation } from '../../../../ui-utils/index.js'
Component({
  properties: {
    goodsId: String,
    selectedSku:{
      type: Object,
      observer(val) {
        if (!this.data.couponsAll || !this.data.couponsAll.length) return
        if (val) {
          const sku_coupons = this.data.couponsAll.find(key => key.sku_id === val.sku_id).coupon_list
          this.setData({ coupons: sku_coupons })
        }
      }
    }
  },
  data: {
    coupons: '',
    couponsAll: []
  },
  lifetimes: {
    ready() {
      // 获取店铺优惠券
      API_Promotions.getOwnCoupons(this.data.goodsId).then(response => {
        response.forEach(key => {
          key.coupon_list.forEach(item => {
            item.start_time = Foundation.unixToDate(item.start_time, 'yyyy-MM-dd')
            item.end_time = Foundation.unixToDate(item.end_time, 'yyyy-MM-dd')
          })
        })
        this.setData({ couponsAll: response })
      })
    }
  },
  methods: {
    /** 显示弹窗 */
    popup() {
      this.selectComponent('#bottomFrame').showFrame();
    },
    // 领取店铺优惠券
    handleReceiveCoupon(e) {
      const coupon = e.target.dataset.coupon
      if (!wx.getStorageSync('refresh_token')) {
        wx.showToast({ title: '您还未登录!', image: '../../static/images/icon_error.png' })
        return false
      }
      API_Members.receiveCoupons(coupon.coupon_id).then(() => {
        wx.showToast({ title: '领取成功' })
      })
    }
  }
})
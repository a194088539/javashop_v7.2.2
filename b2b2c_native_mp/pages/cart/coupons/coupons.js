const app = getApp()
const middleware = require('../../../utils/middleware.js')
import * as API_Promotions from '../../../api/promotions'
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index'

Page(middleware.identityFilter({
    data: {
      shop_id: '',
      coupons: ''
    },
    // 页面初始化 options为页面跳转所带来的参数
    onLoad(options) {
      this.setData({ shop_id: options.shop_id })
      this.GET_Coupons()
    },
    /** 领取优惠券 */
    handleReceiveCoupons(e) {
      const coupon = e.currentTarget.dataset.coupon
      API_Members.receiveCoupons(coupon.coupon_id).then(() => {
        this.GET_Coupons()
        wx.showToast({ title: '领取成功!', icon: 'success' })
      })
    },
    /** 获取店铺优惠券 */
    GET_Coupons() {
      API_Promotions.getShopCoupons(this.data.shop_id).then(response => {
        let _coupons = response
        _coupons.forEach(key => {
          key.start_time = Foundation.unixToDate(key.start_time, 'yyyy-MM-dd')
          key.end_time = Foundation.unixToDate(key.end_time, 'yyyy-MM-dd')
          key.coupon_threshold_price = Foundation.formatPrice(key.coupon_threshold_price)
          key.coupon_price = Foundation.formatPrice(key.coupon_price)
        })
        this.setData({ coupons: _coupons })
      })
    }
  })
)
/**
 * 添加/修改 优惠券
 */
import Vue from 'vue'
import Coupon from './main'

Coupon.install = function() {
  Vue.component(Coupon.name, Coupon)
}

export default Coupon

const app = getApp();
let middleware = require('../../../utils/middleware.js');
import * as API_Order from '../../../api/order'
import { Foundation } from '../../../ui-utils/index.js'

Page({
  data: {
    order_sn: '',//订单编号
    order: '',//订单信息
    skuList:'',//规格列表
    num: 0,// 计算当前订单中的商品件数
  },
  onLoad: function (options) {
    // options为页面跳转所带来的参数页面初始化 
    this.setData({order_sn: options.order_sn})
  },
  onShow: function () {
    this.setData({
      order: '',
      skuList: '',
      num: 0,
    })
    this.getOrderDetail()
  },
  getOrderDetail: function () {
    API_Order.getOrderDetail(this.data.order_sn).then(data => {
      data.create_time = Foundation.unixToDate(data.create_time)
      data.need_pay_money = Foundation.formatPrice(data.need_pay_money)
      data.order_price = Foundation.formatPrice(data.order_price)
      data.balance = Foundation.formatPrice(data.balance)
      data.goods_price = Foundation.formatPrice(data.goods_price)
      data.discount_price = Foundation.formatPrice(data.discount_price)
      data.shipping_price = Foundation.formatPrice(data.shipping_price)
      if (data.coupon_price !== 0) { data.coupon_price = Foundation.formatPrice(data.coupon_price)}
      if(data.cash_back !== 0) { data.cash_back = Foundation.formatPrice(data.cash_back)}
      if(data.gift_list){
        data.gift_list.forEach(key => {
          key.gift_price = Foundation.formatPrice(key.gift_price)
        })
      } 
      if(data.receipt_history){
        data.receipt_history.receipt_type = this.receiptType(data.receipt_history.receipt_type)
      }
      if (data.gift_coupon) { data.gift_coupon.amount = Foundation.formatPrice(data.gift_coupon.amount)} 
      const skuList = data['order_sku_list']
      if(skuList && skuList.length){
        skuList.forEach(key=>{
          key.purchase_price = Foundation.formatPrice(key.purchase_price)
          if (!key.skuName) key.skuName = this.formatterSkuSpec(key)
          if(key && key.num){
            this.setData({
              num : this.data.num += key.num
            })
          }
        })
      }
      this.setData({
        order:data,
        skuList: skuList
      })
    })
  },
  receiptType(type){
    switch(type){
      case 'VATORDINARY':return '增值税普通发票'
      case 'ELECTRO':return '电子普通发票'
      case 'VATOSPECIAL':return '增值税专用发票'
      default : return '不开发票'
    }
  },
  /** 规格格式化显示 */
  formatterSkuSpec(sku) {
    if (!sku.spec_list || !sku.spec_list.length) return ''
    return sku.spec_list.map(spec => spec.spec_value).join(' - ')
  },
  //显示发票弹框
  popup() { this.selectComponent('#bottomFrame').showFrame() },
  // “取消订单”点击效果
  cancelOrder: function () {
    let that = this;
    let orderInfo = that.data.orderInfo;

    wx.showModal({
      title: '',
      content: '确定要取消此订单？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderCancel, {
            orderId: orderInfo.id
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '取消订单成功'
              });
              util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },
  // “取消订单并退款”点击效果
  refundOrder: function () {
    let that = this;
    let orderInfo = that.data.orderInfo;

    wx.showModal({
      title: '',
      content: '确定要取消此订单？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderRefund, {
            orderId: orderInfo.id
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '取消订单成功'
              });
              util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },  
  // “删除”点击效果
  deleteOrder: function () {
    let that = this;
    let orderInfo = that.data.orderInfo;

    wx.showModal({
      title: '',
      content: '确定要删除此订单？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderDelete, {
            orderId: orderInfo.id
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '删除订单成功'
              });
              util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },  
  // “确认收货”点击效果
  confirmOrder: function () {
    let that = this;
    let orderInfo = that.data.orderInfo;

    wx.showModal({
      title: '',
      content: '确认收货？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderConfirm, {
            orderId: orderInfo.id
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '确认收货成功！'
              });
              util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  }
})
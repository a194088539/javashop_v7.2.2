/**
 * 交♂易相关API
 */

import request from '../utils/request'

/**
 * 获取购物车列表
 * @param show_type 要显示的类型 all：全部 checked：已选中的
 */
export function getCarts(show_type = 'all', way = 'CART') {
  return request.ajax({
    url: `/trade/carts/${show_type}`,
    method: 'get',
    params: { way }
  })
}

/**
 * 添加货品到购物车
 * @param sku_id      产品ID
 * @param num         产品的购买数量
 * @param activity_id 默认参与的活动id
 */
export function addToCart(sku_id, num = 1, activity_id, promotion_type) {
  return request.ajax({
    url: '/trade/carts',
    method: 'post',
    params: { sku_id, num, activity_id, promotion_type }
  })
}

/**
 * 立即购买
 * @param sku_id
 * @param num
 * @param activity_id
 */
export function buyNow(sku_id, num = 1, activity_id, promotion_type) {
  return request.ajax({
    url: '/trade/carts/buy',
    method: 'post',
    needToken: true,
    params: { sku_id, num, activity_id, promotion_type }
  })
}

/**
 * 更新购物车商品数量
 * @param sku_id
 * @param num
 */
export function updateSkuNum(sku_id, num = 1) {
  return request.ajax({
    url: `/trade/carts/sku/${sku_id}`,
    method: 'post',
    params: { num }
  })
}

/**
 * 更新购物车货品选中状态
 * @param sku_id
 * @param checked
 */
export function updateSkuChecked(sku_id, checked = true) {
  return request.ajax({
    url: `/trade/carts/sku/${sku_id}`,
    method: 'post',
    params: { checked }
  })
}

/**
 * 删除多个货品项
 * @param sku_ids
 */
export function deleteSkuItem(sku_ids) {
  return request.ajax({
    url: `/trade/carts/${sku_ids}/sku`,
    method: 'delete'
  })
}

/**
 * 清空购物车
 */
export function cleanCarts() {
  return request.ajax({
    url: '/trade/carts',
    method: 'delete'
  })
}

/**
 * 设置全部货品为选中或不选中
 * @param checked
 */
export function checkAll(checked) {
  return request.ajax({
    url: '/trade/carts/checked',
    method: 'post',
    params: { checked }
  })
}

/**
 * 设置店铺内全部货品选中状态
 * @param shop_id
 * @param checked
 */
export function checkShop(shop_id, checked) {
  return request.ajax({
    url: `/trade/carts/seller/${shop_id}`,
    method: 'post',
    params: { checked }
  })
}

/**
 * 获取结算参数
 */
export function getCheckoutParams() {
  return request.ajax({
    url: '/trade/checkout-params',
    method: 'get'
  })
}

/**
 * 设置收货地址ID
 * @param address_id
 */
export function setAddressId(address_id) {
  return request.ajax({
    url: `/trade/checkout-params/address-id/${address_id}`,
    method: 'post'
  })
}

/**
 * 设置支付类型
 * @param payment_type
 */
export function setPaymentType(payment_type = 'ONLINE') {
  return request.ajax({
    url: '/trade/checkout-params/payment-type',
    method: 'post',
    params: { payment_type }
  })
}

/**
 * 设置发票信息
 * @param params
 */
export function setRecepit(params) {
  return request.ajax({
    url: '/trade/checkout-params/receipt',
    method: 'post',
    params
  })
}

/**
 * 取消使用发票
 */
export function cancelReceipt() {
  return request.ajax({
    url: '/trade/checkout-params/receipt',
    method: 'delete'
  })
}

/**
 * 设置送货时间
 * @param receive_time
 */
export function setReceiveTime(receive_time) {
  return request.ajax({
    url: '/trade/checkout-params/receive-time',
    method: 'post',
    params: { receive_time }
  })
}

/**
 * 设置订单备注
 * @param remark
 */
export function setRemark(remark) {
  return request.ajax({
    url: '/trade/checkout-params/remark',
    method: 'post',
    params: { remark }
  })
}

/**
 * 创建订单
 */
export function createTrade(client = 'MINI',way) {
  return request.ajax({
    url: '/trade/create',
    method: 'post',
    data: { client,way }
  })
}

/**
 * 获取支付方式列表
 * @param client_type
 */
export function getPaymentList(client_type = 'PC') {
  return request.ajax({
    url: `/payment/${client_type}`,
    method: 'get'
  })
}

/**
 * 根据交易编号或订单编号查询收银台数据
 * @param params
 */
export function getCashierData(params) {
  return request.ajax({
    url: '/trade/orders/cashier',
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 主动查询支付结果
 * @param trade_type
 * @param sn
 * @param params
 */
export function getPayStatus(trade_type, sn, params) {
  return request.ajax({
    url: `/order/pay/query/${trade_type}/${sn}`,
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 获取微信扫描支付的状态
 * @param sn
 */
export function getWeChatQrStatus(sn) {
  return request.ajax({
    url: `/payment/weixin/status/${sn}`,
    method: 'get',
    needToken: true
  })
}

/**
 * 对一个交易发起支付
 * @param trade_type
 * @param sn
 * @param params
 */
export function initiatePay(trade_type, sn, params) {
  return request.ajax({
    url: `/order/pay/${trade_type}/${sn}`,
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 查询物流
 * @param id
 * @param num
 */
export function getExpress(id, num) {
  return request.ajax({
    url: '/express',
    method: 'get',
    needToken: true,
    params: { id, num }
  })
}

/**
 * 使用优惠券
 * @param shop_id
 * @param mc_id
 */
export function useCoupon(shop_id, coupon_id, way) {
  return request.ajax({
    url: `/trade/promotion/${shop_id}/seller/${coupon_id}/coupon?way=${way}`,
    method: 'post',
    needToken: true
  })
}

/**
 * 获取订单流程图
 * @param order_sn
 */
export function getOrderFlow(order_sn) {
  return request.ajax({
    url: `/trade/orders/${order_sn}/flow`,
    method: 'get',
    needToken: true
  })
}

/**
 * 获取订单交易快照数据
 * @param id
 */
export function getSnapshot(id) {
  return request.ajax({
    url: `/trade/snapshots/${id}`,
    method: 'get',
    needToken: true
  })
}

/**
 * 获取商品销售记录
 * @param goods_id
 * @param params
 */
export function getGoodsSales(goods_id, params) {
  return request.ajax({
    url: `/trade/goods/${goods_id}/sales`,
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 更换参与活动
 * @param params
 */
export function changeActivity(params) {
  return request.ajax({
    url: '/trade/promotion',
    method: 'post',
    needToken: true,
    params
  })
}

/**
 * 不参加促销活动
 * @param params
 */
export function cleanPromotion(seller_id = '', sku_id = '') {
  return request.ajax({
    url: `/trade/promotion?seller_id=${seller_id}&sku_id=${sku_id}`,
    method: 'delete',
    needToken: true
  })
}

/**
 * 向拼团购物车
 * @param sku_id
 * @param num
 */
export function addToAssembleCart(sku_id, num = 1) {
  return request.ajax({
    url: '/pintuan/cart/sku',
    method: 'post',
    needToken: true,
    data: { sku_id, num }
  })
}

/**
 * 获取拼团购物车数据
 */
export function getAssembleCarts() {
  return request.ajax({
    url: '/pintuan/cart',
    method: 'get',
    needToken: true,
    loading: false
  })
}

/**
 * 创建拼团交易
 * @param client
 * @param pintuan_order_id
 */
export function createAssembleTrade(client = 'MINI', pintuan_order_id = '') {
  return request.ajax({
    url: '/pintuan/trade',
    method: 'post',
    needToken: true,
    data: { client, pintuan_order_id }
  })
}


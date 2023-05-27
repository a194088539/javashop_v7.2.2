/**
 * 订单相关API
 */

import request from '../utils/request'

/**
 * 获取订单列表
 * @param params
 */
export function getOrderList(params) {
  return request.ajax({
    url: '/trade/orders',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取订单详情
 * @param order_sn 订单编号
 */
export function getOrderDetail(order_sn) {
  return request.ajax({
    url: `/trade/orders/${order_sn}`,
    method: 'get',
    loading: true
  })
}

/**
 * 取消订单
 * @param order_sn 订单编号
 * @param reason   取消原因
 */
export function cancelOrder(order_sn, reason) {
  return request.ajax({
    url: `/trade/orders/${order_sn}/cancel`,
    method: 'post',
    params: { reason }
  })
}

/**
 * 确认收货
 * @param order_sn 订单编号
 */
export function confirmReceipt(order_sn) {
  return request.ajax({
    url: `/trade/orders/${order_sn}/rog`,
    method: 'post'
  })
}

/**
 * 获取订单状态数量
 */
export function getOrderStatusNum() {
  return request.ajax({
    url: '/trade/orders/status-num',
    method: 'get'
  })
}

/**
 * 根据交易单号查询订单列表
 * @param trade_sn
 */
export function getOrderListByTradeSn(trade_sn) {
  return request.ajax({
    url: `/trade/orders/${trade_sn}/list`,
    method: 'get'
  })
}

/**
 * 获取某个拼团订单的详情
 * @param order_sn
 */
export function getAssembleOrderDetail(order_sn) {
  return request.ajax({
    url: `/pintuan/orders/${order_sn}`,
    method: 'get'
  })
}

/**
 * 拼团猜你喜欢
 * @param order_sn
 */
export function getAssembleGuest(order_sn, num = 6) {
  return request.ajax({
    url: `/pintuan/orders/${order_sn}/guest`,
    method: 'get',
    params: { num }
  })
}

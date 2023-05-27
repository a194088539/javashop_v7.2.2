/**
 * 订单相关API
 */

import request, {Method} from '../utils/request';

/**
 * 获取订单列表
 * @param params
 */
export function getOrderList(params) {
  return request({
    url: 'trade/orders',
    method: Method.GET,
    needToken: true,
    params,
  });
}

/**
 * 获取订单详情
 * @param order_sn 订单编号
 */
export function getOrderDetail(order_sn) {
  return request({
    url: `trade/orders/${order_sn}`,
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 取消订单
 * @param order_sn 订单编号
 * @param reason   取消原因
 */
export function cancelOrder(order_sn, reason) {
  return request({
    url: `trade/orders/${order_sn}/cancel`,
    method: Method.POST,
    needToken: true,
    data: {reason},
  });
}

/**
 * 确认收货
 * @param order_sn 订单编号
 */
export function confirmReceipt(order_sn) {
  return request({
    url: `trade/orders/${order_sn}/rog`,
    method: Method.POST,
    needToken: true,
  });
}

/**
 * 获取订单状态数量
 */
export function getOrderStatusNum() {
  return request({
    url: 'trade/orders/status-num',
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 根据交易单号查询订单列表
 * @param trade_sn
 */
export function getOrderListByTradeSn(trade_sn) {
  return request({
    url: `trade/orders/${trade_sn}/list`,
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 获取拼团订单
 * @param orderSn
 * @returns {AxiosPromise<any>}
 */
export function getPinTuanOrder(orderSn) {
  return request({
    url: `pintuan/orders/${orderSn}`,
    method: Method.GET,
    needToken: true,
    loading: false,
  });
}

/**
 * 获取交易投诉列表
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function getOrderComplains(params) {
  return request({
    url: `trade/order-complains`,
    method: Method.GET,
    needToken: true,
    loading: false,
    params: params,
  });
}

/**
 * 获取交易投诉详情
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function getOrderComplainsDetail(params) {
  return request({
    url: `trade/order-complains/${params.complain_id}`,
    method: Method.GET,
    needToken: true,
    loading: false,
    params: params,
  });
}

/**
 * 获取交易投诉状态集合
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function getOrderComplainsStatus(params) {
  return request({
    url: `trade/order-complains/${params.complain_id}/flow`,
    method: Method.GET,
    needToken: true,
    loading: false,
    params: params,
  });
}

/**
 * 获取交易投诉主题集合
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function getOrderComplainsTopics(params) {
  return request({
    url: `trade/order-complains/${params.complain_id}/flow`,
    method: Method.GET,
    needToken: true,
    loading: false,
    params: params,
  });
}


/**
 * 获取交易投诉主题集合
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function applyComplains(params) {
  return request({
    url: `trade/order-complains`,
    method: Method.POST,
    needToken: true,
    loading: false,
    data: params,
  });
}

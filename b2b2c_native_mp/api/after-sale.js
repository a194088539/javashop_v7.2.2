/**
 * 申请售后相关API
 */

import request from '../utils/request'

/**
 * 获取售后列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getAfterSale(params) {
  return request.ajax({
    url: '/after-sales/refunds',
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 获取售后申请数据
 * @param order_sn
 * @param sku_id
 */
export function getAfterSaleData(order_sn, sku_id) {
  const params = sku_id ? { sku_id } : {}
  return request.ajax({
    url: `/after-sales/refunds/apply/${order_sn}`,
    method: 'get',
    params
  })
}

/**
 * 获取售后详情
 * @param sn 订单编号
 * @returns {AxiosPromise}
 */
export function getAfterSaleDetail(sn) {
  return request.ajax({
    url: `/after-sales/refund/${sn}`,
    method: 'get'
  })
}

/**
 * 申请退款
 * @param params
 */
export function applyAfterSaleMoney(params) {
  return request.ajax({
    url: '/after-sales/refunds/apply',
    method: 'post',
    params
  })
}

/**
 * 申请退货
 * @param params
 */
export function applyAfterSaleGoods(params) {
  return request.ajax({
    url: '/after-sales/return-goods/apply',
    method: 'post',
    params
  })
}

/**
 * 申请取消订单
 * @param params
 */
export function applyAfterSaleCancel(params) {
  return request.ajax({
    url: '/after-sales/refunds/cancel-order',
    method: 'post', 
    params
  })
}

/******************* 以下为新方法 ***********************/
/**
 * 申请取消订单
 * @param params
 */
export function applyCancelOrder(params) {
  return request.ajax({
    url: '/after-sales/apply/cancel/order',
    method: 'post',
    data: params
  })
}

/**
 * 获取售后服务记录相关数据
 * @param params 参数
 */
export function getAfterSaleList(params) {
  return request.ajax({
    url: `/after-sales`,
    method:'get',
    params
  })
}

/**
 * 获取售后退款单相关数据
 * @param params 参数
 */
export function getRefundList(params) {
  return request.ajax({
    url: `/buyer/after-sales/refund`,
    method: 'get',
    params
  })
}

/**
 * 获取售后服务详情数据
 * @param service_sn 售后服务单编号
 */
export function getServiceDetail(service_sn) {
  return request.ajax({
    url: `/after-sales/detail/${service_sn}`,
    method: 'get'
  })
}

/**
 * 获取售后服务操作日志信息
 * @param service_sn 售后服务单编号
 */
export function getServiceLogs(service_sn) {
  return request.ajax({
    url: `/after-sales/log/${service_sn}`,
    method: 'get'
  })
}

/**
 * 获取申请售后页面相关数据
 * @param order_sn 订单编号
 * @param sku_id 商品skuID
 */
export function getApplyInfo(order_sn, sku_id) {
  return request.ajax({
    url: `/after-sales/apply/${order_sn}/${sku_id}`,
    method: 'get'
  })
}

/**
 * 申请退货服务
 * @param params
 */
export function applyReturnGoods(params) {
  return request.ajax({
    url: '/after-sales/apply/return/goods',
    method: 'post',
    data: params
  })
}

/**
 * 申请换货服务
 * @param params
 */
export function applyChangeGoods(params) {
  return request.ajax({
    url: '/after-sales/apply/change/goods',
    method: 'post',
    data: params
  })
}

/**
 * 申请补发商品服务
 * @param params
 */
export function applyReplaceGoods(params) {
  return request.ajax({
    url: '/after-sales/apply/replace/goods',
    method: 'post',
    data: params
  })
}

/**
 * 填充物流信息
 * @param service_sn 售后服务单号
 * @param params 参数信息
 */
export function fillShipInfo(service_sn, params) {
  return request.ajax({
    url: `/after-sales/apply/ship/${service_sn}`,
    method: 'post',
    data: params
  })
}

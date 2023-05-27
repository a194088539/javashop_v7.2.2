/**
 * 申请售后相关API
 */

import request, {Method} from '../utils/request';

/**
 * 获取售后列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getAfterSale(params) {
  return request({
    url: 'after-sales/refunds',
    method: Method.GET,
    needToken: true,
    params,
  });
}

/**
 * 获取售后服务详情数据
 * @param service_sn 售后服务单编号
 */
export function getServiceDetail(service_sn) {
  return request({
    url: `after-sales/detail/${service_sn}`,
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 获取售后服务记录相关数据
 * @param params 参数
 */
export function getAfterSaleList(params) {
  return request({
    url: 'after-sales',
    method: Method.GET,
    needToken: true,
    params,
  });
}

/**
 * 获取售后申请数据
 * @param order_sn
 * @param sku_id
 */
export function getAfterSaleData(order_sn, sku_id) {
  return request({
    url: `after-sales/apply/${order_sn}/${sku_id}`,
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 获取售后详情
 * @param sn 订单编号
 * @returns {AxiosPromise}
 */
export function getAfterSaleDetail(sn) {
  return request({
    url: `after-sales/refund/${sn}`,
    method: Method.GET,
    needToken: true,
  });
}

/**
 * 申请退款
 * @param params
 */
export function applyAfterSaleMoney(params) {
  return request({
    url: 'after-sales/refunds/apply',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请退货
 * @param params
 */
export function applyAfterSaleGoods(params) {
  return request({
    url: 'after-sales/return-goods/apply',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请取消订单
 * @param params
 */
export function applyAfterSaleCancel(params) {
  return request({
    url: 'after-sales/refunds/cancel-order',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请退货服务
 * @param params
 */
export function applyReturnGoods(params) {
  return request({
    url: 'after-sales/apply/return/goods',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请换货服务
 * @param params
 */
export function applyChangeGoods(params) {
  return request({
    url: 'after-sales/apply/change/goods',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请补发商品服务
 * @param params
 */
export function applyReplaceGoods(params) {
  return request({
    url: 'after-sales/apply/replace/goods',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 * 申请取消订单
 * @param params
 */
export function applyCancelOrder(params) {
  return request({
    url: 'after-sales/apply/cancel/order',
    method: Method.POST,
    needToken: true,
    data: params,
  });
}

/**
 *
 保存用户填写的物流信息
 * @param params
 */
export function saveShip(params) {
  return request({
    url: `/after-sales/apply/ship/${params.service_sn}`,
    method: Method.POST,
    needToken: true,
    params,
  });
}

/**
 * 读取售后日志
 * @param params
 * @returns {AxiosPromise | Promise | Promise<unknown> | Promise<R>}
 */
export function getAfterSaleLog(params) {
  return request({
    url: `after-sales/log/${params.service_sn}`,
    method: Method.GET,
    needToken: true,
    params,
  });
}

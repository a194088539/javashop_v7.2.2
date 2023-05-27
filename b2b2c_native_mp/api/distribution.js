import request from '../utils/request'

/**
 * 生成短链接
 */
export function generateShortLink() {
  return request.ajax({
    url: '/distribution/su/get-short-url',
    method: 'post'
  })
}

/**
 * 访问短链接
 * @param params
 */
export function accessShortLink(params) {
  return request.ajax({
    url: '/distribution/su/visit',
    method: 'get',
    params
  })
}

/**
 * 生成小程序码
 * @param params
 */
export function getMiniprogramCode(goods_id) {
  return request.ajax({
    url: '/passport/mini-program/code-unlimit',
    method: 'get',
    params:{ goods_id }
  })
}
/**
 * 访问小程序码
 * @param params
 */
export function visitMiniprogramCode(params) {
  return request.ajax({
    url: '/passport/mini-program/distribution',
    method: 'post',
    params
  })
}

/**
 * 获取我的推荐人
 */
export function getMyRefereer() {
  return request.ajax({
    url: '/distribution/recommend-me',
    method: 'get'
  })
}

/**
 * 获取推荐人列表
 */
export function getRefereeList() {
  return request.ajax({
    url: '/distribution/lower-list',
    method: 'get', 
    loading: true
  })
}

/**
 * 获取与我相关的结算单信息
 * @param params
 */
export function getSettlementTotal(params) {
  return request.ajax({
    url: '/distribution/bill/member',
    method: 'get',
    params
  })
}

/**
 * 获取与我相关的订单信息
 * @param params
 */
export function getRelevantList(params) {
  return request.ajax({
    url: '/distribution/bill/order-list',
    method: 'get', 
    params
  })
}

/**
 * 获取与我相关的退款单信息
 * @param params
 */
export function getRelevantRefundList(params) {
  return request.ajax({
    url: '/distribution/bill/sellback-order-list',
    method: 'get', 
    params
  })
}

/**
 * 获取我的历史业绩
 * @param params
 */
export function getMyHistoryList(params) {
  return request.ajax({
    url: '/distribution/bill/history',
    method: 'get',
    params
  })
}

/**
 * 获取提现参数设置
 */
export function getWithdrawalsParams() {
  return request.ajax({
    url: '/distribution/withdraw/params',
    method: 'get'
  })
}

/**
 * 保存提现设置
 * @param params
 */
export function reserveWithdrawalsParams(params) {
  return request.ajax({
    url: '/distribution/withdraw/params',
    method: 'put',
    params
  })
}

/**
 * 申请提现
 * @param params
 */
export function applyWithdrawals(params) {
  return request.ajax({
    url: '/distribution/withdraw/apply-withdraw',
    method: 'post',
    params
  })
}

/**
 * 获取可提现金额
 */
export function getWithdrawalsCanRebate() {
  return request.ajax({
    url: '/distribution/withdraw/can-rebate',
    method: 'get'
  })
}

/**
 * 获取提现记录
 * @param params
 */
export function getWithdrawalsList(params) {
  return request.ajax({
    url: '/distribution/withdraw/apply-history',
    method: 'get',
    params
  })
}

/** 预存款相关API */

import request, { Method } from '@/utils/request'
import md5 from 'js-md5'

/**
 * 预存款余额
 */
export function getDepositBalance(){
  return request({
    url: 'members/wallet',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 创建充值订单
 */
export function createRechargeOrder(price){
  return request({
    url: 'recharge',
    method: Method.POST,
    needToken: true,
    params: { price }
  })
}

/**
 * 支付充值订单
 */
export function paymentRechargeOrder(params, sn){
  return request({
    url: `recharge/${sn}`,
    method: Method.POST,
    needToken: true,
    params
  })
}

/**
 * 余额充值记录
 */
export function balanceRechargeLogs(params){
  return request({
    url: `members/recharge/list`,
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 预存款日志
 * @param params
 */
export function getDepositLogsList(params) {
  return request({
    url: 'members/deposite/log/list',
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 预存款 支付
 * @param  trade_type
 * @param  sn
 * @param  params
 */
export function getBalancePay(trade_type, sn, params) {
  params = JSON.parse(JSON.stringify(params))
  params.password = md5(params.password)
  return request({
    url: `balance/pay/${trade_type}/${sn}`,
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 检测会员是否设置过支付密码
 */
export function checkPassword() {
  return request({
    url: 'members/wallet/check',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 获取账户信息
 */
export function getMemberAccount() {
  return request({
    url: 'members/wallet/info',
    method: Method.GET,
    needToken: true
  })
}

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
 * 预存款充值记录
 * @param params
 */
export function getRechargeList(params) {
  return request({
    url: 'members/recharge/list',
    method: Method.GET,
    needToken: true,
    params
  })
}
/** 
 * 预存款 创建充值订单
 * @param price 充值金额
 */
export function getRecharge(price) {
  return request({
    url: 'recharge',
    method: Method.POST,
    needToken: true,
    params: { price }
  })
}

/** 
 * 预存款 支付充值订单
 * @param  sn     
 * @param  params
 */
export function getRechargeInitiatePay(sn, params) {
  return request({
    url: `recharge/${sn}`,
    method: Method.POST,
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
 * 预存款相关
 * @param  params
 */
export function getCashier(params) {
  return request({
    url: 'members/wallet/cashier',
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
/**
 * 发送验证码
 * @param  uuid
 * @param  captcha
 * @param  scene
 */
export function sendMobileSms(params) {
  return request({
    url: 'members/wallet/smscode',
    method: Method.POST,
    needToken: true,
    params
  })
}

/**
 *验证修改密码验证码
 * @param sms_code
 */
export function validChangePasswordSms(sms_code) {
  return request({
    url: 'members/wallet/check/smscode',
    method: Method.GET,
    needToken: true,
    params: { sms_code }
  })
}

/**
 * 设置支付密码
 * @param params
 */
export function setPaymentPassword(params) {
  params = JSON.parse(JSON.stringify(params))
  params.password = md5(params.password)
  return request({
    url: 'members/wallet/set-pwd',
    method: Method.POST,
    needToken: true,
    params
  })
}
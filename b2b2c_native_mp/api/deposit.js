/** 预存款相关API */

import request from '../utils/request'
import { md5 } from '../lib/md5'

/** 
 * 预存款余额
 */
export function getDepositBalance(){
  return request.ajax({
	url: '/members/wallet',
	method: 'get',
	needToken: true
  })
}

/**
 * 预存款日志
 * @param params
 */
export function getDepositLogsList(params) {
  return request.ajax({
    url: '/members/deposite/log/list',
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 预存款充值记录
 * @param params
 */
export function getRechargeList(params) {
  return request.ajax({
    url: '/members/recharge/list',
    method: 'get',
    needToken: true,
    params
  })
}
/** 
 * 预存款 创建充值订单
 * @param price 充值金额
 */
export function getRecharge(price) {
  return request.ajax({
    url: '/recharge',
    method: 'post',
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
  return request.ajax({
    url: `/recharge/${sn}`,
    method: 'post',
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
  return request.ajax({
    url: `/balance/pay/${trade_type}/${sn}`,
    method: 'get',
    needToken: true,
    params
  })
}

/** 
 * 预存款相关
 * @param  params
 */
export function getCashier(params) {
  return request.ajax({
    url: '/members/wallet/cashier',
    method: 'get',
    needToken: true,
    params
  })
}

/** 
 * 检测会员是否设置过支付密码
 */
export function checkPassword() {
  return request.ajax({
    url: '/members/wallet/check',
    method: 'get',
    needToken: true
  })
}

/** 
 * 获取账户信息
 */
export function getMemberAccount() {
  return request.ajax({
    url: '/members/wallet/info',
    method: 'get',
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
  params.mini_client = 'MINI'
  return request.ajax({
    url: '/members/wallet/smscode',
    method: 'post',
    needToken: true,
    params
  })
}

/**
 *验证修改密码验证码
 * @param sms_code
 */
export function validChangePasswordSms(sms_code) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: '/members/wallet/check/smscode',
    method: 'get',
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
  return request.ajax({
    url: '/members/wallet/set-pwd',
    method: 'post',
    needToken: true,
    params
  })
}
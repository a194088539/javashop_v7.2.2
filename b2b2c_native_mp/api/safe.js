/**
 * 安全相关API
 */
import request from '../utils/request'
import { md5 } from '../lib/md5.js'

/**
 * 发送绑定手机验证码
 * @param mobile
 * @param captcha
 * @param uuid
 */
export function sendBindMobileSms(mobile, params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/members/security/bind/send/${mobile}`,
    method: 'post',
    needToken: true,
    data: params
  })
}

/**
 * 绑定手机号
 * @param mobile
 * @param sms_code
 */
export function bindMobile(mobile, sms_code) {
  return request.ajax({
    url: `/members/security/bind/${mobile}`,
    method: put,
    needToken: true,
    params: { sms_code }
  })
}

/**
 * 发送手机验证码
 * 在修改手机号和更改密码时通用
 * @param uuid
 * @param captcha
 */
export function sendMobileSms(params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: '/members/security/send',
    method: 'post',
    needToken: true,
    data: params
  })
}

/**
 * 验证更换手机号短信
 * @param sms_code
 */
export function validChangeMobileSms(sms_code, mini_client = 'MINI') {
  return request.ajax({
    url: '/members/security/exchange-bind',
    method: 'get',
    needToken: true,
    params: { sms_code, mini_client }
  })
}

/**
 * 更换手机号
 * @param mobile
 * @param sms_code
 */
export function changeMobile(mobile, sms_code) {
  return request.ajax({
    url: `/members/security/exchange-bind/${mobile}`,
    method: 'put',
    needToken: true,
    params: { sms_code }
  })
}

/**
 * 验证更改密码手机短信
 * @param sms_code
 */
export function validChangePasswordSms(sms_code, mini_client = 'MINI') {
  return request.ajax({
    url: '/members/security/password',
    method: 'get',
    needToken: true,
    params: { sms_code, mini_client }
  })
}

/**
 * 更改密码
 * @param uuid
 * @param captcha
 * @param password
 */
export function changePassword(params) {
  params.password = md5(params.password)
  params.mini_client = 'MINI'
  return request.ajax({
    url: '/members/security/password',
    method: 'put', 
    needToken: true,
    data: params
  })
}

/**
 * 发送绑定电子邮箱验证码
 * @param email
 * @param captcha
 * @param uuid
 */
export function sendBindEmailCode(params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/members/security/bind/email/send`,
    method: 'post',
    needToken: true,
    data: params
  })
}

/**
 * 绑定电子邮箱
 * @param email
 * @param email_code
 */
export function bindEmail(email, email_code) {
  return request.ajax({
    url: `/members/security/bind/email`,
    method: 'post',
    needToken: true,
    data: {
      email,
      email_code
    }
  })
}
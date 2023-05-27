/**
 * 安全相关API
 */

import request, { Method } from '@/utils/request'
import md5 from 'js-md5'

/**
 * 发送绑定手机验证码
 * @param mobile 手机号码
 * @param captcha 图片验证码
 * @param uuid 客户端唯一标识
 * @param scene 图片验证码业务类型
 */
export function sendBindMobileSms(mobile, params) {
  return request({
    url: `members/security/bind/send/${mobile}`,
    method: Method.POST,
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
  return request({
    url: `members/security/bind/${mobile}`,
    method: Method.PUT,
    needToken: true,
    data: { sms_code }
  })
}

/**
 * 发送手机验证码
 * 在修改手机号和更改密码时通用
 * @param uuid 客户端唯一标识
 * @param captcha 图片验证码
 * @param scene 图片验证码业务类型
 */
export function sendMobileSms(params) {
  return request({
    url: 'members/security/send',
    method: Method.POST,
    needToken: true,
    data: params
  })
}

/**
 * 验证更换手机号短信
 * @param sms_code
 */
export function validChangeMobileSms(sms_code) {
  return request({
    url: 'members/security/exchange-bind',
    method: Method.GET,
    needToken: true,
    params: { sms_code }
  })
}

/**
 * 更换手机号
 * @param mobile
 * @param sms_code
 */
export function changeMobile(mobile, sms_code) {
  return request({
    url: `members/security/exchange-bind/${mobile}`,
    method: Method.PUT,
    needToken: true,
    data: { sms_code }
  })
}

/**
 * 验证更改密码手机短信
 * @param sms_code
 */
export function validChangePasswordSms(sms_code) {
  return request({
    url: 'members/security/password',
    method: Method.GET,
    needToken: true,
    params: { sms_code }
  })
}

/**
 * 更改密码
 * @param uuid 客户端唯一标识
 * @param captcha 图片验证码
 * @param password 密码
 * @param scene 图片验证码业务类型
 */
export function changePassword(params) {
  return request({
    url: 'members/security/password',
    method: Method.PUT,
    needToken: true,
    data: params
  })
}

/**
 * 发送绑定电子邮箱验证码
 * @param email 电子邮箱地址
 * @param captcha 图片验证码
 * @param uuid 客户端唯一标识
 * @param scene 图片验证码业务类型
 */
export function sendBindEmailCode(params) {
  return request({
    url: `members/security/bind/email/send`,
    method: Method.POST,
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
  return request({
    url: `members/security/bind/email`,
    method: Method.POST,
    needToken: true,
    data: {
      email,
      email_code
    }
  })
}

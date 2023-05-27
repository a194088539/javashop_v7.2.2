/**
 * 安全相关API
 */

import request, {Method} from '../utils/request';
import {store} from '../redux/store';
import md5 from 'js-md5';

/**
 * 发送绑定手机验证码
 * @param mobile
 * @param captcha
 */
export function sendBindMobileSms(mobile, captcha, scene) {
  return request({
    url: `members/security/bind/send/${mobile}`,
    method: Method.POST,
    needToken: true,
    data: {
      uuid: store.getState().user.uuid,
      captcha,
      scene,
    },
  });
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
    data: {sms_code},
  });
}

/**
 * 发送手机验证码
 * 在修改手机号和更改密码时通用
 * @param captcha
 */
export function sendMobileSms(captcha, scene) {
  return request({
    url: 'members/security/send',
    method: Method.POST,
    needToken: true,
    data: {
      uuid: store.getState().user.uuid,
      captcha,
      scene,
    },
  });
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
    params: {sms_code},
  });
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
    data: {sms_code},
  });
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
    params: {sms_code},
  });
}

/**
 * 更改密码
 * @param captcha
 * @param password
 */
export function changePassword(captcha, password, scene) {
  return request({
    url: 'members/security/password',
    method: Method.PUT,
    needToken: true,
    data: {
      uuid: store.getState().user.uuid,
      captcha,
      password: md5(password),
      scene,
    },
  });
}
/**
 * 发送手机验证码
 * 在修改和设置支付密码时通用
 * @param captcha
 */
export function sendPayPwdSms(uuid, captcha, scene) {
  return request({
    url: '/members/wallet/smscode',
    method: Method.POST,
    needToken: true,
    data: {
      uuid,
      captcha,
      scene,
    },
  });
}
/**
 * 设置支付密码验证短信验证码
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function validPayPwdSms(params) {
  return request({
    url: '/members/wallet/check/smscode',
    method: Method.GET,
    needToken: true,
    params,
  });
}
/**
 * 设置支付密码
 * @param sms_code
 * @returns {AxiosPromise<any>}
 */
export function setDepositePwd(password) {
  return request({
    url: '/members/wallet/set-pwd',
    method: Method.POST,
    needToken: true,
    data: {
      password: md5(password),
    },
  });
}

/**
 * 获取账户信息
 * @param sms_code
 * @returns {AxiosPromise<any>}
 */
export function memberAccount() {
  return request({
    url: '/members/wallet/info',
    method: Method.GET,
    needToken: true,
  });
}

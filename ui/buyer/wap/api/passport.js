/**
 * Created by Andste on 2018/5/2.
 * 用户认证相关API
 */

import request, { Method } from '@/utils/request'
import { api } from '@/ui-domain'
import Storage from '@/utils/storage'
import md5 from 'js-md5'

/**
 * 普通登录
 * @param params
 */
export function login(params) {
  params = JSON.parse(JSON.stringify(params))
  params.password = md5(params.password)
  return request({
    url: `passport/login`,
    method: Method.POST,
    params
  })
}

/**
 * 通过手机号登录
 * @param mobile
 * @param sms_code
 */
export function loginByMobile(mobile, params) {
  return request({
    url: `passport/login/${mobile}`,
    method: Method.POST,
    params
  })
}

/**
 * 发送会员注册手机验证码
 * @param mobile 手机号码
 * @param captcha 图片验证码
 * @param scene 图片验证码业务类型
 */
export function sendRegisterPicSms(mobile, captcha, scene) {
  return request({
    url: `passport/register/smscode/${mobile}`,
    method: Method.POST,
    data: {
      captcha,
      uuid: Storage.getItem('uuid'),
      scene
    }
  })
}

/**
 * 发送会员注册手机滑块验证码
 * @param mobile 手机号码
 * @param c_sessionid
 * @param sig
 * @param nc_token
 */
export function sendRegisterSliderSms(mobile, c_sessionid, sig, nc_token) {
  return request({
    url: `passport/register/smscode/${mobile}`,
    method: Method.POST,
    data: {
      c_sessionid,
      sig,
      nc_token
    }
  })
}

/**
 * 发送会员登录手机验证码
 * @param mobile 手机号码
 * @param captcha 图片验证码
 * @param scene 图片验证码业务类型
 */
export function sendLoginSms(mobile, params) {
  return request({
    url: `passport/login/smscode/${mobile}`,
    method: Method.POST,
    data: params
  })
}

/**
 * 用户名重复校验
 * @param username
 */
export function checkUsernameRepeat(username) {
  return request({
    url: `passport/username/${username}`,
    method: Method.GET,
    loading: false
  })
}

/**
 * 手机号重复校验
 * @param mobile
 */
export function checkMobileRepeat(mobile) {
  return request({
    url: `passport/mobile/${mobile}`,
    method: Method.GET,
    loading: false
  })
}

/**
 * 注册会员【手机号】
 * @param mobile
 * @param password
 */
export function registerByMobile({ mobile, password }) {
  return request({
    url: `passport/register/wap`,
    method: Method.POST,
    data: {
      mobile,
      password: md5(password)
    }
  })
}

/**
 * 验证手机验证码
 * @param mobile   手机号码
 * @param scene    业务场景
 * @param sms_code 短信验证码
 */
export function validMobileSms(mobile, scene, sms_code) {
  return request({
    url: `passport/smscode/${mobile}`,
    method: Method.GET,
    params: {
      scene,
      sms_code
    }
  })
}

/**
 * 验证账户信息
 * @param uuid 客户端唯一标识
 * @param captcha 图片验证码
 * @param account 账户信息
 * @param scene 图片验证码业务类型
 */
export function validAccount(params) {
  return request({
    url: `passport/find-pwd`,
    method: Method.GET,
    params: params
  })
}

/**
 * 发送找回密码短信
 * @param uuid 客户端唯一标识
 * @param captcha 图片验证码
 * @param scene 图片验证码业务类型
 */
export function sendFindPasswordSms(params) {
  return request({
    url: `passport/find-pwd/send`,
    method: Method.POST,
    data: params
  })
}

/**
 * 校验找回密码验证码
 * @param uuid
 * @param sms_code
 */
export function validFindPasswordSms(uuid, sms_code) {
  return request({
    url: `passport/find-pwd/valid`,
    method: Method.GET,
    params: {
      uuid,
      sms_code
    }
  })
}

/**
 * 修改密码【找回密码用】
 * @param uuid
 * @param password
 */
export function changePassword(uuid, password) {
  return request({
    url: `passport/find-pwd/update-password`,
    method: Method.PUT,
    data: {
      uuid,
      password: md5(password)
    }
  })
}

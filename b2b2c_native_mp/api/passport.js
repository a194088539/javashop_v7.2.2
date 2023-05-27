/**
 * Created by Andste on 2018/5/2.
 * 用户认证相关API
 */

import request from '../utils/request'
import { api } from '../config/config'
import { md5 } from '../lib/md5'

/**
 * 普通登录
 * @param params
 */
export function login(params) {
  params = JSON.parse(JSON.stringify(params))
  params.password = md5(params.password)
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/login`,
    method: 'get',
    params
  })
}

/**
 * 通过手机号登录
 * @param mobile
 * @param sms_code
 */
export function loginByMobile(mobile, params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/login/${mobile}`,
    method: 'get',
    params
  })
}

/**
 * 发送会员注册手机验证码
 * @param mobile
 * @param captcha
 */
export function sendRegisterSms(mobile, captcha, scene, mini_client = 'MINI') {
  return request.ajax({
    url: `/passport/register/smscode/${mobile}`,
    method: 'post', 
    loading: true,
    params: {
      captcha,
      uuid:wx.getStorageSync('uuid'),
      scene,
      mini_client
    }
  })
}

/**
 * 发送会员登录手机验证码
 * @param mobile
 * @param captcha
 */
export function sendLoginSms(mobile, params) {
  return request.ajax({
    url: `/passport/login/smscode/${mobile}`,
    method: 'post',
    data: params
  })
}

/**
 * 用户名重复校验
 * @param username
 */
export function checkUsernameRepeat(username) {
  return request.ajax({
    url: `/passport/username/${username}`,
    method:'get'
  })
}

/**
 * 手机号重复校验
 * @param mobile
 */
export function checkMobileRepeat(mobile) {
  return request.ajax({
    url: `/passport/mobile/${mobile}`,
    method: 'get'
  })
}

/**
 * 注册会员【手机号】
 * @param mobile
 * @param password
 */
export function registerByMobile({ mobile, password }) {
  return request.ajax({
    url: '/passport/register/wap',
    method: 'post',
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
  return request.ajax({
    url: `/passport/smscode/${mobile}`,
    method: 'get',
    params: {
      scene,
      sms_code
    }
  })
}

/**
 * 验证账户信息
 * @param params
 */
export function validAccount(params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/find-pwd`,
    method: 'get', 
    params
  })
}

/**
 * 发送找回密码短信
 * @param params
 */
export function sendFindPasswordSms(params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/find-pwd/send`,
    method: 'post',
    params
  })
}

/**
 * 校验找回密码验证码
 * @param uuid
 * @param sms_code
 */
export function validFindPasswordSms(uuid, sms_code) {
  return request.ajax({
    url: `/passport/find-pwd/valid`,
    method: 'get',
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
  return request.ajax({
    url: `/passport/find-pwd/update-password`,
    method: 'put',
    params: {
      uuid,
      password: md5(password)
    }
  })
}

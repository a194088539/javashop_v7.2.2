import request from '../utils/request'
import { md5 } from '../lib/md5'


/**
 * 账户密码登录绑定
 * @param uuid
 */
export function loginBindConnectByAccount(uuid, params) {
  params.password = md5(params.password)
  // 小程序登录验证(后台开启滑动验证时需要的参数)
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/login-binder/wap/${uuid}`,
    method: 'post',
    data: params
  })
}

/**
 * 注册绑定
 * @param uuid
 * @param params
 */
export function registerBindConnect(uuid,params) {
  const _params = { ...params }
  _params.password = md5(_params.password)
  _params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/mini-program/register-bind/${uuid}`,
    method: 'post',
    params:_params
  })
}

/**
 * 微信小程序自动登录
 */
export function loginByAuto(params) {
  return request.ajax({
    url: '/passport/mini-program/auto-login',
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 加密数据解密验证
 * @param params
 */
export function accessUnionID(params) {
  return request.ajax({
    url: '/passport/mini-program/decrypt',
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 发送第三方登录手机验证码
 * @param mobile
 * @param captcha
 * @param uuid
 */
export function sendMobileLoginSms(mobile, captcha, uuid, scene, mini_client = 'MINI') {
  return request.ajax({
    url: `/passport/mobile-binder/sms-code/${mobile}`,
    method: 'post',
    params: {
      captcha,
      uuid,
      scene,
      mini_client
    }
  })
}

/**
 * 第三方登录绑定【通过手机验证码方式登录】
 * @param uuid
 * @param params
 */
export function loginByMobileConnect(uuid, params) {
  params.mini_client = 'MINI'
  return request.ajax({
    url: `/passport/mobile-binder/${uuid}`,
    method: 'post',
    params
  })
}

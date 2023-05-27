/**
 * 公共API
 */
import request from '../utils/request'
import { api } from '../config/config'


/**
 * 获取图片验证码URL
 * @param uuid
 * @param type
 * @returns {string}
 */
export function getValidateCodeUrl(uuid, type) {
  if (!uuid || !type) return ''
  return `${api.base}/captchas/${uuid}/${type}?r=${new Date().getTime()}`
}

/**
 * 获取站点设置
 */
export function getSiteData() {
  return request.ajax({
    url: `/base/site-show`,
    method: 'get'
  })
}
/**
 * 记录商品浏览量【用于统计】
 */
export function recordView(url) {
  return request.ajax({
    url: '/view',
    method: 'get',
    params:{
      url,
      uuid: wx.getStorageSync('uuid')
    }
  })
}
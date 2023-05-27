/**
 * 验证平台相关API
 */

import request from '@/utils/request'

/**
 * 获取验证平台列表
 * @param params
 */
export function getValidatorPlatformList(params) {
  return request({
    url: 'admin/systems/validator',
    method: 'get',
    loading: false,
    params
  })
}

/**
 * 修改验证平台参数
 * @param pluginId
 * @param params
 */
export function editValidatorPlatform(pluginId, params) {
  return request({
    url: `admin/systems/validator/${pluginId}`,
    method: 'put',
    headers: { 'Content-Type': 'application/json' },
    data: params
  })
}

/**
 * 开启验证平台
 * @param pluginId
 */
export function openValidatorPlatformById(pluginId) {
  return request({
    url: `admin/systems/validator/${pluginId}/open`,
    method: 'put'
  })
}

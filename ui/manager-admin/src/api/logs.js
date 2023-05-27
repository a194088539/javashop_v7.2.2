/**
 * 日志分析API
 */

import request from '@/utils/request'

/**
 * 获取服务名列表
 * @param params 参数
 */
export function getLogServices() {
  return request({
    url: `admin/services`,
    method: 'get',
    loading: false
  })
}

/**
 * 获取位于列表
 * @param params 参数
 */
export function getInstances(appName) {
  return request({
    url: `admin/services/${appName}/instances`,
    method: 'get',
    loading: false
  })
}

/**
 * 获取日志详情
 * @param params 参数
 */
export function getLogDetails(appName, uuid, params) {
  return request({
    url: `/admin/services/${appName}/instances/${uuid}/logs`,
    method: 'get',
    params,
    loading: false
  })
}

/**
 * 下载日志详情
 * @param params 参数
 */
export function getDownloaderLogDetails(appName, uuid, params) {
  return request({
    url: `/admin/services/${appName}/instances/${uuid}/logs/downloader`,
    method: 'get',
    params
  })
}

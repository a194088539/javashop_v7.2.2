/**
 * 直播管理相关API
 */

import { api } from '~/ui-domain'
import request from '@/utils/request'

/**
 * 获取直播间列表
 * @param params
 */
export function getLiveVideoRoomList(params) {
  return request({
    url: `${api.zhibo}/admin/live-video/room`,
    method: 'get',
    loading: false,
    params
  })
}

/**
 * 获取直播间商品信息
 * @param params
 */
export function getLiveVideoRoomGoods(roomId, params) {
  return request({
    url: `${api.zhibo}/admin/live-video/room/${roomId}`,
    method: 'get',
    loading: false,
    params
  })
}

/**
 * 获取直播全部商品列表
 * @param params
 */
export function getLiveVideoGoodsList(params) {
  return request({
    url: `${api.zhibo}/admin/live-video/goods`,
    method: 'get',
    loading: false,
    params
  })
}

/**
 * 删除直播商品
 * @param params
 */
export function deleteLiveVideoGoods(id) {
  return request({
    url: `${api.zhibo}/admin/live-video/goods/${id}`,
    method: 'delete',
    loading: false
  })
}

/**
 * 撤回审核
 * @param params
 */
export function resetuditLiveVideoGoods(id) {
  return request({
    url: `${api.zhibo}/admin/live-video/goods/${id}/reset-audit`,
    method: 'post',
    loading: false
  })
}

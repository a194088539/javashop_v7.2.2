import request from '../utils/request'

/**
 * 获取直播间列表
 * @param params
 */
export function getLiveVideoRoomList(params) {
  return request.ajax({
    url: `/zhibo/live-video/room`,
    method: 'get',
    params
  })
}

/**
 * 获取当前直播间商品
 * @param room_id
 */
export function getLiveVideoRoomGoodsList(room_id, params) {
  return request.ajax({
    url: `/zhibo/live-video/room/${room_id}/get-goods-list`,
    method: 'get',
    params
  })
}


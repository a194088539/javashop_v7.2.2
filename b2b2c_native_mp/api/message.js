/**
 * 站内消息相关API
 */

import request from '../utils/request'

/**
 * 获取消息列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getMessages(params) {
  return request.ajax({
    url: '/members/member-nocice-logs',
    method: 'get', 
    loading:true,
    params
  })
}

/**
 * 标记消息为已读
 * @param ids
 */
export function messageMarkAsRead(ids) {
  return request.ajax({
    url: `/members/member-nocice-logs/${ids}/read`,
    method: 'put'
  })
}

/**
 * 删除消息
 * @param ids
 */
export function deleteMessage(ids) {
  return request.ajax({
    url: `/members/member-nocice-logs/${ids}`,
    method: 'delete'
  })
}


/**
 * 获取未读消息数量信息
 */
export function getNoReadMessageNum() {
  return request.ajax({
    url: `/members/member-nocice-logs/number`,
    method: 'get'
  })
}

/**
 * 获取问答消息列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getAskMessages(params) {
  return request.ajax({
    url: '/members/asks/message',
    method: 'get',
    params
  })
}

/**
 * 标记问答消息为已读
 * @param ids
 */
export function setAskMessageRead(ids) {
  return request.ajax({
    url: `/members/asks/message/${ids}/read`,
    method: 'put'
  })
}

/**
 * 删除问答消息
 * @param ids
 */
export function deleteAskMessage(ids) {
  return request.ajax({
    url: `/members/asks/message/${ids}`,
    method: 'delete'
  })
}

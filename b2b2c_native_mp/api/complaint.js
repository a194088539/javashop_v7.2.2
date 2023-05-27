/**
 * 交易投诉相关API
 */

import request from '../utils/request'

/**
 * 获取投诉主题列表
 */
export function getComplaintTheme() {
  return request.ajax({
    url:'/trade/order-complains/topics',
    method: 'get',
    needToken: true
  })
}

/**
 * 提交交易投诉
 */
export function appendComplaint(params) {
  return request.ajax({
    url:'/trade/order-complains',
    method: 'post',
    needToken: true,
    data: params
  })
}

/**
 * 获取交易投诉列表
 * @param params
 */
export function getComplaintList(params) {
  return request.ajax({
    url: '/trade/order-complains',
    method: 'get',
    needToken: true,
    params
  })
}

/**
 * 撤销交易投诉
 * @param id
 */
export function cancelComplaint(id) {
  return request.ajax({
    url: `/trade/order-complains/${id}`,
    method: 'put',
    needToken: true,
  })
}

/**
 * 获取交易投诉详情
 * @param id
 */
export function getComplaintDetail(id) {
  return request.ajax({
    url: `/trade/order-complains/${id}`,
    method: 'get',
    needToken: true,
  })
}

/**
 * 获取交易投诉详情
 * @param id
 */
export function getComplaintFlow(id) {
  return request.ajax({
    url: `/trade/order-complains/${id}/flow`,
    method: 'get',
    needToken: true,
  })
}

/**
 * 发起对话
 * @param id
 * @param params
 */
export function initiationSession(id, params) {
  return request.ajax({
    url: `/trade/order-complains/${id}/communication`,
    method: 'put',
    needToken: true,
    params
  })
}

/**
 * 提交仲裁
 * @param id
 */
export function arbitrationProcess(id) {
  return request.ajax({
    url: `/trade/order-complains/${id}/to-arbitration`,
    method: 'put',
    needToken: true,
  })
}

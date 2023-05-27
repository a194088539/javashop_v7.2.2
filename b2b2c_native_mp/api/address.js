/**
 * 收货地址相关API
 */

import request from '../utils/request'
import { api } from '../config/config'

/**
 * 获取收货地址列表
 * @returns {AxiosPromise}
 */
export function getAddressList() {
  return request.ajax({
    url: '/members/addresses',
    method: 'get',
    loading: true
  })
}

/**
 * 添加收货地址
 * @param params 地址参数
 * @returns {AxiosPromise}
 */
export function addAddress(params) {
  return request.ajax({
    url: '/members/address',
    method: 'post',
    loading: true,
    params
  })
}

/**
 * 编辑地址
 * @param id 地址ID
 * @param params 地址参数
 * @returns {AxiosPromise}
 */
export function editAddress(id, params) {
  return request.ajax({
    url: `/members/address/${id}`,
    method: 'put',
    loading: true,
    params
  })
}

/**
 * 删除收货地址
 * @param id
 */
export function deleteAddress(id) {
  return request.ajax({
    url: `/members/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param id
 */
export function setDefaultAddress(id) {
  return request.ajax({
    url: `/members/address/${id}/default`,
    method: 'put'
  })
}

/**
 * 获取某个地址详情
 * @param id
 */
export function getAddressDetail(id) {
  return request.ajax({
    url: `/members/address/${id}`,
    method: 'get',
    loading: true
  })
}

/**
 * 获取地区列表
 * @param id
 */
export function getAreas(id) {
  const _api = `/base/regions/@id/children`
  return request.ajax({
    url: _api.replace('@id', id),
    method: 'get'
  })
}


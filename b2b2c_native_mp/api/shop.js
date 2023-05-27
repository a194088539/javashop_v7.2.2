/**
 * 店铺相关API
 */

import request from '../utils/request'

/**
 * 获取店铺列表
 * @param params
 */
export function getShopList(params) {
  return request.ajax({
    url: '/shops/list',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 会员初始化店铺信息
 */
export function initApplyShop() {
  return request.ajax({
    url: '/shops/apply',
    method: 'post'
  })
}

/**
 * 获取店铺信息
 */
export function getApplyShopInfo() {
  return request.ajax({
    url: '/shops/apply',
    method: 'get'
  })
}

/**
 * 会员申请开店步骤
 * @param step
 * @param params
 */
export function applyShopStep(step, params) {
  return request.ajax({
    url: `/shops/apply/step${step}`,
    method: 'put',
    params
  })
}

/**
 * 获取店铺基本信息
 * @param shop_id
 */
export function getShopBaseInfo(shop_id) {
  return request.ajax({
    url: `/shops/${shop_id}`,
    method: 'get'
  })
}

/**
 * 获取店铺幻灯片
 * @param shop_id
 */
export function getShopSildes(shop_id) {
  return request.ajax({
    url: `/shops/sildes/${shop_id}`,
    method: 'get'
  })
}

/**
 * 获取店铺导航
 * @param shop_id
 */
export function getShopNav(shop_id) {
  return request.ajax({
    url: `/shops/navigations/${shop_id}`,
    method: 'get'
  })
}

/**
 * 获取店铺分类【分组】
 * @param shop_id
 */
export function getShopCategorys(shop_id) {
  return request.ajax({
    url: `/shops/cats/${shop_id}`,
    method: 'get'
  })
}

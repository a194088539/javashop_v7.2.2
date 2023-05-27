/**
 * Created by Andste on 2018/7/15.
 * 促销相关API
 */

import request from '../utils/request'

/**
 * 获取商品促销活动列表
 * @param goods_id
 */
export function getGoodsPromotions(goods_id) {
  return request.ajax({
    url: `/promotions/${goods_id}`,
    method: 'get',
    loading:true
  })
}

/**
 * 获取团购分类
 */
export function getGroupBuyCategorys() {
  return request.ajax({
    url: '/promotions/group-buy/cats',
    method: 'get'
  })
}

/**
 * 获取团购商品
 * @param params
 */
export function getGroupBuyGoods(params) {
  return request.ajax({
    url: `/promotions/group-buy/goods`,
    method: 'get',
    params
  })
}

/**
 * 获取团信息
 * @param gb_id
 */
export function getGroupBuyDetail(gb_id) {
  return request.ajax({
    url: `/promotions/group-buy/active`,
    method: 'get',
    params: { gb_id }
  })
}

/**
 * 获取全部优惠券
 * @param goods_id
 */
export function getOwnCoupons(goods_id) {
  return request.ajax({
    url: '/promotions/coupons/goods-use',
    method: 'get',
    params: { goods_id }
  })
}

/**
 * 获取店铺优惠券
 * @param seller_id
 */
export function getShopCoupons(seller_id) {
  return request.ajax({
    url: '/promotions/coupons',
    method: 'get',
    params: { seller_id }
  })
}

/**
 * 获取积分商城分类
 */
export function getPointsCategory() {
  return request.ajax({
    url: '/promotions/exchange/cats',
    method: 'get'
  })
}

/**
 * 获取积分商城商品
 * @param params
 */
export function getPointsGoods(params) {
  return request.ajax({
    url: `/promotions/exchange/goods`,
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取限时抢购时间线
 */
export function getSeckillTimeLine() {
  return request.ajax({
    url: '/promotions/seckill/time-line',
    method: 'get'
  })
}

/**
 * 获取限时抢购商品
 * @param params
 */
export function getSeckillTimeGoods(params) {
  return request.ajax({
    url: '/promotions/seckill/goods-list',
    method: 'get',
    params
  })
}

/**
 * 获取全部优惠券
 * @param params
 */
export function getAllCoupons(params) {
  return request.ajax({
    url: '/promotions/coupons/all',
    method: 'get',
    params
  })
}


/** 获取拼团列表 */
export function getAssembleList(params) {
  return request.ajax({
    url: '/pintuan/goods/skus',
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 获取拼团商品sku的详细
 * @param sku_id
 */
export function getAssembleDetail(sku_id) {
  return request.ajax({
    url: `/pintuan/goods/skus/${sku_id}`,
    method: 'get'
  })
}

/**
 * 获取此商品sku_id待成团的订单
 * @param goods_id
 */
export function getAssembleOrderList(goods_id, params) {
  return request.ajax({
    url: `/pintuan/goods/${goods_id}/orders`,
    method: 'get',
    params
  })
}

/***
 * 获取当前商品所有参与拼团的sku规格列表
 * @param goods_id
 */
export function getAssembleSkuList(goods_id, pintuan_id) {
  return request.ajax({
    url: `/pintuan/goods/${goods_id}/skus`,
    method: 'get',
    params: { pintuan_id }
  })
}

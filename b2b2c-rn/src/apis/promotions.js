/**
 * Created by Andste on 2018/7/15.
 * 促销相关API
 */

import request, {Method} from '../utils/request';

/**
 * 获取商品促销活动列表
 * @param goods_id
 */
export function getGoodsPromotions(goods_id) {
  return request({
    url: `promotions/${goods_id}`,
    method: Method.GET,
  });
}

/**
 * 获取团购分类
 */
export function getGroupBuyCategorys() {
  return request({
    url: 'promotions/group-buy/cats',
    method: Method.GET,
  });
}

/**
 * 获取团购商品
 * @param params
 */
export function getGroupBuyGoods(params) {
  return request({
    url: 'promotions/group-buy/goods',
    method: Method.GET,
    params,
  });
}

/**
 * 获取团信息
 * @param gb_id
 */
export function getGroupBuyDetail(gb_id) {
  return request({
    url: 'promotions/group-buy/active',
    method: Method.GET,
  });
}

/**
 * 获取店铺优惠券
 * @param seller_id
 */
export function getShopCoupons(seller_id) {
  return request({
    url: 'promotions/coupons',
    method: Method.GET,
    params: {seller_id},
  });
}

/**
 * 获取平台优惠券
 * @param seller_id
 */
export function getCoupons(goods_id) {
  return request({
    url: 'promotions/coupons/goods-use',
    method: Method.GET,
    params: {goods_id},
  });
}

/**
 * 获取积分商城分类
 */
export function getPointsCategory() {
  return request({
    url: 'promotions/exchange/cats',
    method: Method.GET,
  });
}

/**
 * 获取积分商城商品
 * @param params
 */
export function getPointsGoods(params) {
  return request({
    url: 'promotions/exchange/goods',
    method: Method.GET,
    params,
  });
}

/**
 * 获取限时抢购时间线
 */
export function getSeckillTimeLine() {
  return request({
    url: 'promotions/seckill/time-line',
    method: Method.GET,
  });
}

/**
 * 获取限时抢购商品
 * @param params
 */
export function getSeckillTimeGoods(params) {
  return request({
    url: 'promotions/seckill/goods-list',
    method: Method.GET,
    params,
  });
}

/**
 * 获取全部优惠券
 * @param params
 */
export function getAllCoupons(params) {
  return request({
    url: 'promotions/coupons/all',
    method: Method.GET,
    params,
  });
}

/**
 * 获取拼团商品
 * @param params
 * @returns {AxiosPromise<any>}
 */
export function getPinTuanGoods(params) {
  return request({
    url: 'pintuan/goods/skus',
    method: Method.GET,
    params,
  });
}

/**
 * 获取商品sku拼团列表
 * @param goodsId
 * @param pintuan_id
 * @returns {AxiosPromise<any>}
 */
export function getPinTuanSkus(goodsId, pintuan_id) {
  return request({
    url: `/pintuan/goods/${goodsId}/skus`,
    method: Method.GET,
    params: {
      pintuan_id,
    },
  });
}

/**
 * 获取商品sku拼团列表
 * @param skuId
 * @returns {AxiosPromise<any>}
 */
export function getPinTuanInfo(skuId) {
  return request({
    url: `/pintuan/goods/skus/${skuId}`,
    method: Method.GET,
  });
}

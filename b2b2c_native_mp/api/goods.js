/**
 * 商品相关API
 */
import request from '../utils/request'

/**
 * 获取商品详情
 * @param goods_id 商品ID
 * @returns {AxiosPromise}
 */
export function getGoods(goods_id) {
  return request.ajax({
    url: `/goods/${goods_id}`,
    method: 'get'
  })
}

/**
 * 获取商品列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getGoodsList(params) {
  return request.ajax({
    url: '/goods/search',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取商品选择器
 * @param params
 */
export function getGoodsSelector(params) {
  return request.ajax({
    url: '/goods/search/selector',
    method: 'get',
    params
  })
}

/**
 * 获取商品关键字对应商品数量
 * @param keyword
 */
export function getKeywordNum(keyword) {
  return request.ajax({
    url: '/goods/search/words',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 记录商品浏览次数
 * @param goods_id
 */
export function visitGoods(goods_id) {
  return request.ajax({
    url: `/goods/${goods_id}/visit`,
    method: 'get',
  })
}

/**
 * 获取商品sku列表
 * @param goods_id
 */
export function getGoodsSkus(goods_id) {
  return request.ajax({
    url: `/goods/${goods_id}/skus`,
    method: 'get',
  })
}

/**
 * 获取标签商品
 * @param seller_id 卖家id
 * @param mark      标签 hot：热卖 new：新品 recommend：推荐
 * @param num       获取个数
 */
export function getTagGoods(seller_id, mark = 'hot', num = 5) {
  return request.ajax({
    url: `/goods/tags/${mark}/goods`,
    method: 'get',
    params: {
      seller_id,
      mark,
      num
    }
  })
}

/**
 * 获取商品分类
 * @param parent_id
 */
export function getCategory(parent_id = 0) {
  return request.ajax({
    url: `/goods/categories/${parent_id}/children`,
    method: 'get',
    loading: true
  })
}

/**
 * 查看商品是否在配送区域 1 有货 0 无货
 * @param goods_id
 * @param area_id
 */
export function getGoodsShip(goods_id, area_id) {
  return request.ajax({
    url: `/goods/${goods_id}/area/${area_id}`,
    method: 'get',
  })
}


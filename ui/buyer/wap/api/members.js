/**
 * Created by Andste on 2018/6/8.
 */

import request, { Method } from '@/utils/request'

/**
 * 获取优惠券列表
 * @param params
 */
export function getCoupons(params) {
  return request({
    url: 'members/coupon',
    method: Method.GET,
    needToken: true,
    loading: false,
    params
  })
}

/**
 * 领取优惠券
 * @param coupon_id
 */
export function receiveCoupons(coupon_id) {
  return request({
    url: `members/coupon/${coupon_id}/receive`,
    method: Method.POST,
    needToken: true
  })
}

/**
 * 获取当前会员积分
 * @returns {*}
 */

export function getPoints() {
  return request({
    url: 'members/points/current',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 获取积分明细数据
 * @param params
 * @returns {AxiosPromise}
 */
export function getPointsData(params) {
  return request({
    url: 'members/points',
    method: Method.GET,
    needToken: true,
    loading: false,
    params
  })
}

/**
 * 获取我的评论列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getComments(params) {
  return request({
    url: 'members/comments',
    method: Method.GET,
    needToken: true,
    loading: false,
    params
  })
}

/**
 * 订单评论
 * @param params
 */
export function commentsOrder(params) {
  return request({
    url: 'members/comments',
    method: Method.POST,
    needToken: true,
    headers: { 'Content-Type': 'application/json' },
    data: params
  })
}

/**
 * 获取商品收藏
 * @param params
 * @returns {AxiosPromise}
 */
export function getGoodsCollection(params) {
  return request({
    url: 'members/collection/goods',
    method: Method.GET,
    needToken: true,
    loading: false,
    message: false,
    params
  })
}

/**
 * 收藏商品
 * @param goods_id 商品ID
 * @returns {AxiosPromise}
 */
export function collectionGoods(goods_id) {
  return request({
    url: 'members/collection/goods',
    method: Method.POST,
    needToken: true,
    data: { goods_id }
  })
}

/**
 * 删除商品收藏
 * @param ids 收藏ID【集合或单个商品ID】
 * @returns {AxiosPromise}
 */
export function deleteGoodsCollection(ids) {
  if (Array.isArray(ids)) ids = ids.join(',')
  return request({
    url: `members/collection/goods/${ids}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 获取商品是否被收藏
 * @param good_id
 */
export function getGoodsIsCollect(good_id) {
  return request({
    url: `members/collection/goods/${good_id}`,
    method: Method.GET,
    needToken: true,
    loading: false
  })
}

/**
 * 获取店铺收藏
 * @param params
 * @returns {AxiosPromise}
 */
export function getShopCollection(params) {
  return request({
    url: 'members/collection/shops',
    method: Method.GET,
    needToken: true,
    loading: false,
    params
  })
}

/**
 * 收藏店铺
 * @param shop_id 店铺ID
 * @returns {AxiosPromise}
 */
export function collectionShop(shop_id) {
  return request({
    url: 'members/collection/shop',
    method: Method.POST,
    needToken: true,
    data: { shop_id }
  })
}

/**
 * 删除店铺收藏
 * @param id
 */
export function deleteShopCollection(id) {
  return request({
    url: `members/collection/shop/${id}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 获取店铺是否已被收藏
 * @param shop_id
 */
export function getShopIsCollect(shop_id) {
  return request({
    url: `members/collection/shop/${shop_id}`,
    method: Method.GET,
    needToken: true
  })
}

/**
 * 获取当前登录的用户信息
 * @returns {AxiosPromise}
 */
export function getUserInfo() {
  return request({
    url: `members`,
    method: Method.GET,
    loading: false,
    needToken: true
  })
}

/**
 * 保存用户信息
 * @param params
 * @returns {AxiosPromise}
 */
export function saveUserInfo(params) {
  return request({
    url: 'members',
    method: Method.PUT,
    needToken: true,
    data: params
  })
}

/**
 * 登出
 * @param type
 * @returns {AxiosPromise}
 */
export function logout(type) {
  return request({
    url: type === 'WECHAT' ? 'account-binder/unbind/out' : 'members/logout',
    method: Method.POST,
    needToken: true
  })
}

/**
 * 获取发票列表
 */
export function getReceipts(type) {
  return request({
    url: `members/receipt/${type}`,
    method: Method.GET,
    needToken: true,
    loading: false
  })
}

/**
 * 添加发票
 * @param params
 */
export function addReceipt(params) {
  return request({
    url: 'members/receipt',
    method: Method.POST,
    needToken: true,
    params
  })
}

/**
 * 修改发票
 * @param id
 * @param params
 */
export function editReceipt(id, params) {
  return request({
    url: `members/receipt/${id}`,
    method: Method.PUT,
    needToken: true,
    params
  })
}

/**
 * 删除发票
 * @param id
 */
export function deleteReceipt(id) {
  return request({
    url: `members/receipt/${id}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 设置发票为默认
 * @param id
 */
export function setDefaultReceipt(id) {
  return request({
    url: `members/receipt/${id}/default`,
    method: Method.PUT,
    needToken: true
  })
}

/**
 * 获取店铺是否开启某种发票
 * @param params
 */
export function queryMembersReceipt(ids) {
  return request({
    url: `/shops/${ids}/check/receipt`,
    method: Method.GET,
    loading: false,
    needToken: true
  })
}

/**
 * 获取会员开票历史记录信息列表
 * @param params
 */
export function queryReceiptInfoList(params) {
  return request({
    url: 'members/receipt/history',
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 获取会员开票历史记录信息详情
 * @param params
 */
export function queryReceiptInfoDetail(history_id) {
  return request({
    url: `members/receipt/history/${history_id}`,
    method: Method.GET,
    needToken: true
  })
}

/**
 * 获取统计数量
 * 包括但不限于【订单数量、收藏的商品数量、收藏的店铺数量】
 */
export function getStatisticsNum() {
  return request({
    url: 'members/statistics',
    method: Method.GET,
    loading: false,
    needToken: true
  })
}

/**
 * 获取第三方绑定列表
 */
export function getAccountBinder() {
  return request({
    url: 'members/account-binder',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 发起账号绑定
 * @param type
 */
export function bindAccount(type) {
  return request({
    url: `members/account-binder/pc/${type}`,
    method: Method.GET
  })
}

/**
 * 发起账号解绑
 * @param type
 */
export function unbundAccount(type) {
  return request({
    url: `members/account-binder/pc/${type}`,
    method: Method.POST
  })
}

/**
 * 登录绑定
 * @param uuid
 */
export function loginBindAccount(uuid) {
  return request({
    url: `members/account-binder/login/${uuid}`,
    method: Method.POST
  })
}

/**
 * 注册绑定
 * @param uuid
 */
export function registerBindAccount(uuid) {
  return request({
    url: `members/account-binder/register/${uuid}`,
    method: Method.POST
  })
}

/**
 * 获取商家可用优惠券列表
 * @param seller_ids
 */
export function getShopsCoupons(seller_ids) {
  return request({
    url: `members/coupon/${seller_ids}`,
    method: Method.GET,
    loading: false,
    needToken: true
  })
}

/**
 * 获取商品评论列表
 * @param goods_id
 * @param params
 */
export function getGoodsComments(goods_id, params) {
  return request({
    url: `members/comments/goods/${goods_id}`,
    method: Method.GET,
    loading: false,
    params
  })
}

/**
 * 获取我的足迹列表
 */
export function queryHistoryList(params) {
  return request({
    url: '/members/history/list',
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 清空所有会员足迹
 */
export function clearHistoryList() {
  return request({
    url: '/members/history',
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 根据id删除会员足迹
 * @param id
 */
export function deleteHistoryListId(id) {
  return request({
    url: `/members/history/${id}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 获取待追评列表
 * @param goods_id
 * @param params
 *
 */
export function getWaitAppendComments(params) {
  return request({
    url: `members/comments/list`,
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 获取商品初评信息
 * @param goods_id
 * @param params
 */
export function getGoodsFirstComments(params) {
  return request({
    url: `members/comments/detail`,
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 追加评论
 * @param params
 */
export function AppendCommentsOrder(params) {
  return request({
    url: 'members/comments/additional',
    method: Method.POST,
    needToken: true,
    headers: { 'Content-Type': 'application/json' },
    data: params
  })
}

/**
 * 获取评价详情
 * @param goods_id
 * @param params
 */
export function getCommentsDetail(comment_id) {
  return request({
    url: `/members/comments/${comment_id}`,
    method: Method.GET,
    needToken: true
  })
}

/**
 * 会员增票资质申请
 * @param params
 */
export function ticketInformationApply(params) {
  return request({
    url: 'members/zpzz',
    method: Method.POST,
    data: params,
    needToken: true
  })
}

/**
 * 获取增票资质信息详情
 * @param params
 */
export function queryInvoiceInfo() {
  return request({
    url: 'members/zpzz/detail',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 删除会员增票资质信息
 */
export function deleteInvoiceInfo(id) {
  return request({
    url: `members/zpzz/${id}`,
    method: Method.DELETE,
    needToken: true,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 会员修改增票资质申请
 * @param params
 */
export function changeTicketInformation(params) {
  return request({
    url: `members/zpzz/${params.id}`,
    method: Method.PUT,
    data: params,
    needToken: true
  })
}

/**
 * 增加增票资质收票人地址
 * @param params
 */
export function ticketAdressApply(params) {
  return request({
    url: 'members/receipt/address',
    method: Method.POST,
    data: params,
    needToken: true
  })
}

/**
 * 获取增票资质收票人地址信息
 * @param params
 */
export function queryTicketAdress() {
  return request({
    url: 'members/receipt/address/detail',
    method: Method.GET,
    needToken: true
  })
}

/**
 * 修改增票资质收票人地址
 * @param params
 */
export function changeTicketAdress(params, id) {
  return request({
    url: `members/receipt/address/${id}`,
    method: Method.PUT,
    data: params,
    needToken: true
  })
}

/**
 * 获取我的咨询列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getConsultations(params) {
  return request({
    url: 'members/asks',
    method: Method.GET,
    needToken: true,
    loading: false,
    params
  })
}

/**
 * 商品咨询
 * @param goods_id
 * @param ask_content
 */
export function consultating(goods_id, ask_content, anonymous) {
  return request({
    url: 'members/asks',
    method: Method.POST,
    needToken: true,
    data: {
      goods_id,
      ask_content,
      anonymous
    }
  })
}

/**
 * 获取商品咨询列表
 * @param goods_id
 * @param params
 */
export function getGoodsConsultations(goods_id, params) {
  return request({
    url: `members/asks/goods/${goods_id}`,
    method: Method.GET,
    loading: false,
    params
  })
}

/**
 * 获取咨询详情
 * @param ask_id
 */
export function getAskDetail(ask_id) {
  return request({
    url: `/members/asks/detail/${ask_id}`,
    method: Method.GET
  })
}

/**
 * 获取我的回答
 * @param ask_id
 * @returns {AxiosPromise}
 */
export function deleteAsk(ask_id) {
  return request({
    url: `/members/asks/${ask_id}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 获取商品咨询回复信息
 * @param ask_id
 * @param params
 */
export function getGoodsAskReplys(ask_id, params) {
  return request({
    url: `members/asks/reply/list/${ask_id}`,
    method: Method.GET,
    loading: false,
    params
  })
}

/**
 * 获取我的回答
 * @param params
 * @returns {AxiosPromise}
 */
export function getAnswers(params) {
  return request({
    url: '/members/asks/reply/list/member',
    method: Method.GET,
    needToken: true,
    params
  })
}

/**
 * 删除我的回答
 * @param id
 * @returns {AxiosPromise}
 */
export function deleteAnswers(id) {
  return request({
    url: `/members/asks/reply/${id}`,
    method: Method.DELETE,
    needToken: true
  })
}

/**
 * 回复商品咨询
 * @param ask_id
 * @param reply_content
 * @param anonymous
 */
export function replyAsk(ask_id, reply_content, anonymous) {
  return request({
    url: `/members/asks/reply/${ask_id}`,
    method: Method.POST,
    needToken: true,
    data: {
      ask_id,
      reply_content,
      anonymous
    }
  })
}

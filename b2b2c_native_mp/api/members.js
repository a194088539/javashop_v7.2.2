/**
 * Created by Andste on 2018/6/8.
 */
import request from '../utils/request'

/**
 * 获取优惠券列表
 * @param params
 */
export function getCoupons(params) {
  return request.ajax({
    url: '/members/coupon',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 领取优惠券
 * @param coupon_id
 */
export function receiveCoupons(coupon_id) {
  return request.ajax({
    url: `/members/coupon/${coupon_id}/receive`,
    method: 'post'
  })
}

/**
 * 获取当前会员积分
 * @returns {*}
 */

export function getPoints() {
  return request.ajax({
    url: '/members/points/current',
    method: 'get'
  })
}

/**
 * 获取积分明细数据
 * @param params
 * @returns {AxiosPromise}
 */
export function getPointsData(params) {
  return request.ajax({
    url: '/members/points',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取我的评论列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getComments(params) {
  return request.ajax({
    url: '/members/comments',
    method: 'get',
    params
  })
}

/**
 * 订单评论
 * @param params
 */
export function commentsOrder(params) {
  return request.ajax({
    url: '/members/comments',
    method: 'post',
    isJson: true,
    params
  })
}

/**
 * 获取商品收藏
 * @param params
 * @returns {AxiosPromise}
 */
export function getGoodsCollection(params) {
  return request.ajax({
    url: '/members/collection/goods',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 收藏商品
 * @param goods_id 商品ID
 * @returns {AxiosPromise}
 */
export function collectionGoods(goods_id) {
  return request.ajax({
    url: '/members/collection/goods',
    method: 'post',
    params: { goods_id }
  })
}

/**
 * 删除商品收藏
 * @param ids 收藏ID【集合或单个商品ID】
 * @returns {AxiosPromise}
 */
export function deleteGoodsCollection(ids) {
  if (Array.isArray(ids)) ids = ids.join(',')
  return request.ajax({
    url: `/members/collection/goods/${ids}`,
    method: 'delete'
  })
}

/**
 * 获取商品是否被收藏
 * @param good_id
 */
export function getGoodsIsCollect(good_id) {
  return request.ajax({
    url: `/members/collection/goods/${good_id}`,
    method: 'get'
  })
}

/**
 * 获取店铺收藏
 * @param params
 * @returns {AxiosPromise}
 */
export function getShopCollection(params) {
  return request.ajax({
    url: '/members/collection/shops',
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 收藏店铺
 * @param shop_id 店铺ID
 * @returns {AxiosPromise}
 */
export function collectionShop(shop_id) {
  return request.ajax({
    url: '/members/collection/shop',
    method: 'post',
    params: { shop_id }
  })
}

/**
 * 删除店铺收藏
 * @param id
 */
export function deleteShopCollection(id) {
  return request.ajax({
    url: `/members/collection/shop/${id}`,
    method: 'delete'
  })
}

/**
 * 获取店铺是否已被收藏
 * @param shop_id
 */
export function getShopIsCollect(shop_id) {
  return request.ajax({
    url: `/members/collection/shop/${shop_id}`,
    method: 'get'
  })
}

/**
 * 获取当前登录的用户信息
 * @returns {AxiosPromise}
 */
export function getUserInfo() {
  return request.ajax({
    url: '/members',
    method: 'get'
  })
}

/**
 * 保存用户信息
 * @param params
 * @returns {AxiosPromise}
 */
export function saveUserInfo(params) {
  return request.ajax({
    url: '/members',
    method: 'put',
    loading:true,
    params
  })
}

/**
 * 登出 -- 执行解绑操作
 * @returns {AxiosPromise}
 */
export function logout() {
  return request.ajax({
    url: '/account-binder/unbind/out',
    method: 'post'
  })
}

/**
 * 获取发票列表 参数 发票类型默认普通发票 存在电子发票- ELECTRO
 * @param type
 */
export function getReceipts(type = 'VATORDINARY') {
  return request.ajax({
    url: `/members/receipt/${type}`,
    method: 'get'
  })
}

/**
 * 添加发票
 * @param params
 */
export function addReceipt(params) {
  return request.ajax({
    url: '/members/receipt',
    method: 'post',
    params
  })
}

/**
 * 修改发票
 * @param id
 * @param params
 */
export function editReceipt(id, params) {
  return request.ajax({
    url: `/members/receipt/${id}`,
    method: 'put',
    params
  })
}

/**
 * 删除发票
 * @param id
 */
export function deleteReceipt(id) {
  return request.ajax({
    url: `/members/receipt/${id}`,
    method: 'delete'
  })
}

/**
 * 设置发票为默认
 * @param id
 */
export function setDefaultReceipt(id) {
  return request.ajax({
    url: `/members/receipt/${id}/default`,
    method: 'put'
  })
}

/**
 * 获取店铺是否开启某种发票
 * @param params
 */
export function queryMembersReceipt(ids) {
  return request.ajax({
    url: `/shops/${ids}/check/receipt`,
    method: 'get'
  })
}

/**
 * 获取会员开票历史记录信息列表
 * @param params
 */
export function queryReceiptInfoList(params) {
  return request.ajax({
    url: '/members/receipt/history',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取会员开票历史记录信息详情
 * @param params
 */
export function queryReceiptInfoDetail(history_id) {
  return request.ajax({
    url: `/members/receipt/history/${history_id}`,
    method: 'get'
  })
}

/**
 * 获取商家可用优惠券列表
 * @param seller_ids
 */
export function getShopsCoupons(seller_ids) {
  return request.ajax({
    url: `/members/coupon/${seller_ids}`,
    method: 'get'
  })
}


/**
 * 获取商品评论列表
 * @param goods_id
 * @param params
 */
export function getGoodsComments(goods_id, params) {
  return request.ajax({
    url: `/members/comments/goods/${goods_id}`,
    method: 'get',
    params
  })
}

/**
 * 获取我的足迹列表
 */
export function queryHistoryList(params) {
  return request.ajax({
    url: '/members/history/list',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 清空所有会员足迹
 */
export function clearHistoryList() {
  return request.ajax({
    url: '/members/history',
    method: 'delete'
  })
}

/**
 * 根据id删除会员足迹
 * @param id
 */
export function deleteHistoryListId(id) {
  return request.ajax({
    url: `/members/history/${id}`,
    method: 'delete'
  })
}
/**
 * 获取待追评列表
 * @param goods_id
 * @param params
 *
 */
export function getWaitAppendComments(params) {
  return request.ajax({
    url: '/members/comments/list',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 获取商品初评信息
 * @param goods_id
 * @param params
 */
export function getGoodsFirstComments(params) {
  return request.ajax({
    url: '/members/comments/detail',
    method:'get',
    params
  })
}

/**
 * 追加评论
 * @param params
 */
export function appendCommentsOrder(params) {
  return request.ajax({
    url: '/members/comments/additional',
    method: 'post',
    isJson:true,//判断传入的是否是json数据
    data: params
  })
}

/**
 * 获取评价详情
 * @param goods_id
 * @param params
 */
export function getCommentsDetail(comment_id) {
  return request.ajax({
    url: `/members/comments/${comment_id}`,
    method: 'get'
  })
}


/**
 * 获取增票资质信息详情
 * @param params
 */
export function queryInvoiceInfo() {
  return request.ajax({
    url: '/members/zpzz/detail',
    method:'get'
  })
}

/**
 * 会员增票资质申请
 * @param params
 */
export function ticketInformationApply(params) {
  return request.ajax({
    url: '/members/zpzz',
    method: 'post',
    data: params
  })
}
/**
 * 会员修改增票资质申请
 * @param params
 */
export function changeTicketInformation(params) {
  return request.ajax({
    url: `/members/zpzz/${params.id}`,
    method: 'put',
    data: params
  })
}

/**
 * 删除会员增票资质信息
 */
export function deleteInvoiceInfo(id) {
  return request.ajax({
    url: `/members/zpzz/${id}`,
    method: 'delete',
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 增加增票资质收票人地址
 * @param params
 */
export function ticketAdressApply(params) {
  return request.ajax({
    url: '/members/receipt/address',
    method: 'post',
    data: params
  })
}

/**
 * 获取增票资质收票人地址信息
 * @param params
 */
export function queryTicketAdress() {
  return request.ajax({
    url: '/members/receipt/address/detail',
    method: 'get'
  })
}

/**
 * 修改增票资质收票人地址
 * @param params
 */
export function changeTicketAdress(params, id) {
  return request.ajax({
    url: `/members/receipt/address/${id}`,
    method: 'put',
    data: params
  })
}

/**
 * 获取我的咨询列表
 * @param params
 * @returns {AxiosPromise}
 */
export function getConsultations(params) {
  return request.ajax({
    url: '/members/asks',
    method: 'get',
    loading:true,
    params
  })
}

/**
 * 商品咨询
 * @param goods_id
 * @param ask_content
 */
export function consultating(goods_id, ask_content, anonymous) {
  return request.ajax({
    url: '/members/asks',
    method: 'post',
    data: { goods_id, ask_content, anonymous }
  })
}

/**
 * 获取商品咨询列表
 * @param goods_id
 * @param params
 */
export function getGoodsConsultations(goods_id, params) {
  return request.ajax({
    url: `/members/asks/goods/${goods_id}`,
    method: 'get',
    loading: true,
    params
  })
}

/**
 * 获取资询详情
 * @param ask_id
 */
export function getAskDetail(ask_id){
  return request.ajax({
    url:`/members/asks/detail/${ask_id}`,
    method:'get'
  })
}

/**
 * 获取商品资询回复信息
 * @param ask_id
 * @param params
 */
export function getGoodsAskReplys(ask_id,params){
  return request.ajax({
    url:`/members/asks/reply/list/${ask_id}`,
    method:'get',
    params
  })
}

/**
 * 删除我的提问
 * @param ask_id
 * @returns {AxiosPromise}
 */
export function deleteAsk(ask_id) {
  return request.ajax({
    url: `/members/asks/${ask_id}`,
    method: 'delete'
  })
}

/**
 * 获取我的回答
 * @param params
 * returns {AxiosPromise}
 */
export function getAnswers(params){
  return request.ajax({
    url:'/members/asks/reply/list/member',
    method:'get',
    loading: true,
    params
  })
}

/**
 * 删除我的回答
 * @param id
 * @returns {AxiosPromise}
 */
export function deleteAnswers(id) {
  return request.ajax({
    url: `/members/asks/reply/${id}`,
    method: 'delete'
  })
}

/**
 * 回复商品咨询
 * @param ask_id
 * @param reply_content
 * @param anonymous
 */
export function replyAsk(ask_id, reply_content, anonymous) {
  return request.ajax({
    url: `/members/asks/reply/${ask_id}`,
    method: 'post',
    data: {
      ask_id,
      reply_content,
      anonymous
    }
  })
}

/**
 * 足迹相关API
 */

import request, {Method} from '../utils/request';

/**
 * 获取我的足迹列表
 * @returns {AxiosPromise}
 */
export function getHistoryList(params) {
  return request({
    url: 'members/history/list',
    method: Method.GET,
    needToken: true,
    params,
  });
}

/**
 * 根据足迹id删除会员足迹
 * @returns {AxiosPromise}
 */
export function delHistoryGoods(goods_id) {
  return request({
    url: `members/history/${goods_id}`,
    method: Method.DELETE,
    needToken: true,
  });
}

/**
 * 清空会员所有足迹
 * @returns {AxiosPromise}
 */
export function delAll() {
  return request({
    url: `members/history/`,
    method: Method.DELETE,
    needToken: true,
  });
}

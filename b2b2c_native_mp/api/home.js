import request from '../utils/request'

/**
 * 获取焦点图
 * @param client_type
 */
export function getFocusPictures(client_type = 'PC') {
  return request.ajax({
    url: '/focus-pictures',
    params: { client_type },
    method: 'get'
  })
}

/**
 * 获取导航列表
 */
export function getSiteMenu(client_type = 'PC') {
  return request.ajax({
    url: '/pages/site-navigations',
    method: 'get',
    params: { client_type }
  })
}

/**
 * 获取首页商品分类
 * @param parent_id
 */
export function getCategory(parent_id = 0) {
  return request.ajax({
    url: `/goods/categories/${parent_id}/children`,
    method: 'get'
  })
}

/**
 * 获取热门关键词
 * @param num
 */
export function getHotKeywords(num = 7) {
  return request.ajax({
    url: '/pages/hot-keywords',
    method: 'get',
    params: { num }
  })
}

/**
 * 获取楼层数据
 * @param client_type
 * @param page_type
 */
export function getFloorData(client_type = 'PC', page_type = 'INDEX') {
  return request.ajax({
    url: `/pages/${client_type}/${page_type}`,
    method: 'get'
  })
}

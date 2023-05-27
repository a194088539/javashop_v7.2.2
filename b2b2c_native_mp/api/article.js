/**
 * Created by Andste on 2018/7/3.
 * 文章相关API
 */

import request from '../utils/request'
import { api } from '../config/config'

/**
 * 获取某个分类的文章列表
 * @param category_type
 */
export function getArticleCategory(category_type) {
  return request.ajax({
    url: `/base/pages/article-categories`,
    method: 'get',
    params: { category_type }
  })
}

/**
 * 获取文章详情
 * @param id
 */
export function getArticleDetail(id) {
  return request.ajax({
    url: `/base/pages/articles/${id}`,
    method: 'get'
  })
}

/**
 * 获取某个位置的文章列表
 * @param position
 */
export function getArticlesByPosition(position) {
  return request.ajax({
    url: `/base/pages/${position}/articles`,
    method: 'get'
  })
}

/**
 * 获取某个位置的文章
 * @param position
 */
export function getArticleByPosition(position) {
  return request.ajax({
    url: `/base/pages/${position}/articles`,
    method: 'get', 
    loading: true 
  })
}

/**
 * 获取某个分类下的文章列表
 * @param category_type
 */
export function getArticlesByCategory(category_type) {
  return request.ajax({
    url: `/base/pages/article-categories/${category_type}/articles`,
    method: 'get'
  })
}

package com.enation.app.javashop.service.goods;


import java.util.List;

/**
 * 静态页面商品管理
 * @author liushuai
 * @version v1.0
 * @since v7.0
 * 2018/7/17 下午3:19
 * @Description:
 *
 */
public interface StaticsPageGoodsManager {

	/**
	 * 商品总数
	 * @return
	 */
	Integer count();

	/**
	 * 商品数据获取
	 * @param page 每页
	 * @param pageSize 每页数量
	 * @return
	 */
	List goodsList(Long page,Long pageSize);


}
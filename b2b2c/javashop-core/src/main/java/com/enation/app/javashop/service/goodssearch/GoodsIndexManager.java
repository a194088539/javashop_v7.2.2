package com.enation.app.javashop.service.goodssearch;

import java.util.List;
import java.util.Map;

/**
 * 商品索引管理接口
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 14:11:46
 */
public interface GoodsIndexManager {
	
	/**
	 * 将某个商品加入索引<br>
	 * @param goods 商品数据
	 */
	void addIndex(Map goods);
	
	/**
	 * 更新某个商品的索引
	 * @param goods 商品数据
	 */
	void updateIndex(Map goods);

	
	/**
	 * 更新
	 * @param goods 商品数据
	 */
	void deleteIndex(Map goods);
	
	/**
	 * 初始化索引
	 * @param list 索引数据
	 * @param index 数量
	 * @return  是否是生成成功
	 */
	boolean addAll(List<Map<String, Object>> list, int index);


}

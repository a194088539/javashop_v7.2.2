package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 商品分析manager接口
 * @author xin
 * @version v1.0,2015-12-29
 * @since v1.0
 */
public interface GoodsFrontStatisticsManager {

	/**
	 * 获取商品详情
	 * @param pageNo 当前页码
	 * @param pageSize 每页数据量
	 * @param catId 商品分类id
	 * @param goodsName 商品名称
	 * @return WebPage 分页对象
	 */
	WebPage getGoodsDetail(Long pageNo, Long pageSize, Long catId, String goodsName);

	/**
	 * 获取商品价格数据，分页数据
	 * @param sections 区间List  格式：0 100 200
	 * @param searchCriteria 时间与店铺id相关参数
	 * @return SimpleChart简单图表数据
	 */
	SimpleChart getGoodsPriceSales(List<Integer> sections, SearchCriteria searchCriteria);

	/**
	 * 获取商品下单金额排行前30，分页数据
	 * @param searchCriteria 时间相关参数
	 * @param topNum top数
	 * @return WebPage 分页对象
	 */
	WebPage getGoodsOrderPriceTopPage(int topNum, SearchCriteria searchCriteria);
	
	/**
	 * 获取下单商品数量排行前30，分页数据
	 * @param searchCriteria 时间相关参数
	 * @param topNum 名次 默认为30
	 * @return	WebPage 分页对象
	 */
	WebPage getGoodsNumTopPage(int topNum, SearchCriteria searchCriteria);

    /**
     * 获取商品下单金额排行前30，图表数据
     * @param topNum top数
     * @param searchCriteria 时间相关参数
     * @return SimpleChart 简单图表数据
     */
	SimpleChart getGoodsOrderPriceTop(Integer topNum, SearchCriteria searchCriteria);

    /**
     * 获取商品下单数量排行前30，图表数据
     * @param topNum top数
     * @param searchCriteria 时间相关参数
     * @return SimpleChart 简单图表数据
     */
	SimpleChart getGoodsNumTop(Integer topNum, SearchCriteria searchCriteria);
}

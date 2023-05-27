package com.enation.app.javashop.service.goodssearch;

import com.enation.app.javashop.model.goodssearch.GoodsSearchDTO;
import com.enation.app.javashop.model.goodssearch.GoodsWords;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;
import java.util.Map;

/**
 * 商品搜索
 *
 * @author fk
 * @version v6.4
 * @since v6.4
 * 2017年9月14日 上午10:52:20
 */
public interface GoodsSearchManager {

    /**
     * 搜索
     *
     * @param goodsSearch 搜索条件
     * @return 商品分页
     */
    WebPage search(GoodsSearchDTO goodsSearch);

    /**
     * 获取筛选器
     *
     * @param goodsSearch 搜索条件
     * @return Map
     */
    Map<String, Object> getSelector(GoodsSearchDTO goodsSearch);

    /**
     * 通过关键字获取商品分词索引
     *
     * @param keyword 关键字
     * @return
     */
    List<GoodsWords> getGoodsWords(String keyword);


}

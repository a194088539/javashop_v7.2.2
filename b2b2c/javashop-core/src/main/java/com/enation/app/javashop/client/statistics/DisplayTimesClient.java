package com.enation.app.javashop.client.statistics;

import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import com.enation.app.javashop.model.statistics.dos.ShopPageView;

import java.util.List;

/**
 * 访问次数client
 *
 * @author fk
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2020/3/20 上午8:21
 */
public interface DisplayTimesClient {

    /**
     * 立即整理现有的数据
     */
    void countNow();

    /**
     * 将统计好的店铺数据 写入数据库
     *
     * @param list
     */
    void countShop(List<ShopPageView> list);

    /**
     * 将统计好的商品数据 写入数据库
     *
     * @param list
     */
    void countGoods(List<GoodsPageView> list);
}

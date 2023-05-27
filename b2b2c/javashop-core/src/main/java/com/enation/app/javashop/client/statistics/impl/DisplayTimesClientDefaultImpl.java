package com.enation.app.javashop.client.statistics.impl;

import com.enation.app.javashop.client.statistics.DisplayTimesClient;
import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import com.enation.app.javashop.model.statistics.dos.ShopPageView;
import com.enation.app.javashop.service.statistics.DisplayTimesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

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
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class DisplayTimesClientDefaultImpl implements DisplayTimesClient {

    @Autowired
    private DisplayTimesManager displayTimesManager;

    @Override
    public void countNow() {
        displayTimesManager.countNow();
    }

    @Override
    public void countShop(List<ShopPageView> list) {
        displayTimesManager.countShop(list);
    }

    @Override
    public void countGoods(List<GoodsPageView> list) {

        displayTimesManager.countGoods(list);
    }
}

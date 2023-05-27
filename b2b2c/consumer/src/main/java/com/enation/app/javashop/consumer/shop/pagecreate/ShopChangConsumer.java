package com.enation.app.javashop.consumer.shop.pagecreate;

import com.enation.app.javashop.consumer.core.event.ShopChangeEvent;
import com.enation.app.javashop.consumer.shop.pagecreate.service.PageCreator;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.pagecreate.PageCreatePrefixEnum;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.shop.enums.ShopMessageTypeEnum;
import com.enation.app.javashop.model.shop.vo.ShopChangeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 店铺消息
 * Author: gy
 * Date: Created in 2020/7/7 8:55 下午
 * Version: 0.0.1
 */

@Component
public class ShopChangConsumer implements ShopChangeEvent {


    @Autowired
    private PageCreator pageCreator;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void shopChange(ShopChangeMsg shopChangeMsg) {


        try {
            if (!StringUtil.equals(shopChangeMsg.getMessageType(), ShopMessageTypeEnum.All.value()) && !StringUtil.equals(shopChangeMsg.getMessageType(), ShopMessageTypeEnum.PAGE_CREATE.value())) {
                return;
            }
            //生成店铺首页静态页
            String pageName = PageCreatePrefixEnum.SHOP.getHandlerShop(shopChangeMsg.getNewShop().getShopId());
            pageCreator.createOne(pageName, ClientType.PC.name(), "/" + ClientType.PC.name() + pageName);
            pageCreator.createOne(pageName, ClientType.WAP.name(), "/" + ClientType.WAP.name() + pageName);
        } catch (Exception e) {
            logger.error("生成店铺首页静态页失败", e);
            e.printStackTrace();
        }

    }
}

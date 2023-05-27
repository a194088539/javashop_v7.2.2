package com.enation.app.javashop.consumer.shop.pagecreate;


import com.enation.app.javashop.consumer.core.event.CategoryChangeEvent;
import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
import com.enation.app.javashop.consumer.core.event.IndexChangeEvent;
import com.enation.app.javashop.consumer.core.event.MobileIndexChangeEvent;
import com.enation.app.javashop.consumer.shop.pagecreate.service.PageCreator;
import com.enation.app.javashop.model.base.message.CategoryChangeMsg;
import com.enation.app.javashop.model.base.message.CmsManageMsg;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 首页生成
 *
 * @author zh
 * @version v1.0
 * @since v6.4.0
 * 2017年8月29日 下午3:41:18
 */
@Component
public class IndexChangeConsumer implements IndexChangeEvent, CategoryChangeEvent, MobileIndexChangeEvent, GoodsChangeEvent {

    @Autowired
    private PageCreator pageCreator;

    /**
     * 生成首页
     */
    @Override
    public void createIndexPage(CmsManageMsg operation) {
        this.createIndex();
    }

    @Override
    public void categoryChange(CategoryChangeMsg categoryChangeMsg) {
        this.createIndex();
    }

    private void createIndex() {
        try {
            // 生成静态页面
            pageCreator.createIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mobileIndexChange(CmsManageMsg operation) {
        this.createIndex();
    }

    @Override
    public void goodsChange(GoodsChangeMsg goodsChangeMsg) {
        this.createIndex();
    }
}

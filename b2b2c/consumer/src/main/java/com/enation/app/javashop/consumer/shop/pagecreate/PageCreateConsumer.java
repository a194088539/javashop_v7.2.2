package com.enation.app.javashop.consumer.shop.pagecreate;

import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.consumer.core.event.PageCreateEvent;
import com.enation.app.javashop.consumer.shop.pagecreate.service.PageCreator;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.system.StaticsPageHelpClient;
import com.enation.app.javashop.model.pagecreate.PageCreateEnum;
import com.enation.app.javashop.model.system.vo.ProgressEnum;
import com.enation.app.javashop.model.system.vo.TaskProgress;
import com.enation.app.javashop.model.system.vo.TaskProgressConstant;
import com.enation.app.javashop.model.util.progress.ProgressManager;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 静态页面创建
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/7/17 下午3:55
 */
@Service
public class PageCreateConsumer implements PageCreateEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PageCreator pageCreator;

    @Autowired
    private ProgressManager progressManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private StaticsPageHelpClient staticsPageHelpClient;

    @Autowired
    private ShopClient shopClient;

    @Autowired
    private Debugger debugger;

    /**
     * 生成
     *
     * @param choosePages
     */
    @Override
    public void createPage(String[] choosePages) {
        debugger.log("开始生成静态页面：", StringUtil.arrayToString(choosePages, ","));
        int goodsCount = 0, helpCount = 0, indexCount = 2, shopCount = 0;
        int count = 0;
        for (String choose : choosePages) {
            if (choose.equals(PageCreateEnum.GOODS.name())) {
                goodsCount = goodsClient.queryGoodsCount();
                count += goodsCount;
            }
            if (choose.equals(PageCreateEnum.HELP.name())) {
                helpCount = staticsPageHelpClient.count();
                count += helpCount;
            }
            if (choose.equals(PageCreateEnum.INDEX.name())) {
                count += indexCount;
            }
            if (choose.equals(PageCreateEnum.SHOP.name())) {
                shopCount = shopClient.queryShopCount();
                count += shopCount;
            }
        }
        this.createMessage(count);
        try {
            for (String choose : choosePages) {

                if (choose.equals(PageCreateEnum.GOODS.name()) && goodsCount > 0) {
                    this.pageCreator.createGoods();
                }
                if (choose.equals(PageCreateEnum.HELP.name()) && helpCount > 0) {
                    this.pageCreator.createHelp();
                }
                if (choose.equals(PageCreateEnum.INDEX.name())) {
                    this.pageCreator.createIndex();
                }
                if (choose.equals(PageCreateEnum.SHOP.name())) {
                    this.pageCreator.createShopIndex();
                }

            }
            this.successMessage();
        } catch (Exception e) {
            this.logger.error("静态页面异常：", e);
            this.errorMessage("静态页面异常：" + e.getMessage());

        }


    }

    private void createMessage(Integer total) {

        progressManager.putProgress(TaskProgressConstant.PAGE_CREATE, new TaskProgress(total));
    }


    private void successMessage() {
        TaskProgress tk = progressManager.getProgress(TaskProgressConstant.PAGE_CREATE);
        if (tk == null) {
            tk = new TaskProgress(0);
            tk.step("静态页生成停止");
        } else {
            tk.step("静态页生成完成");
        }

        tk.success();
        progressManager.putProgress(TaskProgressConstant.PAGE_CREATE, tk);
    }

    private void errorMessage(String message) {
        TaskProgress tk = progressManager.getProgress(TaskProgressConstant.PAGE_CREATE);
        if (tk == null) {
            tk = new TaskProgress(0);
        }
        tk.setTaskStatus(ProgressEnum.EXCEPTION.name());
        tk.setMessage(message);
        progressManager.putProgress(TaskProgressConstant.PAGE_CREATE, tk);
    }


}
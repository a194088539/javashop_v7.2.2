package com.enation.app.javashop.model.pagecreate;

/**
 * PageCreatePrefixEnum
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-17 下午2:15
 */
public enum PageCreatePrefixEnum {

    /**
     * 首页
     */
    INDEX("/"),
    /**
     * 商品页面
     */
    GOODS("/goods/{goods_id}"),

    /**
     * 店铺页面
     */
    SHOP("/shop/{shop_id}"),

    /**
     * 帮助页面
     */
    HELP("/help/{article_id}");

    String prefix;

    PageCreatePrefixEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getHandlerGoods(Long goodsId) {
        return this.prefix.replace("{goods_id}", goodsId.toString());
    }

    public String getHandlerShop(Long shopId) {
        return this.prefix.replace("{shop_id}", shopId.toString());
    }

    public String getHandlerHelp(Integer articleId) {
        return this.prefix.replace("{article_id}", articleId.toString());

    }
}

package com.enation.app.javashop.client.promotion;

import com.enation.app.javashop.model.goods.dto.ExchangeClientDTO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;

/**
 * @author fk
 * @version v2.0
 * @Description: 积分兑换
 * @date 2018/8/21 15:13
 * @since v7.0.0
 */
public interface ExchangeGoodsClient {

    /**
     * 添加积分兑换
     * @param dto 积分兑换
     * @return ExchangeSetting 积分兑换 */
    ExchangeDO add(ExchangeClientDTO dto);

    /**
     * 修改积分兑换
     * @param dto 商品DTO
     * @return ExchangeSetting 积分兑换
     */
    ExchangeDO edit(ExchangeClientDTO dto);

    /**
     * 查询某个商品的积分兑换信息
     * @param goodsId
     * @return
     */
    ExchangeDO getModelByGoods(Long goodsId);

    /**
     * 删除某个商品的积分兑换信息
     * @param goodsId
     * @return
     */
    void del(Long goodsId);

}

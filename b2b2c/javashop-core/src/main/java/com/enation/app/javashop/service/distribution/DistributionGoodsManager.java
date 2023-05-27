package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.model.distribution.dos.DistributionGoods;

/**
 * 分销商品接口
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018/6/14 上午12:37
 * @Description:
 *
 */
public interface DistributionGoodsManager {

    /**
     * 设置分销商品提现设置
     * @param distributionGoods
     * @return
     */
    DistributionGoods edit(DistributionGoods distributionGoods);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 获取model
     * @param goodsId
     * @return
     */
    DistributionGoods getModel(Long goodsId);


}

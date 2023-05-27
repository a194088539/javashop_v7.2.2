package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.distribution.DistributionGoodsMapper;
import com.enation.app.javashop.model.distribution.dos.DistributionGoods;
import com.enation.app.javashop.service.distribution.DistributionGoodsManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * DistributionGoodsManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-06-14 上午12:39
 */
@Service
public class DistributionGoodsManagerImpl implements DistributionGoodsManager {

    @Autowired
    private DistributionGoodsMapper distributionGoodsMapper;

    /**
     * 修改分销商品提现设置
     *
     * @param distributionGoods
     * @return
     */
    @Override
    @Transactional(value = "distributionTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DistributionGoods edit(DistributionGoods distributionGoods) {
        QueryWrapper<DistributionGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", distributionGoods.getGoodsId());
        DistributionGoods old = distributionGoodsMapper.selectOne(wrapper);
        if (null == old) {
            distributionGoodsMapper.insert(distributionGoods);
            return distributionGoods;
        } else {
            UpdateWrapper<DistributionGoods> wrapperr = new UpdateWrapper<>();
            wrapperr.eq("id",old.getId());
            DistributionGoods distributionGood = new DistributionGoods();
            distributionGood.setGrade1Rebate(old.getGrade1Rebate());
            distributionGood.setGrade2Rebate(old.getGrade2Rebate());
            distributionGoodsMapper.update(distributionGoods,wrapperr);
            return distributionGoods;
        }
    }

    /**
     * 删除
     *
     * @param goodsId
     */
    @Override
    public void delete(Long goodsId) {
        distributionGoodsMapper.deleteById(goodsId);
    }

    /**
     * 获取
     *
     * @param goodsId
     */
    @Override
    public DistributionGoods getModel(Long goodsId) {
        QueryWrapper<DistributionGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        return distributionGoodsMapper.selectOne(wrapper);
    }
}

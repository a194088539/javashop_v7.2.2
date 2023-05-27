package com.enation.app.javashop.service.promotion.groupbuy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyQuantityLogMapper;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyQuantityLog;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyQuantityLogManager;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 团购商品库存日志表业务类
 *
 * @author xlp
 * @version v1.0
 * @since v7.0.0
 * 2018-07-09 15:32:29
 */
@Service
public class GroupbuyQuantityLogManagerImpl implements GroupbuyQuantityLogManager {

    @Autowired
    private GroupbuyQuantityLogMapper groupbuyQuantityLogMapper;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<GroupbuyQuantityLog> rollbackReduce(String orderSn) {
        //根据订单编号获取团购商品库存日志信息集合
        List<GroupbuyQuantityLog> logList = groupbuyQuantityLogMapper.selectList(new QueryWrapper<GroupbuyQuantityLog>().eq("order_sn", orderSn));

        List<GroupbuyQuantityLog> result = new ArrayList<>();
        //循环结果集，添加日志信息
        for (GroupbuyQuantityLog log : logList) {
            log.setQuantity(log.getQuantity());
            log.setOpTime(DateUtil.getDateline());
            log.setReason("取消订单，回滚库存");
            log.setLogId(null);
            this.add(log);
            result.add(log);

            //清空缓存中的促销活动商品key
            promotionGoodsManager.cleanCache(log.getGoodsId());
        }
        return result;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GroupbuyQuantityLog add(GroupbuyQuantityLog groupbuyQuantityLog) {
        //团购商品库存日志信息入库
        groupbuyQuantityLogMapper.insert(groupbuyQuantityLog);
        return groupbuyQuantityLog;
    }

}

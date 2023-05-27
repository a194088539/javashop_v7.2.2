package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.mapper.statistics.OrderDataMapper;
import com.enation.app.javashop.mapper.statistics.OrderGoodsDataMapper;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.statistics.dto.OrderData;
import com.enation.app.javashop.model.statistics.dto.OrderGoodsData;
import com.enation.app.javashop.service.statistics.OrderDataManager;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单实现
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/22 下午10:11
 */
@Service
public class OrderDataManagerImpl implements OrderDataManager {

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private OrderGoodsDataMapper orderGoodsDataMapper;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Override
    public void put(OrderDO order) {
        List<OrderItemsDO> itemsDOList = orderClient.orderItems(order.getSn());
        int goodsNum = 0;
        for (OrderItemsDO oi : itemsDOList) {
            OrderGoodsData orderGoodsData = new OrderGoodsData(oi, order);
            CategoryDO categoryDO = goodsClient.getCategory(oi.getCatId());
            orderGoodsData.setIndustryId(getIndustry(categoryDO.getCategoryPath()));
            orderGoodsData.setCategoryPath(categoryDO.getCategoryPath());
            orderGoodsDataMapper.insert(orderGoodsData);
            goodsNum = goodsNum + oi.getNum();
        }
        order.setGoodsNum(goodsNum);
        orderDataMapper.insert(new OrderData(order));
    }

    @Override
    public void change(OrderDO order) {

        //根据订单编号查询订单
        OrderData od = new QueryChainWrapper<>(orderDataMapper)
                .eq("sn", order.getSn())
                .one();

        if (od != null) {
            od.setOrderStatus(order.getOrderStatus());
            od.setPayStatus(order.getPayStatus());
        } else {
            od = new OrderData(order);
        }

        //根据订单编号修改订单
        new UpdateChainWrapper<>(orderDataMapper)
                .eq("sn", order.getSn())
                .update(od);
    }


    /**
     * 获取第二级别分类。
     *
     * @param path
     * @return
     */
    private Long getIndustry(String path) {
        try {
            String pattern = "(0\\|)(\\d+)";
            // 创建 Pattern 对象
            Pattern r = Pattern.compile(pattern);
            // 现在创建 matcher 对象
            Matcher m = r.matcher(path);
            if (m.find()) {
                return new Long(m.group(2));
            }
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}

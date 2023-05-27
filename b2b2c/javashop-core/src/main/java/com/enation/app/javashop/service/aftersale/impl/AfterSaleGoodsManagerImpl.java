package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleGoodsMapper;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleGoodsDO;
import com.enation.app.javashop.service.aftersale.AfterSaleGoodsManager;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 售后服务商品业务接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-03
 */
@Service
public class AfterSaleGoodsManagerImpl implements AfterSaleGoodsManager {

    @Autowired
    private AfterSaleGoodsMapper afterSaleGoodsMapper;

    @Override
    public void add(AfterSaleGoodsDO afterSaleGoodsDO) {
        //售后服务商品信息入库
        afterSaleGoodsMapper.insert(afterSaleGoodsDO);
    }

    @Override
    public AfterSaleGoodsDO fillGoods(String serviceSn, Integer returnNum, OrderItemsDO itemsDO) {
        //新建售后商品信息对象
        AfterSaleGoodsDO afterSaleGoodsDO = new AfterSaleGoodsDO();
        //设置售后服务单号
        afterSaleGoodsDO.setServiceSn(serviceSn);
        //设置售后服务商品id
        afterSaleGoodsDO.setGoodsId(itemsDO.getGoodsId());
        //设置售后服务商品sku
        afterSaleGoodsDO.setSkuId(itemsDO.getProductId());
        //设置售后服务商品发货数量
        afterSaleGoodsDO.setShipNum(itemsDO.getNum());
        //设置售后服务商品价格
        afterSaleGoodsDO.setPrice(itemsDO.getPrice());
        //设置售后服务商品退货数量
        afterSaleGoodsDO.setReturnNum(returnNum);
        //设置售后服务商品名称
        afterSaleGoodsDO.setGoodsName(itemsDO.getName());
        //设置售后服务商品图片
        afterSaleGoodsDO.setGoodsImage(itemsDO.getImage());
        //设置售后服务商品规格json
        afterSaleGoodsDO.setSpecJson(itemsDO.getSpecJson());
        //售后商品信息入库
        this.add(afterSaleGoodsDO);

        return afterSaleGoodsDO;
    }

    @Override
    public List<AfterSaleGoodsDO> listGoods(String serviceSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleGoodsDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("service_sn", serviceSn);
        //返回售后服务商品信息集合
        return afterSaleGoodsMapper.selectList(wrapper);
    }

    @Override
    public void updateStorageNum(String serviceSn, Long skuId, Integer num) {
        //新建修改条件包装器
        UpdateWrapper<AfterSaleGoodsDO> wrapper = new UpdateWrapper<>();
        //修改售后服务商品入库数量
        wrapper.set("storage_num", num);
        //以售后服务单号为修改条件
        wrapper.eq("service_sn", serviceSn);
        //以售后服务商品sku为修改条件
        wrapper.eq("sku_id", skuId);
        //修改售后服务商品信息
        afterSaleGoodsMapper.update(new AfterSaleGoodsDO(), wrapper);
    }
}

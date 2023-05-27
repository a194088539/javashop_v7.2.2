package com.enation.app.javashop.service.promotion.exchange.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.exchange.ExchangeMapper;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.exchange.dto.ExchangeQueryParam;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDetailDTO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import com.enation.app.javashop.service.promotion.tool.support.PromotionCacheKeys;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 积分商品业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 11:47:18
 */
@Service
public class ExchangeGoodsManagerImpl implements ExchangeGoodsManager {

    @Autowired
    private ExchangeMapper exchangeMapper;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private Cache cache;


    @Override
    public WebPage list(ExchangeQueryParam param) {
        //获取积分兑换分页列表数据
        IPage<ExchangeDO> iPage = exchangeMapper.selectExchangePage(new Page(param.getPageNo(), param.getPageSize()), PromotionTypeEnum.EXCHANGE.name(), param);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ExchangeDO add(ExchangeDO exchangeSetting, PromotionGoodsDTO goodsDTO) {
        //设置商品ID
        exchangeSetting.setGoodsId(goodsDTO.getGoodsId());
        //设置商品名称
        exchangeSetting.setGoodsName(goodsDTO.getGoodsName());
        //设置商品价格
        exchangeSetting.setGoodsPrice(goodsDTO.getPrice());
        //设置商品图片
        exchangeSetting.setGoodsImg(goodsDTO.getThumbnail());

        //如果允许积分兑换 0:否，1:是
        if (exchangeSetting.getEnableExchange() == 1) {
            //如果积分兑换分类ID等于空，默认设置为0
            if (exchangeSetting.getCategoryId() == null) {
                exchangeSetting.setCategoryId(0L);
            }

            //积分兑换信息入库
            exchangeMapper.insert(exchangeSetting);
            //获取主键ID
            Long settingId = exchangeSetting.getExchangeId();

            //获取当前时间
            long nowTime = DateUtil.getDateline();
            //获取一年后的时间
            long endTime = endOfSomeDay(365);

            List<PromotionGoodsDTO> list = new ArrayList<>();
            list.add(goodsDTO);

            //新建促销活动详情对象
            PromotionDetailDTO detailDTO = new PromotionDetailDTO();
            //设置促销活动标题
            detailDTO.setTitle("积分活动");
            //设置促销活动类型为积分兑换
            detailDTO.setPromotionType(PromotionTypeEnum.EXCHANGE.name());
            //设置促销活动ID
            detailDTO.setActivityId(settingId);
            //设置活动开始时间
            detailDTO.setStartTime(nowTime);
            //设置活动结束时间
            detailDTO.setEndTime(endTime);
            //入库操作
            this.promotionGoodsManager.add(list, detailDTO);
            //将积分兑换信息放入缓存中
            this.cache.put(PromotionCacheKeys.getExchangeKey(settingId), exchangeSetting);
        }

        return exchangeSetting;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ExchangeDO edit(ExchangeDO exchangeSetting, PromotionGoodsDTO goodsDTO) {
        //删除之前的相关信息
        this.deleteByGoods(goodsDTO.getGoodsId());

        //如果允许积分兑换 0:否，1:是
        if (exchangeSetting != null && exchangeSetting.getEnableExchange() == 1) {
            //新添加积分兑换信息
            this.add(exchangeSetting, goodsDTO);
        }
        return exchangeSetting;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(Long id) {
        //删除数据库信息
        exchangeMapper.deleteById(id);
        //删除活动商品对照表的关系
        this.promotionGoodsManager.delete(id, PromotionTypeEnum.EXCHANGE.name());
        //删除缓存
        this.cache.remove(PromotionCacheKeys.getExchangeKey(id));
    }

    @Override
    public ExchangeDO getModel(Long id) {
        //从缓存中获取积分兑换相关信息
        ExchangeDO exchangeDO = (ExchangeDO) this.cache.get(PromotionCacheKeys.getExchangeKey(id));
        //如果缓存中的信息等于空，那么需要从数据库中查询
        if (exchangeDO == null) {
            exchangeDO = exchangeMapper.selectById(id);
        }
        return exchangeDO;
    }

    @Override
    public ExchangeDO getModelByGoods(Long goodsId) {
        //新建查询条件包装器
        QueryWrapper<ExchangeDO> wrapper = new QueryWrapper<>();
        //以商品ID为查询条件
        wrapper.eq("goods_id", goodsId);
        //返回查询出的积分兑换信息
        return exchangeMapper.selectOne(wrapper);
    }

    @Override
    public ExchangeDO getModelByCategoryId(Long categoryId) {
        //新建查询条件包装器
        QueryWrapper<ExchangeDO> wrapper = new QueryWrapper<>();
        //以分类ID为查询条件
        wrapper.eq("category_id", categoryId);
        //返回查询出的积分兑换信息
        return exchangeMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void deleteByGoods(Long goodsId) {
        //根据商品ID获取积分兑换信息
        ExchangeDO exchangeDO = this.getModelByGoods(goodsId);
        if (exchangeDO == null) {
            return;
        }
        //删除积分兑换信息
        this.delete(exchangeDO.getExchangeId());
    }

    /**
     * 某天的开始时间
     *
     * @param dayUntilNow 距今多少天以后
     * @return 时间戳
     */
    private static long endOfSomeDay(int dayUntilNow) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, +dayUntilNow);
        Date date = calendar.getTime();
        return date.getTime() / 1000;
    }
}

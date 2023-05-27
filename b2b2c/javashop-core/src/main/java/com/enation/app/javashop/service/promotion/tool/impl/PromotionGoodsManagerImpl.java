package com.enation.app.javashop.service.promotion.tool.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.promotion.PromotionGoodsMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyGoodsVO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillGoodsVO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDetailDTO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionAbnormalGoods;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;
import com.enation.app.javashop.service.promotion.coupon.CouponManager;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountGiftManager;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyGoodsManager;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
import com.enation.app.javashop.service.promotion.minus.MinusManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillManager;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动商品对照实现类
 *
 * @author Snow create in 2018/3/21
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class PromotionGoodsManagerImpl implements PromotionGoodsManager {

    @Autowired
    private PromotionGoodsMapper promotionGoodsMapper;

    @Autowired
    private Cache cache;

    @Autowired
    private ExchangeGoodsManager exchangeGoodsManager;

    @Autowired
    private GroupbuyGoodsManager groupbuyGoodsManager;

    @Autowired
    private FullDiscountManager fullDiscountManager;

    @Autowired
    private MinusManager minusManager;

    @Autowired
    private HalfPriceManager halfPriceManager;

    @Autowired
    private SeckillManager seckillManager;

    @Autowired
    private FullDiscountGiftManager fullDiscountGiftManager;

    @Autowired
    private CouponManager couponManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SeckillGoodsManager seckillGoodsManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void addAndCheck(List<PromotionGoodsDO> list, PromotionDetailDTO detailDTO) {

        if (list.isEmpty()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "没有可用的商品参与此活动");
        }

        //检测限时抢购和团购是否存在同时参与的情况
        if (PromotionTypeEnum.SECKILL.name().equals(detailDTO.getPromotionType())
                || PromotionTypeEnum.GROUPBUY.name().equals(detailDTO.getPromotionType())) {
            List<Long> skuIds = new ArrayList<>();
            list.forEach(goodsDO -> {
                skuIds.add(goodsDO.getSkuId());
            });
            List<PromotionAbnormalGoods> result = this.checkPromotion(skuIds, detailDTO.getStartTime(), detailDTO.getEndTime(), detailDTO.getPromotionType());
            if (result.size() > 0) {
                StringBuffer errorMessage = new StringBuffer();
                for (PromotionAbnormalGoods goods : result) {
                    errorMessage.append("【");
                    errorMessage.append(goods.getGoodsName());
                    errorMessage.append("】");
                }
                errorMessage.append("活动存在冲突，审核失败！");
                throw new ServiceException(PromotionErrorCode.E401.code(), errorMessage.toString());
            }
        }


        /**
         * 因为 Spring jdbcTemplate 没有提供批量插入的方法,
         * 使用 insert into table_name (xxx1,xxx2) VALUES (1,2),(3,3) 拼接参数，会有sql注入的问题。
         * 所以经过和架构师的沟通，此操作不是高频操作，可以一个一个插入数据库。
         */
        for (PromotionGoodsDO goodsDO : list) {
            String promotionType = goodsDO.getPromotionType();
            //因为限时抢购和团购活动为 平台发布,不是商家
            if (goodsDO.getSellerId() == null && !PromotionTypeEnum.SECKILL.name().equals(promotionType)
                    && !PromotionTypeEnum.GROUPBUY.name().equals(promotionType)) {
                goodsDO.setSellerId(UserContext.getSeller().getSellerId());
            } else {
                //如果是限时抢购或团购活动，需要把限时抢购价格和团购价格存放至表中
                goodsDO.setPrice(goodsDO.getPrice());
            }
            this.promotionGoodsMapper.insert(goodsDO);

        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void add(List<PromotionGoodsDTO> list, PromotionDetailDTO detailDTO) {

        if (list.isEmpty()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "没有可用的商品参与此活动");
        }

        /**
         * 因为 Spring jdbcTemplate 没有提供批量插入的方法,
         * 使用 insert into table_name (xxx1,xxx2) VALUES (1,2),(3,3) 拼接参数，会有sql注入的问题。
         * 所以经过和架构师的沟通，此操作不是高频操作，可以一个一个插入数据库。
         */
        for (PromotionGoodsDTO goodsDTO : list) {

            PromotionGoodsDO goodsDO = new PromotionGoodsDO();
            goodsDO.setGoodsId(goodsDTO.getGoodsId());
            goodsDO.setSkuId(goodsDTO.getSkuId());
            goodsDO.setStartTime(detailDTO.getStartTime());
            goodsDO.setEndTime(detailDTO.getEndTime());
            goodsDO.setActivityId(detailDTO.getActivityId());
            goodsDO.setPromotionType(detailDTO.getPromotionType());
            goodsDO.setTitle(detailDTO.getTitle());
            goodsDO.setSellerId(goodsDTO.getSellerId());

            String promotionType = goodsDO.getPromotionType();
            //因为限时抢购和团购活动为 平台发布,不是商家
            if (goodsDO.getSellerId() == null && !PromotionTypeEnum.SECKILL.name().equals(promotionType)
                    && !PromotionTypeEnum.GROUPBUY.name().equals(promotionType)) {
                goodsDO.setSellerId(UserContext.getSeller().getSellerId());
            } else {
                //如果是限时抢购或团购活动，需要把限时抢购价格和团购价格存放至表中
                goodsDO.setPrice(goodsDTO.getPrice());
            }
            this.promotionGoodsMapper.insert(goodsDO);

        }

    }

    @Override
    public void edit(List<PromotionGoodsDTO> list, PromotionDetailDTO detailDTO) {
        this.delete(detailDTO.getActivityId(), detailDTO.getPromotionType());
        this.add(list, detailDTO);
    }


    @Override
    public void delete(Long activityId, String promotionType) {
        this.promotionGoodsMapper.delete(new QueryWrapper<PromotionGoodsDO>()
                .eq("activity_id", activityId)
                .eq("promotion_type", promotionType));
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void delete(Long goodsId, Long activityId, String promotionType) {

        this.promotionGoodsMapper.delete(new QueryWrapper<PromotionGoodsDO>()
                .eq("activity_id", activityId)
                .eq("promotion_type", promotionType)
                .eq("goods_id", goodsId));
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void delete(List<Long> delSkuIds) {

        //如果delSkuIds为空，则不删除
        if (delSkuIds == null || delSkuIds.isEmpty()) {
            return;
        }
        this.promotionGoodsMapper.delete(new QueryWrapper<PromotionGoodsDO>()
                .in("sku_id", delSkuIds)
                .ge("end_time", DateUtil.getDateline())
                .ne("promotion_type", PromotionTypeEnum.EXCHANGE.name()));

        //删除限时抢购商品
        seckillGoodsManager.deleteSeckillGoods(delSkuIds);
        //删除团购商品
        groupbuyGoodsManager.deleteGoods(delSkuIds);


    }

    @Override
    public List<PromotionVO> getPromotion(Long goodsId) {

        long currTime = DateUtil.getDateline();

        String currDate = DateUtil.toString(currTime, "yyyyMMdd");
        //使用关键字+当天日期+goods_id为关键字
        String promotionKey = CachePrefix.PROMOTION_KEY + currDate + goodsId;
        //从缓存中读取商品活动信息
        List<PromotionVO> promotionVOList = (List<PromotionVO>) cache.get(promotionKey);

        if (promotionVOList == null || promotionVOList.isEmpty()) {
            promotionVOList = new ArrayList<>();


            //读取此商品参加的活动
//            String sql = "select distinct goods_id,sku_id, start_time, end_time, activity_id, promotion_type,title,num,price " +
//                    "from es_promotion_goods where goods_id=? and start_time<=? and end_time>=?";
//            List<PromotionGoodsDO> resultList = this.daoSupport.queryForList(sql, PromotionGoodsDO.class, goodsId, currTime, currTime);

            List<PromotionGoodsDO> resultList = this.promotionGoodsMapper.selectList(new QueryWrapper<PromotionGoodsDO>()
                    .select("distinct goods_id", "sku_id", "start_time", "end_time", "activity_id", " promotion_type", "title", "num", "price")
                    .eq("goods_id", goodsId)
                    .le("start_time", currTime)
                    .ge("end_time", currTime));


            //查询全部商品参与的活动，条件为：
            // 商品id && 大于等于开始时间 && 小于等于结束时间 && 不是团购活动 && 不是限时抢购活动 && 商家id
            Integer totalGoodsId = -1;
//            sql = "select distinct goods_id, start_time, end_time, activity_id, promotion_type,title,num,price " +
//                    " from es_promotion_goods where goods_id=? and start_time<=? and end_time>=? " +
//                    " and promotion_type != '" + PromotionTypeEnum.GROUPBUY.name() + "' " +
//                    " and promotion_type != '" + PromotionTypeEnum.SECKILL.name() + "' and seller_id=?";

            CacheGoods cacheGoods = this.goodsClient.getFromCache(goodsId);
//            List<PromotionGoodsDO> promotionGoodsDOList = this.daoSupport.queryForList(sql, PromotionGoodsDO.class, totalGoodsId, currTime, currTime, cacheGoods.getSellerId());
            List<PromotionGoodsDO> promotionGoodsDOList = this.promotionGoodsMapper.selectList(new QueryWrapper<PromotionGoodsDO>()
                    .select("distinct goods_id", "start_time", "end_time", "activity_id", " promotion_type", "title", "num", "price")
                    .eq("goods_id", totalGoodsId)
                    .le("start_time", currTime)
                    .ge("end_time", currTime)
                    .ne("promotion_type", PromotionTypeEnum.GROUPBUY.name())
                    .ne("promotion_type", PromotionTypeEnum.SECKILL.name())
                    .eq("seller_id", cacheGoods.getSellerId()));


            resultList.addAll(promotionGoodsDOList);

            for (PromotionGoodsDO promotionGoodsDO : resultList) {
                PromotionVO promotionVO = new PromotionVO();
                BeanUtils.copyProperties(promotionGoodsDO, promotionVO);

                //积分换购
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.EXCHANGE.name())) {
                    ExchangeDO exchange = exchangeGoodsManager.getModel(promotionGoodsDO.getActivityId());
                    promotionVO.setExchange(exchange);
                }

                //团购
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.GROUPBUY.name())) {
                    GroupbuyGoodsDO groupbuyGoodsDO = groupbuyGoodsManager.getModel(promotionGoodsDO.getActivityId(), promotionGoodsDO.getGoodsId(), promotionGoodsDO.getSkuId());

                    GroupbuyGoodsVO groupbuyGoodsVO = new GroupbuyGoodsVO();
                    BeanUtils.copyProperties(groupbuyGoodsDO, groupbuyGoodsVO);

                    promotionVO.setGroupbuyGoodsVO(groupbuyGoodsVO);
                }

                //满优惠活动
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.FULL_DISCOUNT.name())) {
                    FullDiscountVO fullDiscountVO = this.fullDiscountManager.getModel(promotionGoodsDO.getActivityId());

                    //赠品
                    if (fullDiscountVO.getIsSendGift() != null && fullDiscountVO.getIsSendGift() == 1 && fullDiscountVO.getGiftId() != null) {
                        FullDiscountGiftDO giftDO = fullDiscountGiftManager.getModel(fullDiscountVO.getGiftId());
                        fullDiscountVO.setFullDiscountGiftDO(giftDO);
                    }

                    //判断是否赠品优惠券
                    if (fullDiscountVO.getIsSendBonus() != null && fullDiscountVO.getIsSendBonus() == 1 && fullDiscountVO.getBonusId() != null) {
                        CouponDO couponDO = this.couponManager.getModel(fullDiscountVO.getBonusId());
                        fullDiscountVO.setCouponDO(couponDO);
                    }

                    //判断是否赠品优惠券
                    if (fullDiscountVO.getIsSendPoint() != null && fullDiscountVO.getIsSendPoint() == 1 && fullDiscountVO.getPointValue() != null) {
                        fullDiscountVO.setPoint(fullDiscountVO.getPointValue());
                    }


                    promotionVO.setFullDiscountVO(fullDiscountVO);
                }

                //单品立减活动
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.MINUS.name())) {
                    MinusVO minusVO = this.minusManager.getFromDB(promotionGoodsDO.getActivityId());
                    promotionVO.setMinusVO(minusVO);
                }

                //第二件半价活动
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.HALF_PRICE.name())) {
                    HalfPriceVO halfPriceVO = this.halfPriceManager.getFromDB(promotionGoodsDO.getActivityId());
                    promotionVO.setHalfPriceVO(halfPriceVO);
                }
                //限时抢购活动
                if (promotionGoodsDO.getPromotionType().equals(PromotionTypeEnum.SECKILL.name())) {
                    SeckillGoodsVO seckillGoodsVO = this.seckillManager.getSeckillSku(promotionGoodsDO.getGoodsId(), promotionGoodsDO.getSkuId());
                    promotionVO.setSeckillGoodsVO(seckillGoodsVO);
                }

                promotionVOList.add(promotionVO);
            }

        } else {


            for (PromotionVO promotionVO : promotionVOList) {

                //当前时间大于活动的开始时间 && 当前时间小于活动的结束时间
                if (currTime > promotionVO.getStartTime() && currTime < promotionVO.getEndTime()) {

                    //初始化限时抢购数据，防止返回的时间戳错误
                    promotionVO.setSeckillGoodsVO(null);

                    //限时抢购活动需要重新计算 距离结束的时间戳 返回给前端
                    if (promotionVO.getPromotionType().equals(PromotionTypeEnum.SECKILL.name())) {
                        SeckillGoodsVO seckillGoodsVO = this.seckillManager.getSeckillSku(promotionVO.getGoodsId(), promotionVO.getSkuId());
                        promotionVO.setSeckillGoodsVO(seckillGoodsVO);
                    }
                    promotionVOList.add(promotionVO);
                }
            }

        }

        return promotionVOList;
    }


    @Override
    public List<PromotionGoodsDO> getPromotionGoods(Long activityId, String promotionType) {
//        String sql = "select * from es_promotion_goods where activity_id=? and promotion_type=?";
//        List<PromotionGoodsDO> goodsDOList = this.daoSupport.queryForList(sql, PromotionGoodsDO.class, activityId, promotionType);
        List<PromotionGoodsDO> goodsDOList = this.promotionGoodsMapper.selectList(new QueryWrapper<PromotionGoodsDO>()
                .eq("activity_id", activityId)
                .eq("promotion_type", promotionType));

        return goodsDOList;
    }


    @Override
    public void cleanCache(Long goodsId) {
        long currTime = DateUtil.getDateline();
        String currDate = DateUtil.toString(currTime, "yyyyMMdd");
        //使用关键字+当天日期+goods_id为关键字
        String promotionKey = CachePrefix.PROMOTION_KEY + currDate + goodsId;
        this.cache.remove(promotionKey);

    }

    /**
     * 重新写入缓存
     *
     * @param goodsId
     */
    @Override
    public void reputCache(Long goodsId) {
        this.cleanCache(goodsId);
        this.getPromotion(goodsId);
    }

    /**
     * 查询指定时间范围，是否有参与其他活动
     *
     * @param skuIds    商品skuid集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @Override
    public List<PromotionAbnormalGoods> checkPromotion(List<Long> skuIds, Long startTime, Long endTime, String promotionType) {


        //何为冲突
        //（1）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                已经存在 活动
        //       --》【              】《--
        // ******************************************************************       时间轴

        //（2）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                                       已经存在 活动
        //                              --》【              】《--
        // ******************************************************************       时间轴

        //（3）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                       已经存在 活动
        //        --》【                               】《--
        // ******************************************************************       时间轴

        //（4）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                       已经存在 活动
        //                      --》【     】《--
        // ******************************************************************       时间轴

//        List term = new ArrayList();
//        List<String> skuIdsStr = new ArrayList<>();
//        for (Long goodsId :
//                skuIds) {
//            term.add(goodsId);
//            skuIdsStr.add("?");
//        }
//
//        term.add(startTime);
//        term.add(startTime);
//        term.add(endTime);
//        term.add(endTime);
//        term.add(startTime);
//        term.add(endTime);
//        term.add(endTime);
//        term.add(startTime);
//
//        StringBuffer sql = new StringBuffer("select * from es_promotion_goods where sku_id in (");
//        sql.append(StringUtil.listToString(skuIdsStr, ","));
//        sql.append(") and ( (start_time < ? and end_time > ?) or (start_time < ? and end_time > ?) or(start_time < ? and end_time > ?) or(start_time < ? and end_time > ?))");
//
//
//        if (PromotionTypeEnum.SECKILL.name().equals(promotionType)) {
//            sql.append(" and promotion_type = ? ");
//            term.add(PromotionTypeEnum.GROUPBUY.name());
//        }
//
//        if (PromotionTypeEnum.GROUPBUY.name().equals(promotionType)) {
//            sql.append(" and promotion_type = ? ");
//            term.add(PromotionTypeEnum.SECKILL.name());
//        }

        //查询时间轴以外的促销活动商品
        List<PromotionGoodsDO> promotionGoodsDOS = this.promotionGoodsMapper.selectList(new QueryWrapper<PromotionGoodsDO>()
                .in("sku_id", skuIds)
                .and(timeWrapper -> {
                    timeWrapper.lt("start_time", startTime).gt("end_time", startTime)
                            .or(orWrapper -> {
                                orWrapper.lt("start_time", endTime).gt("end_time", endTime);
                            })
                            .or(orWrapper -> {
                                orWrapper.lt("start_time", startTime).gt("end_time", endTime);
                            })
                            .or(orWrapper -> {
                                orWrapper.lt("start_time", endTime).gt("end_time", startTime);
                            });
                })
                .eq(PromotionTypeEnum.SECKILL.name().equals(promotionType), "promotion_type", PromotionTypeEnum.GROUPBUY.name())
                .eq(PromotionTypeEnum.GROUPBUY.name().equals(promotionType), "promotion_type", PromotionTypeEnum.SECKILL.name()));


//        List<PromotionGoodsDO> promotionGoodsDOS = this.daoSupport.queryForList(sql.toString(),
//                PromotionGoodsDO.class, term.toArray()
//        );

        List<PromotionAbnormalGoods> promotionAbnormalGoods = new ArrayList<>();
        promotionGoodsDOS.forEach(goods -> {
            PromotionAbnormalGoods paGoods = new PromotionAbnormalGoods();
            paGoods.setEndTime(goods.getEndTime());
            paGoods.setStartTime(goods.getStartTime());
            paGoods.setGoodsId(goods.getGoodsId());
            paGoods.setGoodsName(goodsClient.getFromCache(paGoods.getGoodsId()).getGoodsName());
            paGoods.setPromotionType(goods.getPromotionType());
            promotionAbnormalGoods.add(paGoods);
        });
        return promotionAbnormalGoods;

    }

    @Override
    public List<PromotionGoodsDO> getPromotionGoodsList(String promotionType, Long nowTime) {
        return promotionGoodsMapper.selectGoodsList(promotionType, nowTime);
    }

    @Override
    public void updatePromotionEndTime(Long endTime, Long pgId) {
        PromotionGoodsDO goods = new PromotionGoodsDO();
        goods.setPgId(pgId);
        goods.setEndTime(endTime);
        promotionGoodsMapper.updateById(goods);
    }

    /**
     * 活动商品缓存
     *
     * @param startTime
     * @param goodsId
     * @return
     */
    private String getPromotionGoodsKey(long startTime, long goodsId) {
        String key = CachePrefix.PROMOTION_KEY.getPrefix() + DateUtil.toString(startTime, "yyyyMMdd") + goodsId;
        return key;
    }

    public static void main(String[] args) {

        long line = DateUtil.getDateline("2018-12-25 22:05:00", "yyyy-MM-dd HH:mm:ss");
        String str = DateUtil.toString(1514120400L, "yyyy-MM-dd HH:mm:ss");
//        System.out.println(str);
        System.out.println(line);
//        System.out.println(DateUtil.getDateline());
//        Long[] goodsIds = new Integer[2];
//
//        goodsIds[0]=1;
//        goodsIds[1]=2;
//        String goodsIdStr = StringUtils.join(goodsIds,",");
//
//        System.out.println(goodsIdStr);


    }

}

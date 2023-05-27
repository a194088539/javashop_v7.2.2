package com.enation.app.javashop.service.promotion.coupon.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.promotion.CouponMapper;
import com.enation.app.javashop.mapper.promotion.fulldiscount.FullDiscountMapper;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.dto.CouponParams;
import com.enation.app.javashop.model.promotion.coupon.enums.CouponType;
import com.enation.app.javashop.model.promotion.coupon.enums.CouponUseScope;
import com.enation.app.javashop.model.promotion.coupon.vo.CouponVO;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponVO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.service.promotion.coupon.CouponManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 优惠券业务类
 *
 * @author Snow
 * @version v2.0
 * @since v7.0.0
 * 2018-04-17 23:19:39
 */
@Service
public class CouponManagerImpl implements CouponManager {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private FullDiscountMapper fullDiscountMapper;

    @Override
    public WebPage list(CouponParams couponParams) {

        QueryWrapper<CouponDO> wrapper = new QueryWrapper<CouponDO>()
                .eq("seller_id", couponParams.getSellerId())
                //如果开始时间不为空，则>=开始时间
                .ge(couponParams.getStartTime() != null, "start_time", couponParams.getStartTime())
                //如果结束时间不为空，则<=结束时间
                .le(couponParams.getEndTime() != null, "end_time", couponParams.getEndTime())
                //如果关键字不为空，则模糊查询title
                .like(StringUtil.notEmpty(couponParams.getKeyword()), "title", couponParams.getKeyword())
                //优惠券id倒叙
                .orderByDesc("coupon_id");


        IPage<CouponVO> iPage = this.couponMapper.selectCouponPage(new Page(couponParams.getPageNo(), couponParams.getPageSize()),wrapper);

        return PageConvert.convert(iPage);
    }

    @Override
    public List<GoodsCouponVO> getListByGoods(Long goodsId) {

        List<GoodsCouponVO> res = new ArrayList<>();

        //查询商品的分类
        CacheGoods goods = goodsClient.getFromCache(goodsId);
        //商品的规格信息
        List<GoodsSkuVO> skuList = goods.getSkuList();

        Long sellerId = goods.getSellerId();
        Long categoryId = goods.getCategoryId();

        long time = DateUtil.getDateline();
//        term.add(time);
//        term.add(time);
//        term.add(CouponType.FREE_GET.name());
//        term.add(sellerId);
//        //全品
//        String allSql = "use_scope = '" + CouponUseScope.ALL + "'";
//        //分类
//        String catSql = "use_scope = '" + CouponUseScope.CATEGORY + "' and concat(',',scope_id,',') like '%," + categoryId + ",%'";
//
//        String sql = "select * from es_coupon where start_time < ? and end_time > ? " +
//                " and type = ? and ( seller_id = ?  or (seller_id = 0 and (" + allSql + " or (" + catSql + "))))";

        List<CouponDO> couponList = this.couponMapper.selectList(new QueryWrapper<CouponDO>()
                .lt("start_time", time)
                .gt("end_time", time)
                .eq("type", CouponType.FREE_GET.name())
                .and(ew -> {
                    //店铺优惠券
                    ew.eq("seller_id", sellerId).or(e -> {
                        //平台优惠券
                        e.eq("seller_id", 0)
                                .and(catOrAllWrapper -> {
                                    //使用范围是全部
                                    catOrAllWrapper.eq("use_scope", CouponUseScope.ALL.name())
                                            //或者
                                            .or(catWrapper -> {
                                                //使用范围是分类，且满足分类id
                                                catWrapper.eq("use_scope", CouponUseScope.CATEGORY.name())
                                                        .like("concat(',',scope_id,',')", "," + categoryId + ",");
                                            });
                                });
                    });
                }));


        //查询当前可以使用的部分商品的优惠券
//        term.remove(3);
//        sql = "select * from es_coupon where start_time < ? and end_time > ?  and type = ? " +
//                "and seller_id = 0 and use_scope = '" + CouponUseScope.SOME_GOODS + "'";
//        List<CouponDO> someGoodsCouponList = this.daoSupport.queryForList(sql, CouponDO.class, term.toArray());

        List<CouponDO> someGoodsCouponList = this.couponMapper.selectList(new QueryWrapper<CouponDO>()
                .lt("start_time", time)
                .gt("end_time", time)
                .eq("type", CouponType.FREE_GET.name())
                .eq("seller_id", 0)
                .eq("use_scope", CouponUseScope.SOME_GOODS.name()));

        //循环sku，嵌套循环部分商品优惠券，查看skuid是否在scope_id中
        if (skuList != null) {
            for (GoodsSkuVO skuVO : skuList) {
                GoodsCouponVO couponVO = new GoodsCouponVO();
                List<CouponDO> skuCouponList = new ArrayList<>();
                skuCouponList.addAll(couponList);
                couponVO.setGoodsId(goodsId);
                couponVO.setSkuId(skuVO.getSkuId());
                //循环部分商品的优惠券
                if (someGoodsCouponList != null) {
                    for (CouponDO couponDO : someGoodsCouponList) {
                        if (couponDO.getScopeId().contains("," + skuVO.getSkuId() + ",")) {
                            //添加到sku可用的优惠券中
                            skuCouponList.add(couponDO);
                        }
                    }
                }
                couponVO.setCouponList(skuCouponList);
                res.add(couponVO);
            }
        }
        return res;
    }

    @Override
    public List<CouponDO> getList(Long sellerId) {

        //查询免费领取的优惠券
        List<CouponDO> couponDOList = this.couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("seller_id", sellerId)
                .lt("start_time", DateUtil.getDateline())
                .gt("end_time", DateUtil.getDateline())
                .eq("type", CouponType.FREE_GET.name()));

        return couponDOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class, RuntimeException.class, Exception.class})
    public CouponDO add(CouponDO coupon) {
        //检测开始时间和结束时间
        PromotionValid.paramValid(coupon.getStartTime(), coupon.getEndTime(), 1, null);
        if (coupon.getLimitNum() < 0) {
            throw new ServiceException(PromotionErrorCode.E406.code(), "限领数量不能为负数");
        }
        //校验每人限领数是都大于发行量
        if (coupon.getLimitNum() > coupon.getCreateNum()) {
            throw new ServiceException(PromotionErrorCode.E405.code(), "限领数量超出发行量");
        }
        if (coupon.getCouponPrice() == null || coupon.getCouponPrice() <= 0) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券面额必须大于0元");
        }
        if (coupon.getCouponThresholdPrice() == null || coupon.getCouponThresholdPrice() <= 0) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券门槛价格必须大于0元");
        }
        //校验优惠券面额是否小于门槛价格
        if (coupon.getCouponPrice() >= coupon.getCouponThresholdPrice()) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券面额必须小于优惠券门槛价格");
        }

        //开始时间取前段+00:00:00 结束时间取前段+23:59:59
        String startStr = DateUtil.toString(coupon.getStartTime(), "yyyy-MM-dd");
        String endStr = DateUtil.toString(coupon.getEndTime(), "yyyy-MM-dd");

        coupon.setStartTime(DateUtil.getDateline(startStr + " 00:00:00"));
        coupon.setEndTime(DateUtil.getDateline(endStr + " 23:59:59", "yyyy-MM-dd hh:mm:ss"));

        this.paramValid(coupon.getStartTime(), coupon.getEndTime());

        coupon.setReceivedNum(0);
        coupon.setUsedNum(0);
        //部分商品和分类的id存储增加,,
        if (CouponUseScope.SOME_GOODS.name().equals(coupon.getUseScope()) || CouponUseScope.CATEGORY.name().equals(coupon.getUseScope())) {
            coupon.setScopeId("," + coupon.getScopeId() + ",");
        }

        this.couponMapper.insert(coupon);

        //启用延时任务创建优惠券脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(coupon.getCouponId());
        promotionScriptMsg.setPromotionName(coupon.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.COUPON);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(coupon.getEndTime());
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.COUPON.name() + "}_" + coupon.getCouponId();
        timeTrigger.add(TimeExecute.COUPON_SCRIPT_EXECUTER, promotionScriptMsg, coupon.getStartTime(), uniqueKey);

        return coupon;
    }

    @Override
    public CouponDO edit(CouponDO coupon, Long id) {

        //校验每人限领数是都大于发行量
        if (coupon.getLimitNum() > coupon.getCreateNum()) {
            throw new ServiceException(PromotionErrorCode.E405.code(), "限领数量超出发行量");
        }
        if (coupon.getCouponPrice() == null || coupon.getCouponPrice() <= 0) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券面额必须大于0元");
        }
        if (coupon.getCouponThresholdPrice() == null || coupon.getCouponThresholdPrice() <= 0) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券门槛价格必须大于0元");
        }
        //校验优惠券面额是否小于门槛价格
        if (coupon.getCouponPrice() >= coupon.getCouponThresholdPrice()) {
            throw new ServiceException(PromotionErrorCode.E409.code(), "优惠券面额必须小于优惠券门槛价格");
        }

        //开始时间取前段+00:00:00 结束时间取前段+23:59:59
        String startStr = DateUtil.toString(coupon.getStartTime(), "yyyy-MM-dd");
        String endStr = DateUtil.toString(coupon.getEndTime(), "yyyy-MM-dd");

        coupon.setStartTime(DateUtil.getDateline(startStr + " 00:00:00"));
        coupon.setEndTime(DateUtil.getDateline(endStr + " 23:59:59", "yyyy-MM-dd hh:mm:ss"));

        this.paramValid(coupon.getStartTime(), coupon.getEndTime());


        //验证开始结束时间
        this.paramValid(coupon.getStartTime(), coupon.getEndTime());
        //验证优惠券活动是不是不是可更改的状态
        this.verifyStatus(id);
        //检测开始时间和结束时间
        PromotionValid.paramValid(coupon.getStartTime(), coupon.getEndTime(), 1, null);
        //获取修改操作之前的优惠券信息
        CouponDO oldCoupon = this.getModel(id);

        coupon.setCouponId(id);
        this.couponMapper.updateById(coupon);

        //启用延时任务创建优惠券脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(id);
        promotionScriptMsg.setPromotionName(coupon.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.COUPON);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(coupon.getEndTime());

        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.COUPON.name() + "}_" + id;
        timeTrigger.edit(TimeExecute.COUPON_SCRIPT_EXECUTER, promotionScriptMsg, oldCoupon.getStartTime(), coupon.getStartTime(), uniqueKey);


        return coupon;
    }

    @Override
    public void delete(Long id) {

        this.verifyStatus(id);

        //获取优惠券信息
        CouponDO couponDO = this.getModel(id);

        this.couponMapper.deleteById(id);

        //删除缓存中的延时任务执行器
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.COUPON.name() + "}_" + id;
        timeTrigger.delete(TimeExecute.COUPON_SCRIPT_EXECUTER, couponDO.getStartTime(), uniqueKey);
        if (couponDO.getEndTime().longValue() < DateUtil.getDateline()) {
            timeTrigger.delete(TimeExecute.COUPON_SCRIPT_EXECUTER, couponDO.getEndTime(), uniqueKey);
        }
    }

    @Override
    public CouponDO getModel(Long id) {

        CouponDO couponDO = this.couponMapper.selectById(id);

        return couponDO;
    }

    @Override
    public void verifyAuth(Long id) {
        CouponDO couponDO = this.getModel(id);

        if (couponDO == null) {
            throw new NoPermissionException("无权操作或者数据不存在");
        }

        if (couponDO.getSellerId() != 0 && !couponDO.getSellerId().equals(UserContext.getSeller().getSellerId())) {
            throw new NoPermissionException("无权操作或者数据不存在");
        }

        if (couponDO.getSellerId() == 0 && AdminUserContext.getAdmin() == null) {
            throw new NoPermissionException("无权操作或者数据不存在");
        }
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void addUsedNum(Long couponId) {
        this.couponMapper.update(new CouponDO(), new UpdateWrapper<CouponDO>()
                .setSql("used_num = used_num+1")
                .eq("coupon_id", couponId));
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void addReceivedNum(Long couponId) {

        this.couponMapper.update(new CouponDO(), new UpdateWrapper<CouponDO>()
                .setSql("received_num = received_num+1")
                .eq("coupon_id", couponId));
    }

    @Override
    public WebPage all(long page, long pageSize, Long sellerId) {
        Long nowTime = DateUtil.getDateline();

        IPage iPage = this.couponMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<CouponDO>()
                .le("start_time", nowTime)
                .gt("end_time", nowTime)
                .eq("type", CouponType.FREE_GET.name())
                .eq(sellerId != null, "seller_id", sellerId)
                .orderByDesc("coupon_id"));

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void editCouponShopName(Long shopId, String shopName) {
        //修改优惠券的店铺名称
        this.couponMapper.update(new CouponDO(), new UpdateWrapper<CouponDO>()
                .set("seller_name", shopName)
                .eq("seller_id", shopId));
    }

    @Override
    public List<CouponDO> getByStatus(Integer status) {
        Seller seller = UserContext.getSeller();
        //获取当前时间
        Long currentTime = DateUtil.getDateline();

        return this.couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("seller_id", seller.getSellerId())
                //如果状态值为1，则添加条件
                .and(status == 1, e -> {
                    //结束时间>=当前时间
                    e.ge("end_time", currentTime)
                            //活动赠送
                            .eq("type", CouponType.ACTIVITY_GIVE.name())
                            //数量-领取数量>0
                            .last(" and create_num-received_num>0");
                })
                //如果状态值为2，则结束时间<当前时间
                .lt(status == 2, "end_time", currentTime));
    }

    /**
     * 验证是否可修改和删除
     *
     * @param id
     */
    private void verifyStatus(Long id) {
        CouponDO couponDO = this.getModel(id);
        long nowTime = DateUtil.getDateline();

        //如果当前时间大于起始时间，小于终止时间，标识活动已经开始了，不可修改和删除。
        if (couponDO.getStartTime().longValue() < nowTime && couponDO.getEndTime().longValue() > nowTime) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动已经开始，不能进行编辑删除操作");
        }

        List<FullDiscountDO> list = fullDiscountMapper.selectList(new QueryWrapper<FullDiscountDO>()
                .eq("is_send_bonus", 1)
                .eq("bonus_id", id)
                .eq("seller_id", couponDO.getSellerId())
                .gt("end_time", DateUtil.getDateline()));

        if (list != null && list.size() != 0) {
            String msg = "";
            for (FullDiscountDO full : list) {
                msg += "【" + full.getTitle() + "】";
            }

            throw new ServiceException(PromotionErrorCode.E400.code(), "当前优惠券参与了促销活动" + msg + "，不能进行编辑删除操作");
        }
    }

    /**
     * 参数验证
     *
     * @param startTime
     * @param endTime
     */
    private void paramValid(Long startTime, Long endTime) {

        long nowTime = DateUtil.getDateline();

        //如果活动起始时间小于现在时间
//        if (startTime.longValue() < nowTime) {
//            throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER, "活动起始时间必须大于当前时间");
//        }

        // 开始时间不能大于结束时间
        if (startTime.longValue() > endTime.longValue()) {
            throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER, "活动起始时间不能大于活动结束时间");
        }
    }

}

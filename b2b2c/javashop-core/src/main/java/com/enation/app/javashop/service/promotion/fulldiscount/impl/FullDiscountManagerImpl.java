package com.enation.app.javashop.service.promotion.fulldiscount.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.fulldiscount.FullDiscountMapper;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.service.promotion.coupon.CouponManager;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountDO;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountManager;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDetailDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import com.enation.app.javashop.service.promotion.tool.impl.AbstractPromotionRuleManagerImpl;
import com.enation.app.javashop.service.promotion.tool.support.PromotionCacheKeys;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 满优惠活动业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 17:34:32
 */
@SuppressWarnings("Duplicates")
@Service
public class FullDiscountManagerImpl extends AbstractPromotionRuleManagerImpl implements FullDiscountManager {

    @Autowired
    private FullDiscountMapper fullDiscountMapper;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private CouponManager couponManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private Cache cache;

    @Autowired
    private TimeTrigger timeTrigger;

    @Override
    public WebPage list(long page, long pageSize, String keywords) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //获取商家店铺ID
        Long sellerId = seller.getSellerId();

        //新建查询条件包装器
        QueryWrapper<FullDiscountDO> wrapper = new QueryWrapper<>();
        //以商家店铺ID为查询条件
        wrapper.eq("seller_id", sellerId);
        //如果查询关键字不为空，则以满减满赠促销活动标题为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(keywords), "title", keywords);
        //以满减满赠促销活动ID倒序排序
        wrapper.orderByDesc("fd_id");
        //获取满减满赠促销活动分页列表数据
        IPage<FullDiscountVO> iPage = fullDiscountMapper.selectFullDiscountVoPage(new Page(page, pageSize), wrapper);

        //获取结果集并循环设置促销活动状态信息
        for (FullDiscountVO fullDiscountVO : iPage.getRecords()) {
            long nowTime = DateUtil.getDateline();
            //当前时间小于活动的开始时间 则为活动未开始
            if (nowTime < fullDiscountVO.getStartTime().longValue()) {
                fullDiscountVO.setStatusText("活动未开始");
                fullDiscountVO.setStatus(PromotionStatusEnum.WAIT.toString());
                //大于活动的开始时间，小于活动的结束时间
            } else if (fullDiscountVO.getStartTime().longValue() < nowTime && nowTime < fullDiscountVO.getEndTime()) {
                fullDiscountVO.setStatusText("正在进行中");
                fullDiscountVO.setStatus(PromotionStatusEnum.UNDERWAY.toString());

            } else {
                fullDiscountVO.setStatusText("活动已结束");
                fullDiscountVO.setStatus(PromotionStatusEnum.END.toString());
            }
        }

        return PageConvert.convert(iPage);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public FullDiscountVO add(FullDiscountVO fullDiscountVO) {
        //检测开始时间和结束时间
        PromotionValid.paramValid(fullDiscountVO.getStartTime(),fullDiscountVO.getEndTime(),1,null);
        this.verifyTime(fullDiscountVO.getStartTime(), fullDiscountVO.getEndTime(), PromotionTypeEnum.FULL_DISCOUNT, null);

        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //是否是全部商品参与
        if (fullDiscountVO.getRangeType() == 1) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            goodsDTO.setGoodsId(-1L);
            goodsDTO.setSkuId(-1L);
            goodsDTO.setGoodsName("全部商品");
            goodsDTO.setThumbnail("");
            goodsDTOList.add(goodsDTO);
            fullDiscountVO.setGoodsList(goodsDTOList);
        }

        this.verifyRule(fullDiscountVO.getGoodsList());

        // 查看是否赠送优惠券，是则判断优惠券的使用时间是否大于活动结束时间
        if (fullDiscountVO.getIsSendBonus() != null && fullDiscountVO.getIsSendBonus() == 1) {
            Long couponId = fullDiscountVO.getBonusId();
            couponManager.getModel(couponId);
            CouponDO coupon = couponManager.getModel(couponId);
            if(coupon.getEndTime()<fullDiscountVO.getEndTime()){
                throw new ServiceException(PromotionErrorCode.E401.code(),"赠送的优惠券有效时间必须大于活动时间");
            }
        }

        //新建满减满赠活动对象
        FullDiscountDO fullDiscountDO = new FullDiscountDO();
        //从VO中复制相关属性值
        BeanUtils.copyProperties(fullDiscountVO, fullDiscountDO);
        //满减满赠活动信息入库
        fullDiscountMapper.insert(fullDiscountDO);

        //获取活动Id
        Long id = fullDiscountDO.getFdId();
        fullDiscountVO.setFdId(id);

        //设置促销信息
        PromotionDetailDTO detailDTO = new PromotionDetailDTO();
        detailDTO.setStartTime(fullDiscountVO.getStartTime());
        detailDTO.setEndTime(fullDiscountVO.getEndTime());
        detailDTO.setActivityId(fullDiscountVO.getFdId());
        detailDTO.setPromotionType(PromotionTypeEnum.FULL_DISCOUNT.name());
        detailDTO.setTitle(fullDiscountVO.getTitle());

        //将活动商品入库
        this.promotionGoodsManager.add(fullDiscountVO.getGoodsList(), detailDTO);
        cache.put(PromotionCacheKeys.getFullDiscountKey(id), fullDiscountDO);

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(id);
        promotionScriptMsg.setPromotionName(fullDiscountDO.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.FULL_DISCOUNT);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(fullDiscountDO.getEndTime());
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.FULL_DISCOUNT.name() + "}_" + id;
        timeTrigger.add(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, promotionScriptMsg, fullDiscountDO.getStartTime(), uniqueKey);

        return fullDiscountVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public FullDiscountVO edit(FullDiscountVO fullDiscountVO, Long id) {
        //验证此活动是否可进行编辑操作
        this.verifyStatus(id);
        //验证活动时间（同一时间只能有一个活动生效）
        this.verifyTime(fullDiscountVO.getStartTime(), fullDiscountVO.getEndTime(), PromotionTypeEnum.FULL_DISCOUNT, id);
        //检测开始时间和结束时间
        PromotionValid.paramValid(fullDiscountVO.getStartTime(),fullDiscountVO.getEndTime(),1,null);
        //获取修改操作之前的满减满赠促销活动信息
        FullDiscountDO oldFullDiscount = this.getModel(id);

        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //是否是全部商品参与
        if (fullDiscountVO.getRangeType() == 1) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            goodsDTO.setGoodsId(-1L);
            goodsDTO.setSkuId(-1L);
            goodsDTO.setGoodsName("全部商品");
            goodsDTO.setThumbnail("");
            goodsDTOList.add(goodsDTO);
            fullDiscountVO.setGoodsList(goodsDTOList);
        }
        //检测活动与活动之间的规则冲突
        this.verifyRule(fullDiscountVO.getGoodsList());

        //获取当前登录的店铺ID
        Seller seller = UserContext.getSeller();
        Long sellerId = seller.getSellerId();
        fullDiscountVO.setSellerId(sellerId);

        //新建满减满赠活动对象
        FullDiscountDO fullDiscountDO = new FullDiscountDO();
        //从VO对象中复制相关属性值
        BeanUtils.copyProperties(fullDiscountVO, fullDiscountDO);
        //设置主键ID
        fullDiscountDO.setFdId(id);
        //修改相关满减满赠信息
        fullDiscountMapper.updateById(fullDiscountDO);

        //删除之前的活动与商品的对照关系
        PromotionDetailDTO detailDTO = new PromotionDetailDTO();
        detailDTO.setStartTime(fullDiscountVO.getStartTime());
        detailDTO.setEndTime(fullDiscountVO.getEndTime());
        detailDTO.setActivityId(fullDiscountVO.getFdId());
        detailDTO.setPromotionType(PromotionTypeEnum.FULL_DISCOUNT.name());
        detailDTO.setTitle(fullDiscountVO.getTitle());

        //将活动商品入库
        this.promotionGoodsManager.edit(fullDiscountVO.getGoodsList(), detailDTO);
        cache.put(PromotionCacheKeys.getFullDiscountKey(fullDiscountVO.getFdId()), fullDiscountDO);

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(id);
        promotionScriptMsg.setPromotionName(fullDiscountDO.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.FULL_DISCOUNT);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(fullDiscountDO.getEndTime());
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.FULL_DISCOUNT.name() + "}_" + id;
        timeTrigger.edit(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, promotionScriptMsg, oldFullDiscount.getStartTime(), fullDiscountDO.getStartTime(), uniqueKey);

        return fullDiscountVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public void delete(Long id) {
        //验证此活动是否可进行编辑操作
        this.verifyStatus(id);
        //获取促销活动详细信息
        FullDiscountDO fullDiscountDO = this.getModel(id);
        //删除满减满赠促销活动信息
        fullDiscountMapper.deleteById(id);
        //删除活动对照表
        this.promotionGoodsManager.delete(id, PromotionTypeEnum.FULL_DISCOUNT.name());
        this.cache.remove(PromotionCacheKeys.getFullDiscountKey(id));

        //删除缓存中的延时任务执行器
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.FULL_DISCOUNT.name() + "}_" + id;
        timeTrigger.delete(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, fullDiscountDO.getStartTime(), uniqueKey);
        if (fullDiscountDO.getEndTime().longValue() < DateUtil.getDateline()) {
            timeTrigger.delete(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, fullDiscountDO.getEndTime(), uniqueKey);
        }
    }


    @Override
    public FullDiscountVO getModel(Long fdId) {
        //先从缓存中获取满减满赠活动信息，如果缓存中的活动信息为空，则从数据库中取出
        FullDiscountDO fullDiscountDO = (FullDiscountDO) this.cache.get(PromotionCacheKeys.getFullDiscountKey(fdId));
        if (fullDiscountDO == null) {
            fullDiscountDO = fullDiscountMapper.selectById(fdId);
        }
        //校验活动信息是否存在
        if (fullDiscountDO == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动不存在");
        }

        //新建活动信息VO对象
        FullDiscountVO fullDiscountVO = new FullDiscountVO();
        //从DO中复制相关属性值
        BeanUtils.copyProperties(fullDiscountDO, fullDiscountVO);

        //获取参与满减满赠活动的商品集合
        List<PromotionGoodsDO> goodsDOList = this.promotionGoodsManager.getPromotionGoods(fdId, PromotionTypeEnum.FULL_DISCOUNT.name());
        //校验是否有商品参与满减满赠活动
        if (goodsDOList.isEmpty()) {
            throw new ServiceException(PromotionErrorCode.E401.code(), "此活动没有商品参与");
        }

        //循环商品集合拿到所有商品的sku_id
        Long[] skuIds = new Long[goodsDOList.size()];
        for (int i = 0; i < goodsDOList.size(); i++) {
            skuIds[i] = goodsDOList.get(i).getSkuId();
        }

        //根据sku_id得到相关商品信息集合（一般是指商品选择器使用的信息）
        List<GoodsSelectLine> goodsSelectLineList = this.goodsClient.querySkus(skuIds);
        List<PromotionGoodsDTO> goodsList = new ArrayList<>();

        //循环信息集合分别复制促销活动相关属性值
        for (GoodsSelectLine goodsSelectLine : goodsSelectLineList) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            BeanUtils.copyProperties(goodsSelectLine, goodsDTO);
            goodsList.add(goodsDTO);
        }

        fullDiscountVO.setGoodsList(goodsList);
        return fullDiscountVO;
    }


    @Override
    public void verifyAuth(Long id) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //根据活动ID获取活动详细信息
        FullDiscountVO fullDiscountVO = this.getModel(id);
        //验证越权操作
        if (fullDiscountVO == null || !seller.getSellerId().equals(fullDiscountVO.getSellerId())) {
            throw new NoPermissionException("无权操作");
        }
    }


    /**
     * 验证此活动是否可进行编辑删除操作<br/>
     * 如有问题则抛出异常
     *
     * @param id 活动id
     */
    private void verifyStatus(Long id) {
        //根据活动ID获取活动详细信息
        FullDiscountVO fullDiscountVO = this.getModel(id);
        //获取当前时间
        long nowTime = DateUtil.getDateline();
        //如果活动起始时间小于现在时间，活动已经开始了。
        if (fullDiscountVO.getStartTime().longValue() < nowTime && fullDiscountVO.getEndTime().longValue() > nowTime) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动已经开始，不能进行编辑删除操作");
        }
    }

}

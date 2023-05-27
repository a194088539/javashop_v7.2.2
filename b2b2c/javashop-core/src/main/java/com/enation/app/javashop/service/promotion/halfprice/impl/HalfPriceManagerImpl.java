package com.enation.app.javashop.service.promotion.halfprice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.HalfPriceMapper;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.halfprice.dos.HalfPriceDO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
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
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 第二件半价业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 19:53:42
 */
@Service
public class HalfPriceManagerImpl extends AbstractPromotionRuleManagerImpl implements HalfPriceManager {


    @Autowired
    private HalfPriceMapper halfPriceMapper;

    @Autowired
    private Cache cache;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TimeTrigger timeTrigger;

    @Override
    public WebPage list(long page, long pageSize, String keywords) {

        // 获取当前登录的店铺ID
        Seller seller = UserContext.getSeller();
        Long sellerId = seller.getSellerId();

        QueryWrapper<HalfPriceVO> wrapper = new QueryWrapper();

        wrapper.eq("seller_id", sellerId);
        if (!StringUtil.isEmpty(keywords)) {
            wrapper.like("title", wrapper);
        }
        wrapper.orderByDesc("hp_id");

        IPage<HalfPriceVO> iPage = halfPriceMapper.selectPageVO(new Page(page, pageSize), wrapper);

        WebPage webPage = PageConvert.convert(iPage);


        List<HalfPriceVO> halfPriceVOList = webPage.getData();
        for (HalfPriceVO halfPriceVO : halfPriceVOList) {
            long nowTime = DateUtil.getDateline();
            //当前时间小于活动的开始时间 则为活动未开始
            if (nowTime < halfPriceVO.getStartTime().longValue()) {
                halfPriceVO.setStatusText("活动未开始");
                halfPriceVO.setStatus(PromotionStatusEnum.WAIT.toString());

                //大于活动的开始时间，小于活动的结束时间
            } else if (halfPriceVO.getStartTime().longValue() < nowTime && nowTime < halfPriceVO.getEndTime()) {
                halfPriceVO.setStatusText("正在进行中");
                halfPriceVO.setStatus(PromotionStatusEnum.UNDERWAY.toString());

            } else {
                halfPriceVO.setStatusText("活动已失效");
                halfPriceVO.setStatus(PromotionStatusEnum.END.toString());
            }
        }

        return webPage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public HalfPriceVO add(HalfPriceVO halfPriceVO) {
        //检测开始时间和结束时间
        PromotionValid.paramValid(halfPriceVO.getStartTime(), halfPriceVO.getEndTime(), 1, null);
        this.verifyTime(halfPriceVO.getStartTime(), halfPriceVO.getEndTime(), PromotionTypeEnum.HALF_PRICE, null);

        //初步形成商品的DTO列表
        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //是否是全部商品参与
        if (halfPriceVO.getRangeType() == 1) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            goodsDTO.setGoodsId(-1L);
            goodsDTO.setSkuId(-1L);
            goodsDTO.setGoodsName("全部商品");
            goodsDTO.setThumbnail("");
            goodsDTOList.add(goodsDTO);
            halfPriceVO.setGoodsList(goodsDTOList);
        }

        this.verifyRule(halfPriceVO.getGoodsList());

        HalfPriceDO halfPriceDO = new HalfPriceDO();
        BeanUtils.copyProperties(halfPriceVO, halfPriceDO);
        this.halfPriceMapper.insert(halfPriceDO);

        Long id = halfPriceDO.getHpId();
        halfPriceVO.setHpId(id);
        PromotionDetailDTO detailDTO = new PromotionDetailDTO();
        detailDTO.setStartTime(halfPriceVO.getStartTime());
        detailDTO.setEndTime(halfPriceVO.getEndTime());
        detailDTO.setActivityId(halfPriceVO.getHpId());
        detailDTO.setPromotionType(PromotionTypeEnum.HALF_PRICE.name());
        detailDTO.setTitle(halfPriceVO.getTitle());

        //将活动商品入库
        this.promotionGoodsManager.add(halfPriceVO.getGoodsList(), detailDTO);

        cache.put(PromotionCacheKeys.getHalfPriceKey(halfPriceVO.getHpId()), halfPriceDO);

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(id);
        promotionScriptMsg.setPromotionName(halfPriceDO.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.HALF_PRICE);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(halfPriceDO.getEndTime());
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.HALF_PRICE.name() + "}_" + id;
        timeTrigger.add(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, promotionScriptMsg, halfPriceDO.getStartTime(), uniqueKey);

        return halfPriceVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public HalfPriceVO edit(HalfPriceVO halfPriceVO, Long id) {

        this.verifyStatus(id);

        this.verifyTime(halfPriceVO.getStartTime(), halfPriceVO.getEndTime(), PromotionTypeEnum.HALF_PRICE, id);
        //检测开始时间和结束时间
        PromotionValid.paramValid(halfPriceVO.getStartTime(), halfPriceVO.getEndTime(), 1, null);
        //获取修改操作之前的第二件半价促销活动信息
        HalfPriceDO oldHalfPrice = this.getFromDB(id);

        //初步形成商品的DTO列表
        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //是否是全部商品参与
        if (halfPriceVO.getRangeType() == 1) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            goodsDTO.setGoodsId(-1L);
            goodsDTO.setSkuId(-1L);
            goodsDTO.setGoodsName("全部商品");
            goodsDTO.setThumbnail("");
            goodsDTOList.add(goodsDTO);
            halfPriceVO.setGoodsList(goodsDTOList);
        }

        this.verifyRule(halfPriceVO.getGoodsList());

        Seller seller = UserContext.getSeller();
        Long sellerId = seller.getSellerId();

        halfPriceVO.setSellerId(sellerId);
        HalfPriceDO halfPriceDO = new HalfPriceDO();
        BeanUtils.copyProperties(halfPriceVO, halfPriceDO);
        halfPriceDO.setHpId(id);
        this.halfPriceMapper.updateById(halfPriceDO);
        PromotionDetailDTO detailDTO = new PromotionDetailDTO();
        detailDTO.setStartTime(halfPriceVO.getStartTime());
        detailDTO.setEndTime(halfPriceVO.getEndTime());
        detailDTO.setActivityId(halfPriceVO.getHpId());
        detailDTO.setPromotionType(PromotionTypeEnum.HALF_PRICE.name());
        detailDTO.setTitle(halfPriceVO.getTitle());

        //将活动商品入库
        this.promotionGoodsManager.edit(halfPriceVO.getGoodsList(), detailDTO);

        cache.put(PromotionCacheKeys.getHalfPriceKey(halfPriceVO.getHpId()), halfPriceDO);

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        promotionScriptMsg.setPromotionId(id);
        promotionScriptMsg.setPromotionName(halfPriceDO.getTitle());
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.HALF_PRICE);
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        promotionScriptMsg.setEndTime(halfPriceDO.getEndTime());
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.HALF_PRICE.name() + "}_" + id;
        timeTrigger.edit(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, promotionScriptMsg, oldHalfPrice.getStartTime(), halfPriceDO.getStartTime(), uniqueKey);

        return halfPriceVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class, ServiceException.class, NoPermissionException.class})
    public void delete(Long id) {

        this.verifyStatus(id);

        //获取第二件半价促销活动信息
        HalfPriceDO halfPriceDO = this.getFromDB(id);
        halfPriceMapper.deleteById(id);
        //删除活动关系对照表
        this.promotionGoodsManager.delete(id, PromotionTypeEnum.HALF_PRICE.name());
        this.cache.remove(PromotionCacheKeys.getHalfPriceKey(id));

        //删除缓存中的延时任务执行器
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.HALF_PRICE.name() + "}_" + id;
        timeTrigger.delete(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, halfPriceDO.getStartTime(), uniqueKey);
        if (halfPriceDO.getEndTime().longValue() < DateUtil.getDateline()) {
            timeTrigger.delete(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, halfPriceDO.getEndTime(), uniqueKey);
        }
    }


    @Override
    public HalfPriceVO getFromDB(Long id) {
        //读取缓存中的活动信息
        HalfPriceDO halfPriceDO = (HalfPriceDO) this.cache.get(PromotionCacheKeys.getHalfPriceKey(id));
        //如果为空从数据库中读取
        if (halfPriceDO == null) {

            halfPriceDO = this.halfPriceMapper.selectById(id);
        }
        //如果从缓存和数据库读取都是空，则抛出异常
        if (halfPriceDO == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动不存在");
        }

        HalfPriceVO halfPriceVO = new HalfPriceVO();
        BeanUtils.copyProperties(halfPriceDO, halfPriceVO);

        //读取此活动参与的商品
        List<PromotionGoodsDO> goodsDOList = this.promotionGoodsManager.getPromotionGoods(id, PromotionTypeEnum.HALF_PRICE.name());
        Long[] skuIds = new Long[goodsDOList.size()];
        for (int i = 0; i < goodsDOList.size(); i++) {
            skuIds[i] = goodsDOList.get(i).getSkuId();
        }

        //读取商品的信息
        List<GoodsSelectLine> goodsSelectLineList = this.goodsClient.querySkus(skuIds);
        List<PromotionGoodsDTO> goodsList = new ArrayList<>();

        for (GoodsSelectLine goodsSelectLine : goodsSelectLineList) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            BeanUtil.copyProperties(goodsSelectLine, goodsDTO);
            goodsList.add(goodsDTO);
        }

        halfPriceVO.setGoodsList(goodsList);

        return halfPriceVO;
    }

    @Override
    public void verifyAuth(Long id) {
        Seller seller = UserContext.getSeller();
        HalfPriceVO halfPriceVO = this.getFromDB(id);

        //验证越权操作
        if (halfPriceVO == null || !seller.getSellerId().equals(halfPriceVO.getSellerId())) {
            throw new NoPermissionException("无权操作");
        }
    }


    /**
     * 验证此活动是否可进行编辑删除操作<br/>
     * 如有问题则抛出异常
     *
     * @param halfPriceId 活动id
     */
    private void verifyStatus(Long halfPriceId) {
        HalfPriceVO halfPriceVO = this.getFromDB(halfPriceId);
        long nowTime = DateUtil.getDateline();

        //如果活动起始时间小于现在时间，活动已经开始了。
        if (halfPriceVO.getStartTime().longValue() < nowTime && halfPriceVO.getEndTime().longValue() > nowTime) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动已经开始，不能进行编辑删除操作");
        }

    }

}

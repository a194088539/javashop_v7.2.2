package com.enation.app.javashop.service.promotion.seckill.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillApplyMapper;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillMapper;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillDO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillRangeDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillAuditParam;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.model.promotion.seckill.enums.SeckillGoodsApplyStatusEnum;
import com.enation.app.javashop.model.promotion.seckill.enums.SeckillStatusEnum;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillGoodsVO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillVO;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillRangeManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillScriptManager;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDetailDTO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionPriceDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import com.enation.app.javashop.service.promotion.tool.impl.AbstractPromotionRuleManagerImpl;
import com.enation.app.javashop.service.promotion.tool.support.PromotionCacheKeys;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 限时抢购入库业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 10:32:36
 */
@Service
public class SeckillManagerImpl extends AbstractPromotionRuleManagerImpl implements SeckillManager {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SeckillApplyMapper seckillApplyMapper;

    @Autowired
    private SeckillRangeManager seckillRangeManager;

    @Autowired
    private SeckillGoodsManager seckillGoodsManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private Cache cache;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private SeckillScriptManager seckillScriptManager;

    @Override
    public WebPage list(SeckillQueryParam param) {

        long toDayStartTime = DateUtil.startOfTodDay();
        long toDayEndTime = DateUtil.endOfTodDay();

        IPage iPage = seckillMapper.selectCustomPage(new Page(param.getPageNo(), param.getPageSize()), param, toDayStartTime, toDayEndTime);

        List<SeckillVO> seckillVOList = new ArrayList<>();
        List<SeckillDO> seckillDOList = iPage.getRecords();
        for (SeckillDO seckillDO : seckillDOList) {

            List<Integer> rangeList = new ArrayList<>();
            List<SeckillRangeDO> rangeDOList = this.seckillRangeManager.getList(seckillDO.getSeckillId());
            for (SeckillRangeDO rangeDO : rangeDOList) {
                rangeList.add(rangeDO.getRangeTime());
            }

            SeckillVO seckillVO = new SeckillVO();
            BeanUtils.copyProperties(seckillDO, seckillVO);

            if (seckillVO.getSeckillStatus() != null) {
                SeckillStatusEnum statusEnum = SeckillStatusEnum.valueOf(seckillVO.getSeckillStatus());
                //如果状态是已发布状态，则判断该活动是否已开始或者已结束
                seckillVO.setSeckillStatusText(statusEnum.description());
                if (SeckillStatusEnum.RELEASE.equals(statusEnum)) {

                    //活动开始时间
                    long startDay = seckillDO.getStartDay();

                    if (DateUtil.startOfTodDay() <= startDay && DateUtil.endOfTodDay() > startDay) {
                        seckillVO.setSeckillStatusText("已开启");
                    } else if (startDay < DateUtil.endOfTodDay()) {
                        seckillVO.setSeckillStatusText("已关闭");
                    }

                }
            }

            seckillVO.setRangeList(rangeList);
            seckillVOList.add(seckillVO);
        }
        iPage.setRecords(seckillVOList);
        return PageConvert.convert(iPage);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class, RuntimeException.class})
    public SeckillVO add(SeckillVO seckill) {

        List<SeckillDO> list = new QueryChainWrapper<>(seckillMapper)
                //按活动名称查询
                .eq("seckill_name", seckill.getSeckillName())
                //列表查询
                .list();

        if (list.size() > 0) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动名称重复");
        }

        String date = DateUtil.toString(seckill.getStartDay(), "yyyy-MM-dd");
        long startTime = DateUtil.getDateline(date + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        long endTime = DateUtil.getDateline(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        this.verifyTime(startTime, endTime, PromotionTypeEnum.SECKILL, null);

        SeckillDO seckillDO = new SeckillDO();
        seckillDO.setDeleteStatus(DeleteStatusEnum.NORMAL.value());
        BeanUtils.copyProperties(seckill, seckillDO);
        seckillMapper.insert(seckillDO);

        Long id = seckillDO.getSeckillId();
        seckill.setSeckillId(id);

        this.seckillRangeManager.addList(seckill.getRangeList(), id);

        //开启延时任务执行器
        openTimeExecuter(seckill, endTime, id);

        return seckill;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class})
    public SeckillVO edit(SeckillVO seckill, Long id) {

        List<SeckillDO> list = new QueryChainWrapper<>(seckillMapper)
                //拼接活动名称查询条件
                .eq("seckill_name", seckill.getSeckillName())
                //拼接活动id查询条件
                .eq("seckill_id", id)
                //列表查询
                .list();

        if (list.size() > 0) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动名称重复");
        }

        String date = DateUtil.toString(seckill.getStartDay(), "yyyy-MM-dd");
        long startTime = DateUtil.getDateline(date + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
        long endTime = DateUtil.getDateline(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        PromotionValid.paramValid(startTime,endTime,1,null);
        this.verifyTime(startTime, endTime, PromotionTypeEnum.SECKILL, id);

        SeckillDO seckillDO = new SeckillDO();
        BeanUtils.copyProperties(seckill, seckillDO);

        seckillDO.setSeckillId(id);
        seckillMapper.updateById(seckillDO);

        this.seckillRangeManager.addList(seckill.getRangeList(), id);

        //开启延时任务执行器
        openTimeExecuter(seckill, endTime, id);

        return seckill;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class})
    public void delete(Long id) {

        SeckillVO seckill = this.getModel(id);
        if (seckill == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动不存在");
        }

        //编辑中和已经结束的活动均可以删除
        if (SeckillStatusEnum.EDITING.name().equals(seckill.getSeckillStatus()) || seckill.getStartDay().longValue() < DateUtil.startOfTodDay()) {
            seckillMapper.delete(new QueryWrapper<SeckillDO>()
                    //按活动id删除
                    .eq("seckill_id", id)
            );
            //删除限时抢购商品
            this.seckillGoodsManager.deleteBySeckillId(id);
            this.promotionGoodsManager.delete(id, PromotionTypeEnum.SECKILL.name());
        } else {
            throw new ServiceException(PromotionErrorCode.E400.code(), "该活动不是能删除的状态");
        }

    }

    @Override
    public SeckillVO getModel(Long id) {

        SeckillDO seckillDO = seckillMapper.selectById(id);

        if (seckillDO == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动不存在");
        }
        SeckillVO seckillVO = new SeckillVO();
        BeanUtils.copyProperties(seckillDO, seckillVO);

        List<Integer> rangeList = new ArrayList<>();
        List<SeckillRangeDO> rangeDOList = this.seckillRangeManager.getList(id);
        for (SeckillRangeDO rangeDO : rangeDOList) {
            rangeList.add(rangeDO.getRangeTime());
        }
        seckillVO.setRangeList(rangeList);
        return seckillVO;
    }


    @Override
    public SeckillGoodsVO getSeckillGoods(Long goodsId) {

        Map<Integer, List<SeckillGoodsVO>> map = this.seckillGoodsManager.getSeckillGoodsList();
        SeckillGoodsVO goodsVO = null;
        for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {
            List<SeckillGoodsVO> list = entry.getValue();

            for (SeckillGoodsVO seckillGoods : list) {
                if (seckillGoods.getGoodsId().equals(goodsId) ) {
                    goodsVO = new SeckillGoodsVO(seckillGoods, entry.getKey());
                }
            }
        }
        return goodsVO;
    }

    @Override
    public SeckillGoodsVO getSeckillSku(Long goodsId, Long skuId) {
        Map<Integer, List<SeckillGoodsVO>> map = this.seckillGoodsManager.getSeckillGoodsList();
        SeckillGoodsVO goodsVO = null;
        for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {
            List<SeckillGoodsVO> list = entry.getValue();

            for (SeckillGoodsVO seckillGoods : list) {
                if (seckillGoods.getGoodsId().equals(goodsId) && seckillGoods.getSkuId().equals(skuId)) {
                    goodsVO = new SeckillGoodsVO(seckillGoods, entry.getKey());
                }
            }
        }
        return goodsVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class, RuntimeException.class})
    public void batchAuditGoods(SeckillAuditParam param) {
        if (param.getApplyIds() == null || param.getApplyIds().length == 0) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "请选择要审核的商品");
        }

        if (StringUtil.isEmpty(param.getStatus())) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "审核状态值不正确");
        }

        //状态值不正确
        SeckillGoodsApplyStatusEnum applyStatusEnum = SeckillGoodsApplyStatusEnum.valueOf(param.getStatus());

        //驳回，原因必填
        if (applyStatusEnum.equals(SeckillGoodsApplyStatusEnum.FAIL)) {
            if (StringUtil.isEmpty(param.getFailReason())) {
                throw new ServiceException(PromotionErrorCode.E400.code(), "驳回原因必填");
            }

            if (param.getFailReason().length() > 500){
                throw new ServiceException(PromotionErrorCode.E400.code(), "驳回原因长度不能超过500个字符");
            }
        }

        //审核状态 true：审核通过，false：审核未通过
        boolean auditStatus = SeckillGoodsApplyStatusEnum.PASS.equals(applyStatusEnum);
        //参与限时抢购促销活动并且已被平台审核通过的商品集合
        List<SeckillApplyDO> goodsList = new ArrayList<>();
        //审核通过的限时抢购商品集合
        List<PromotionGoodsDO> promotionGoodsDOS = new ArrayList<>();
        Long actId = 0L;
        //批量审核
        for (Long applyId : param.getApplyIds()) {

            SeckillApplyDO apply = new QueryChainWrapper<>(seckillApplyMapper)
                    //拼接活动id查询条件
                    .eq("apply_id", applyId)
                    //拼接秒杀活动商品的申请状态查询条件
                    .eq("status", SeckillGoodsApplyStatusEnum.APPLY.name())
                    //查询单个对象
                    .one();
            //申请不存在
            if (apply == null) {
                throw new ServiceException(PromotionErrorCode.E400.code(), "商品【"+apply.getGoodsName()+"】不是可以审核的状态");
            }
            if(actId == 0){
                actId = apply.getSeckillId();
            }

            apply.setStatus(applyStatusEnum.name());
            apply.setFailReason(param.getFailReason());

            new UpdateChainWrapper<>(seckillApplyMapper)
                    //按活动id修改
                    .eq("apply_id", applyId)
                    //提交修改
                    .update(apply);

            //查询商品
            CacheGoods goods = goodsClient.getFromCache(apply.getGoodsId());
            //将审核通过的商品，存储到活动商品表和缓存中
            if (auditStatus) {
                //将审核通过的商品放入集合中
                goodsList.add(apply);

                //促销商品表
                PromotionGoodsDO promotion = new PromotionGoodsDO();
                promotion.setTitle("限时抢购");
                promotion.setGoodsId(apply.getGoodsId());
                promotion.setSkuId(apply.getSkuId());
                promotion.setPromotionType(PromotionTypeEnum.SECKILL.name());
                promotion.setActivityId(apply.getSeckillId());
                promotion.setNum(apply.getSoldQuantity());
                promotion.setPrice(apply.getPrice());
                promotion.setSellerId(goods.getSellerId());
                //商品活动的开始时间为当前商品的参加时间段
                int timeLine = apply.getTimeLine();
                String date = DateUtil.toString(apply.getStartDay(), "yyyy-MM-dd");
                long startTime = DateUtil.getDateline(date + " " + timeLine + ":00:00", "yyyy-MM-dd HH:mm:ss");
                long endTime = DateUtil.getDateline(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");

                promotion.setStartTime(startTime);
                promotion.setEndTime(endTime);
                promotionGoodsDOS.add(promotion);


                //从缓存读取限时抢购的活动的商品
                String redisKey = getRedisKey(apply.getStartDay());
                Map<Integer, List<SeckillGoodsVO>> map = this.cache.getHash(redisKey);
                //如果redis中有当前审核商品参与的限时抢购活动商品信息，就删除掉
                if (map != null && !map.isEmpty()) {
                    this.cache.remove(redisKey);
                }

                //设置延迟加载任务，到活动开始时间后将搜索引擎中的优惠价格设置为0
                PromotionPriceDTO promotionPriceDTO = new PromotionPriceDTO();
                promotionPriceDTO.setGoodsId(apply.getGoodsId());
                promotionPriceDTO.setPrice(apply.getPrice());
                timeTrigger.add(TimeExecute.PROMOTION_EXECUTER, promotionPriceDTO, startTime, null);
                //此活动结束后将索引的优惠价格重置为0
                promotionPriceDTO.setPrice(0.0);
                timeTrigger.add(TimeExecute.PROMOTION_EXECUTER, promotionPriceDTO, endTime, null);
            }
        }

        //如果当前是商品审核通过的操作
        if (auditStatus) {
            SeckillVO seckillVO = this.getModel(actId);
            if(seckillVO != null){
                String date = DateUtil.toString(seckillVO.getStartDay(), "yyyy-MM-dd");
                long startTime = DateUtil.getDateline(date + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
                long endTime = DateUtil.getDateline(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
                //活动信息DTO
                PromotionDetailDTO detailDTO = new PromotionDetailDTO();
                detailDTO.setActivityId(seckillVO.getSeckillId());
                detailDTO.setStartTime(startTime);
                detailDTO.setEndTime(endTime);
                detailDTO.setPromotionType(PromotionTypeEnum.SECKILL.name());
                detailDTO.setTitle("限时抢购");
                this.promotionGoodsManager.addAndCheck(promotionGoodsDOS,detailDTO);
            }

            //创建审核通过的商品限时抢购促销活动脚本信息
            this.seckillScriptManager.createCacheScript(goodsList.get(0).getSeckillId(), goodsList);
        }
    }

    @Override
    public void sellerApply(Long sellerId, Long seckillId) {
        SeckillDO seckillDO = this.getModel(seckillId);
        String sellerIds;
        if (!StringUtil.isEmpty(seckillDO.getSellerIds())) {
            sellerIds = seckillDO.getSellerIds() + sellerId + ",";
        } else {
            sellerIds = sellerId + ",";
        }

        new UpdateChainWrapper<>(seckillMapper)
                //设置商家id
                .set("seller_ids", sellerIds)
                //按活动id修改
                .eq("seckill_id", seckillId)
                //提交修改
                .update();

    }

    @Override
    public void close(Long id) {

        SeckillVO seckill = this.getModel(id);
        if (seckill == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动不存在");
        }

        String statusEnum = seckill.getSeckillStatus();
        if (SeckillStatusEnum.RELEASE.name().equals(statusEnum)) {

            //活动开始时间
            long startDay = seckill.getStartDay();

            //已开启状态
            if (DateUtil.startOfTodDay() < startDay && DateUtil.endOfTodDay() > startDay) {
                //此时可以暂停

            }
        }

    }

    /**
     * 获取限时抢购key
     * @param dateline
     * @return
     */
    private String getRedisKey(long dateline) {
        return PromotionCacheKeys.getSeckillKey(DateUtil.toString(dateline, "yyyyMMdd"));
    }

    /**
     * 开启延时任务执行器
     * @param seckill 限时抢购信息
     * @param endTime 活动结束时间
     * @param id 限时抢购活动ID
     */
    private void openTimeExecuter(SeckillVO seckill, long endTime, Long id) {
        //如果活动状态是已发布
        if (SeckillStatusEnum.RELEASE.value().equals(seckill.getSeckillStatus())) {
            //启用延时任务,限时抢购促销活动结束时删除脚本信息
            PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
            promotionScriptMsg.setPromotionId(id);
            promotionScriptMsg.setPromotionName(seckill.getSeckillName());
            promotionScriptMsg.setPromotionType(PromotionTypeEnum.SECKILL);
            promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.DELETE);
            promotionScriptMsg.setEndTime(endTime);
            String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.SECKILL.name() + "}_" + id;
            timeTrigger.add(TimeExecute.SECKILL_SCRIPT_EXECUTER, promotionScriptMsg, endTime, uniqueKey);
        }
    }

}

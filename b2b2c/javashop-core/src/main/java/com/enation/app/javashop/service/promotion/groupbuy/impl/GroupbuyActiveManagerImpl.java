package com.enation.app.javashop.service.promotion.groupbuy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.PromotionGoodsMapper;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyActiveMapper;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyActiveDO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.enums.GroupBuyGoodsStatusEnum;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyActiveVO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyAuditParam;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyQueryParam;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyGoodsManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyActiveManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyScriptManager;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 团购活动表业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 11:52:14
 */
@SuppressWarnings("Duplicates")
@Service
public class GroupbuyActiveManagerImpl extends AbstractPromotionRuleManagerImpl implements GroupbuyActiveManager {

    @Autowired
    private GroupbuyActiveMapper groupbuyActiveMapper;

    @Autowired
    private PromotionGoodsMapper promotionGoodsMapper;

    @Autowired
    private GroupbuyGoodsManager groupbuyGoodsManager;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private Cache cache;

    @Autowired
    private GroupbuyScriptManager groupbuyScriptManager;

    @Override
    public WebPage list(GroupbuyQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyActiveDO> wrapper = new QueryWrapper<>();
        //以删除状态是正常状态查询
        wrapper.eq("delete_status", DeleteStatusEnum.NORMAL.value());
        //如果活动名称不为空，以活动名称为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getActName()), "act_name", param.getActName());

        //获取当前时间
        long nowTime = DateUtil.getDateline();
        String ALL = "ALL";
        String NOT_STARTED = "NOT_STARTED";
        String STARTED = "STARTED";
        String OVER = "OVER";

        //如果活动状态不为空并且不是按照全部状态进行查询
        if (StringUtil.notEmpty(param.getActStatus()) && !ALL.equals(param.getActStatus())) {
            //如果查询活动状态是未开始，那么按活动开始时间大于当前时间进行查询
            wrapper.gt(NOT_STARTED.equals(param.getActStatus()), "start_time", nowTime);
            //如果查询活动状态是进行中，那么按活动开始时间小于等于当前时间并且结束时间大于等于当前时间进行查询
            wrapper.le(STARTED.equals(param.getActStatus()), "start_time", nowTime).ge(STARTED.equals(param.getActStatus()), "end_time", nowTime);
            //如果查询活动状态是已结束，那么按活动结束时间小于当前时间进行查询
            wrapper.lt(OVER.equals(param.getActStatus()), "end_time", nowTime);
        }

        //如果查询活动时间范围开始时间不为空也不等于0，那么按照活动开始时间大于等于这个时间进行查询
        wrapper.ge(param.getStartTime() != null && param.getStartTime() != 0, "start_time", param.getStartTime());
        //如果查询活动时间范围结束时间不为空也不等于0，那么按照活动结束时间小于等于这个时间进行查询
        wrapper.le(param.getEndTime() != null && param.getEndTime() != 0, "end_time", param.getEndTime());

        //按活动创建时间倒序查询
        wrapper.orderByDesc("add_time");
        //获取团购信息分页列表集合
        IPage<GroupbuyActiveVO> iPage = groupbuyActiveMapper.selectPageVo(new Page(param.getPage(), param.getPageSize()), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class})
    public GroupbuyActiveDO add(GroupbuyActiveDO groupbuyActive) {
        //参数验证 1、活动起始时间必须大于当前时间 2、活动开始时间是否大于活动结束时间
        PromotionValid.paramValid(groupbuyActive.getStartTime(), groupbuyActive.getEndTime(), 1, null);
        //验证活动时间 同一时间只能有一个活动生效
        this.verifyTime(groupbuyActive.getStartTime(), groupbuyActive.getEndTime(), PromotionTypeEnum.GROUPBUY, null);
        //验证活动名称是否重复
        this.verifyName(groupbuyActive.getActName(), false, 0L);

        //设置添加时间为当前时间
        groupbuyActive.setAddTime(DateUtil.getDateline());
        //设置删除状态为正常未删除
        groupbuyActive.setDeleteStatus(DeleteStatusEnum.NORMAL.value());
        //团购活动入库操作
        groupbuyActiveMapper.insert(groupbuyActive);
        //获取团购活动主键ID
        Long id = groupbuyActive.getActId();

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        //设置活动ID
        promotionScriptMsg.setPromotionId(id);
        //设置活动名称
        promotionScriptMsg.setPromotionName(groupbuyActive.getActName());
        //设置活动类型
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.GROUPBUY);
        //设置脚本操作类型为创建脚本
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        //设置活动结束时间
        promotionScriptMsg.setEndTime(groupbuyActive.getEndTime());
        //创建延时任务key值
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.GROUPBUY.name() + "}_" + id;
        //发送消息创建延时任务
        timeTrigger.add(TimeExecute.GROUPBUY_SCRIPT_EXECUTER, promotionScriptMsg, groupbuyActive.getStartTime(), uniqueKey);

        return groupbuyActive;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class})
    public GroupbuyActiveDO edit(GroupbuyActiveDO groupbuyActive, Long id) {
        //验证活动时间 同一时间只能有一个活动生效
        this.verifyTime(groupbuyActive.getStartTime(), groupbuyActive.getEndTime(), PromotionTypeEnum.GROUPBUY, id);
        //验证活动名称是否重复
        this.verifyName(groupbuyActive.getActName(), true, id);
        //验证操作权限
        this.verifyAuth(id);
        //检测开始时间和结束时间
        PromotionValid.paramValid(groupbuyActive.getStartTime(), groupbuyActive.getEndTime(), 1, null);
        //获取修改之前的团购活动信息
        GroupbuyActiveDO oldGroupbuyActive = this.getModel(id);
        //设置主键ID
        groupbuyActive.setActId(id);
        //根据ID修改团购活动信息
        groupbuyActiveMapper.updateById(groupbuyActive);

        //新建修改条件包装器
        UpdateWrapper<PromotionGoodsDO> wrapper = new UpdateWrapper<>();
        //修改促销商品表中相关活动的开始时间和结束时间
        wrapper.set("start_time", groupbuyActive.getStartTime()).set("end_time", groupbuyActive.getEndTime());
        //以促销活动ID和促销活动类型为修改条件
        wrapper.eq("activity_id", id).eq("promotion_type", PromotionTypeEnum.GROUPBUY.name());
        //修改促销商品表中参与本次团购活动的商品的开始和结束时间(也就是团购表和促销商品表中的活动开始和结束时间要保持一致)
        promotionGoodsMapper.update(new PromotionGoodsDO(), wrapper);

        //启用延时任务创建促销活动脚本信息
        PromotionScriptMsg promotionScriptMsg = new PromotionScriptMsg();
        //设置活动ID
        promotionScriptMsg.setPromotionId(id);
        //设置活动名称
        promotionScriptMsg.setPromotionName(groupbuyActive.getActName());
        //设置活动类型
        promotionScriptMsg.setPromotionType(PromotionTypeEnum.GROUPBUY);
        //设置脚本操作类型为创建脚本
        promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.CREATE);
        //设置活动结束时间
        promotionScriptMsg.setEndTime(groupbuyActive.getEndTime());
        //创建延时任务key值
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.GROUPBUY.name() + "}_" + id;
        //发送消息创建延时任务
        timeTrigger.edit(TimeExecute.GROUPBUY_SCRIPT_EXECUTER, promotionScriptMsg, oldGroupbuyActive.getStartTime(), groupbuyActive.getStartTime(), uniqueKey);

        return groupbuyActive;
    }

    @Override
    public void delete(Long id, String deleteReason, String operaName) {
        //权限验证
        this.verifyAuth(id);
        //获取团购活动信息
        GroupbuyActiveDO groupbuyActiveDO = this.getModel(id);

        //新建修改条件包装器
        UpdateWrapper<GroupbuyActiveDO> wrapper = new UpdateWrapper<>();
        //修改团购活动状态为已删除，并且修改删除原因、删除时间和操作人
        wrapper.set("delete_status", DeleteStatusEnum.DELETED.value()).set("delete_reason", deleteReason).set("delete_time", DateUtil.getDateline()).set("delete_name", operaName);
        //以活动ID为删除条件
        wrapper.eq("act_id", id);
        //删除团购活动信息（也就是将状态改为已删除）
        groupbuyActiveMapper.update(new GroupbuyActiveDO(), wrapper);

        //删除团购商品（这里清空的是es_promotion_goods表中的团购商品，对es_groupbuy_active表的团购商品没影响）
        this.promotionGoodsManager.delete(id, PromotionTypeEnum.GROUPBUY.name());

        //删除缓存中的延时任务执行器
        String uniqueKey = "{TIME_TRIGGER_" + PromotionTypeEnum.GROUPBUY.name() + "}_" + id;
        timeTrigger.delete(TimeExecute.GROUPBUY_SCRIPT_EXECUTER, groupbuyActiveDO.getStartTime(), uniqueKey);
        if (groupbuyActiveDO.getEndTime().longValue() < DateUtil.getDateline()) {
            timeTrigger.delete(TimeExecute.GROUPBUY_SCRIPT_EXECUTER, groupbuyActiveDO.getEndTime(), uniqueKey);
        }

    }

    @Override
    public GroupbuyActiveDO getModel(Long id) {
        //根据团购活动ID查询团购活动信息并返回
        return groupbuyActiveMapper.selectById(id);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void batchAuditGoods(GroupbuyAuditParam param) {
        //校验活动id
        if (param.getActId() == null || param.getActId() == 0) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参数活动ID不正确");
        }
        //校验活动商品
        if (param.getGbIds() == null || param.getGbIds().length == 0) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "请选择要审核的商品");
        }
        //校验审核状态
        if (param.getStatus() == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参数审核状态值不能为空");
        }
        //校验参数审核状态值是否服务规范
        if (param.getStatus().intValue() != 1 && param.getStatus().intValue() != 2) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参数审核状态值不正确");
        }

        //获取团购活动信息
        GroupbuyActiveDO activeDO = this.getModel(param.getActId());
        //审核状态 true：审核通过，false：审核未通过
        boolean auditStatus = param.getStatus().intValue() == GroupBuyGoodsStatusEnum.APPROVED.status();
        //参与团购促销活动并且已被平台审核通过的商品集合
        List<PromotionGoodsDO> goodsList = new ArrayList<>();
        //循环团购商品ID集合
        for (Long gbId : param.getGbIds()) {
            //读取团购商品信息并校验
            GroupbuyGoodsDO goodsDO = this.groupbuyGoodsManager.getModel(gbId);
            if (goodsDO == null) {
                throw new ServiceException(PromotionErrorCode.E400.code(), "商品【" + goodsDO.getGoodsName() + "】不存在");
            }
            if (goodsDO.getGbStatus().intValue() != 0) {
                throw new ServiceException(PromotionErrorCode.E400.code(), "商品【" + goodsDO.getGoodsName() + "】不是可以审核的状态");
            }

            //修改状态
            this.groupbuyGoodsManager.updateStatus(gbId, param.getStatus());

            //如果通过审核
            if (auditStatus) {
                //新建修改条件包装器
                UpdateWrapper<GroupbuyActiveDO> wrapper = new UpdateWrapper<>();
                //以团购活动ID为修改条件，修改参与团购商品数量+1
                wrapper.setSql("goods_num=goods_num+1").eq("act_id", param.getActId());
                //修改已参与团购活动的商品数量
                groupbuyActiveMapper.update(new GroupbuyActiveDO(), wrapper);

                //商品集合
                List<PromotionGoodsDO> goodsDTOList = new ArrayList<>();
                //初始化促销活动商品数据
                PromotionGoodsDO promotion = new PromotionGoodsDO();
                //设置活动名称
                promotion.setTitle(activeDO.getActName());
                //设置商品ID
                promotion.setGoodsId(goodsDO.getGoodsId());
                //设置商品sku
                promotion.setSkuId(goodsDO.getSkuId());
                //设置促销活动类型为团购活动
                promotion.setPromotionType(PromotionTypeEnum.GROUPBUY.name());
                //设置活动ID
                promotion.setActivityId(activeDO.getActId());
                //设置参与促销活动的商品数量
                promotion.setNum(activeDO.getGoodsNum());
                //设置商品价格
                promotion.setPrice(goodsDO.getPrice());
                //设置商家ID
                promotion.setSellerId(goodsDO.getSellerId());
                //设置活动开始时间
                promotion.setStartTime(activeDO.getStartTime());
                //设置活动结束时间
                promotion.setEndTime(activeDO.getEndTime());
                goodsDTOList.add(promotion);

                //初始化活动信息DTO
                PromotionDetailDTO detailDTO = new PromotionDetailDTO();
                //设置活动ID
                detailDTO.setActivityId(param.getActId());
                //设置活动开始时间
                detailDTO.setStartTime(activeDO.getStartTime());
                //设置活动结束时间
                detailDTO.setEndTime(activeDO.getEndTime());
                //设置促销活动类型为团购活动
                detailDTO.setPromotionType(PromotionTypeEnum.GROUPBUY.name());
                //设置活动名称
                detailDTO.setTitle(activeDO.getActName());

                //入库到活动商品对照表
                this.promotionGoodsManager.addAndCheck(goodsDTOList, detailDTO);
                this.cache.put(PromotionCacheKeys.getGroupbuyKey(param.getActId()), goodsDO);

                //将此商品加入延迟加载队列，到指定的时间将索引价格变成最新的优惠价格
                PromotionPriceDTO promotionPriceDTO = new PromotionPriceDTO();
                promotionPriceDTO.setGoodsId(goodsDO.getGoodsId());
                promotionPriceDTO.setPrice(goodsDO.getPrice());
                //检测开始时间和结束时间
                PromotionValid.paramValid(detailDTO.getStartTime(), detailDTO.getEndTime(), 1, null);
                timeTrigger.add(TimeExecute.PROMOTION_EXECUTER, promotionPriceDTO, activeDO.getStartTime(), null);
                //此活动结束后将索引的优惠价格重置为0
                promotionPriceDTO.setPrice(0.0);
                timeTrigger.add(TimeExecute.PROMOTION_EXECUTER, promotionPriceDTO, activeDO.getEndTime(), null);

                //构建要生成脚本的促销商品数据集合
                PromotionGoodsDO promotionGoodsDO = new PromotionGoodsDO();
                promotionGoodsDO.setSkuId(promotion.getSkuId());
                promotionGoodsDO.setPrice(goodsDO.getPrice());
                goodsList.add(promotionGoodsDO);

            }
        }

        //获取当前时间
        long currTime = DateUtil.getDateline();
        //活动是否在进行中
        boolean activeStatus = activeDO.getStartTime().longValue() <= currTime && activeDO.getEndTime().longValue() >= currTime;
        //如果当前团购活动正在进行中并且是商品审核通过的操作
        if (activeStatus && auditStatus) {
            //先删除已存在的脚本信息
            this.groupbuyScriptManager.deleteCacheScript(param.getActId(), goodsList);
            //创建审核通过的商品团购促销活动脚本信息
            this.groupbuyScriptManager.createCacheScript(param.getActId(), goodsList);

        }
    }

    @Override
    public List<GroupbuyActiveDO> getActiveList() {
        //获取当前时间
        long nowTime = DateUtil.getDateline();
        //新建查询条件包装器
        QueryWrapper<GroupbuyActiveDO> wrapper = new QueryWrapper<>();
        //以团购报名截止时间大于等于当前时间为查询条件
        wrapper.ge("join_end_time", nowTime);
        //以删除状态为未删除为查询条件
        wrapper.eq("delete_status", DeleteStatusEnum.NORMAL.value());
        //按活动添加时间倒序查询
        wrapper.orderByDesc("add_time");
        //查询团购活动集合数据并返回
        return groupbuyActiveMapper.selectList(wrapper);
    }

    @Override
    public void verifyAuth(Long id) {
        //根据活动ID获取团购活动信息
        GroupbuyActiveDO activeDO = this.getModel(id);
        //获取当前时间
        long nowTime = DateUtil.getDateline();
        //如果活动起始时间小于现在时间，活动已经开始了。
        if (activeDO.getStartTime().longValue() < nowTime && activeDO.getEndTime().longValue() > nowTime) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "活动已经开始，不能进行编辑删除操作");
        }
    }

}

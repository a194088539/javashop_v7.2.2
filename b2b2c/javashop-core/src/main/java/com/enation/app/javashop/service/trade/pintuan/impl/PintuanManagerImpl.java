package com.enation.app.javashop.service.trade.pintuan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanMapper;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanOrderMapper;
import com.enation.app.javashop.model.base.message.PintuanChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.promotion.pintuan.*;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanOrderManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanScriptManager;
import com.enation.app.javashop.service.trade.pintuan.exception.PintuanErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 拼团业务类
 *
 * @author admin
 * @version vv1.0.0
 * @since vv7.1.0
 * 2019-01-21 15:17:57
 */
@Service
public class PintuanManagerImpl implements PintuanManager {

    /**
     * 拼团促销前缀
     */
    private static final String TRIGGER_PREFIX = "{pintuan_promotion}_";

    @Autowired
    private PintuanMapper pintuanMapper;

    @Autowired
    private PintuanOrderMapper pintuanOrderMapper;

    @Autowired
    private PintuanGoodsManager pintuanGoodsManager;

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private PintuanOrderManager pintuanOrderManager;

    @Autowired
    private PintuanScriptManager pintuanScriptManager;

    @Override
    public WebPage list(PintuanQueryParam param) {

        IPage<Pintuan> iPage = this.pintuanMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()),
                new QueryWrapper<Pintuan>()
                        //如果商家不为空
                        .eq(param.getSellerId() != null && param.getSellerId() != 0, "seller_id", param.getSellerId())
                        //如果活动状态不为空
                        .eq(StringUtil.notEmpty(param.getStatus()), "status", param.getStatus())
                        //如果活动名称不为空
                        .like(StringUtil.notEmpty(param.getName()), "promotion_name", param.getName())
                        //如果活动开始时间不为空
                        .ge(param.getStartTime() != null && param.getStartTime() != 0, "start_time", param.getStartTime())
                        //如果活动结束时间不为空
                        .le(param.getEndTime() != null && param.getEndTime() != 0, "end_time", param.getEndTime())
                        //创建时间倒叙
                        .orderByDesc("create_time"));

        return PageConvert.convert(iPage);
    }

    @Override
    public List<Pintuan> get(String status) {

        return this.pintuanMapper.selectList(new QueryWrapper<Pintuan>()
                .eq("status", status)
                .and(PromotionStatusEnum.UNDERWAY.name().equals(status), e -> {
                    long now = DateUtil.getDateline();
                    e.gt("start_time", now).lt("end_time", now);
                }));
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pintuan add(Pintuan pintuan) {
        //检测开始时间和结束时间
        PromotionValid.paramValid(pintuan.getStartTime(), pintuan.getEndTime(), 1, null);
        this.verifyParam(pintuan.getStartTime(), pintuan.getEndTime());
        pintuan.setStatus(PromotionStatusEnum.WAIT.name());
        pintuan.setSellerName(UserContext.getSeller().getSellerName());
        pintuan.setCreateTime(DateUtil.getDateline());
        pintuan.setSellerId(UserContext.getSeller().getSellerId());
        //可操作状态为nothing，代表活动不可以执行任何操作
        pintuan.setOptionStatus(PintuanOptionEnum.NOTHING.name());
        this.pintuanMapper.insert(pintuan);

        //创建活动 启用延时任务
        PintuanChangeMsg pintuanChangeMsg = new PintuanChangeMsg();
        pintuanChangeMsg.setPintuanId(pintuan.getPromotionId());
        pintuanChangeMsg.setOptionType(1);
        timeTrigger.add(TimeExecute.PINTUAN_EXECUTER, pintuanChangeMsg, pintuan.getStartTime(), TRIGGER_PREFIX + pintuan.getPromotionId());
        return pintuan;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pintuan edit(Pintuan pintuan, Long id) {
        //获取拼团活动
        Pintuan oldPintaun = this.getModel(id);
        //校验拼团是否可以被操作
        if (pintuan.getStatus().equals(PromotionStatusEnum.UNDERWAY.name())) {
            throw new ServiceException(PintuanErrorCode.E5017.code(), PintuanErrorCode.E5017.describe());
        }

        this.verifyParam(pintuan.getStartTime(), pintuan.getEndTime());

        pintuan.setPromotionId(id);
        this.pintuanMapper.updateById(pintuan);
        //修改拼团活动,首先删除旧活动的关闭任务  add by liuyulei 2019-08-08
        timeTrigger.delete(TimeExecute.PINTUAN_EXECUTER, oldPintaun.getEndTime(), "{TIME_TRIGGER}_" + id);

        PintuanChangeMsg pintuanChangeMsg = new PintuanChangeMsg();
        pintuanChangeMsg.setPintuanId(pintuan.getPromotionId());
        pintuanChangeMsg.setOptionType(1);
        //检测开始时间和结束时间
        PromotionValid.paramValid(pintuan.getStartTime(), pintuan.getEndTime(), 1, null);
        timeTrigger.edit(TimeExecute.PINTUAN_EXECUTER, pintuanChangeMsg, pintuan.getStartTime(), pintuan.getStartTime(), TRIGGER_PREFIX + id);
        return pintuan;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        Pintuan pintuan = this.getModel(id);

        if (pintuan.getStatus().equals(PromotionStatusEnum.UNDERWAY.name())) {
            throw new ServiceException(PintuanErrorCode.E5017.code(), PintuanErrorCode.E5017.describe());
        }

        this.pintuanMapper.deleteById(id);


        timeTrigger.delete(TimeExecute.PINTUAN_EXECUTER, pintuan.getStartTime(), TRIGGER_PREFIX + id);
    }

    @Override
    public Pintuan getModel(Long id) {
        return this.pintuanMapper.selectById(id);
    }


    /**
     * 开始一个活动
     *
     * @param promotionId
     */
    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void openPromotion(Long promotionId) {

        //获取拼团活动详情
        Pintuan pintuan = this.getModel(promotionId);
        //获取参与拼团活动的商品信息集合
        List<PintuanGoodsDO> goodsList = this.pintuanGoodsManager.getPintuanGoodsList(promotionId);

        UpdateWrapper updateWrapper = new UpdateWrapper<>()
                .eq("promotion_id", promotionId);
        //如果还在活动时间内
        //修改状态为进行中，活动可操作状态变成可以关闭
        if (pintuan.getEndTime() > DateUtil.getDateline()) {
            updateWrapper.set("status", PromotionStatusEnum.UNDERWAY.name());
            updateWrapper.set("option_status", PintuanOptionEnum.CAN_CLOSE.name());
//            this.tradeDaoSupport.execute("update es_pintuan set status = ? ,option_status=? where promotion_id = ?", PromotionStatusEnum.UNDERWAY.name(), PintuanOptionEnum.CAN_CLOSE.name(), promotionId);
            //生成拼团商品索引
            pintuanGoodsManager.addIndex(promotionId);
            //生成拼团商品脚本信息
            pintuanScriptManager.createCacheScript(promotionId, goodsList);
        } else {
            //活动时间范围外，修改状态为已结束，活动可操作状态变成nothing
//            this.tradeDaoSupport.execute("update es_pintuan set status = ? ,option_status=? where promotion_id = ?", PromotionStatusEnum.END.name(), PintuanOptionEnum.NOTHING.name(), promotionId);
            updateWrapper.set("status", PromotionStatusEnum.END.name());
            updateWrapper.set("option_status", PintuanOptionEnum.NOTHING.name());
            //删除拼团商品脚本信息
            pintuanScriptManager.deleteCacheScript(promotionId, goodsList);
        }

        this.pintuanMapper.update(null, updateWrapper);

    }

    /**
     * 停止一个活动
     *
     * @param promotionId
     */
    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void closePromotion(Long promotionId) {

        //获取拼团活动详情
        Pintuan pintuan = this.getModel(promotionId);
        //获取参与拼团活动的商品信息集合
        List<PintuanGoodsDO> goodsList = this.pintuanGoodsManager.getPintuanGoodsList(promotionId);

        UpdateWrapper updateWrapper = new UpdateWrapper<>()
                .set("status", PromotionStatusEnum.END.name())
                .eq("promotion_id", promotionId);

        //如果结束时间大于当前时间
        // 可以操作为开启状态，活动状态为已结束
        if (pintuan.getEndTime() > DateUtil.getDateline()) {
            //表示可以再次开启，则不处理未成团订单，因为可以开启
            updateWrapper.set("option_status", PintuanOptionEnum.CAN_OPEN.name());
//            this.tradeDaoSupport.execute("update es_pintuan set status = ? ,option_status=? where promotion_id = ?", PromotionStatusEnum.END.name(), PintuanOptionEnum.CAN_OPEN.name(), promotionId);
        } else {
            updateWrapper.set("option_status", PintuanOptionEnum.NOTHING.name());
//            this.tradeDaoSupport.execute("update es_pintuan set status = ? ,option_status=? where promotion_id = ?", PromotionStatusEnum.END.name(), PintuanOptionEnum.NOTHING.name(), promotionId);
            //查询所有该活动下的未成团订单（未付款，已付款未成团）
            List<PintuanOrder> orderList = this.pintuanOrderMapper.selectList(new QueryWrapper<PintuanOrder>()
                    .eq("pintuan_id", promotionId)
                    .and(e -> {
                        e.eq("order_status", PintuanOrderStatus.new_order.name())
                                .or().eq("order_status", PintuanOrderStatus.wait.name());
                    }));

            for (PintuanOrder order : orderList) {
                pintuanOrderManager.handle(order.getOrderId());
            }
        }

        this.pintuanMapper.update(null, updateWrapper);

        //删除拼团商品索引信息
        pintuanGoodsManager.delIndex(promotionId);
        //删除拼团商品脚本信息
        pintuanScriptManager.deleteCacheScript(promotionId, goodsList);
    }

    /**
     * 手动停止一个活动
     *
     * @param promotionId
     */
    @Override
    public void manualClosePromotion(Long promotionId) {
        if (check(promotionId, 0)) {
            this.closePromotion(promotionId);
        } else {
            throw new ServiceException(PintuanErrorCode.E5012.code(), PintuanErrorCode.E5012.describe());
        }
    }

    /**
     * 手动开始一个活动
     *
     * @param promotionId
     */
    @Override
    public void manualOpenPromotion(Long promotionId) {
        if (check(promotionId, 1)) {
            this.openPromotion(promotionId);
        } else {
            throw new ServiceException(PintuanErrorCode.E5012.code(), PintuanErrorCode.E5012.describe());
        }
    }

    /**
     * 校验 是否可以手动操作
     *
     * @param promotionId 拼团id
     * @param type        1开启检测 0结束检测
     * @return
     */
    private boolean check(Long promotionId, Integer type) {


        Pintuan pintuan = this.getModel(promotionId);
        if (AdminUserContext.getAdmin() == null) {
            if (UserContext.getSeller().getSellerId().equals(pintuan.getSellerId())) {
                throw new ServiceException(PintuanErrorCode.E5013.code(), PintuanErrorCode.E5013.describe());
            }
        }

        //时间段不对，不许操作
        if (pintuan.getStartTime() > DateUtil.getDateline() || pintuan.getEndTime() < DateUtil.getDateline()) {
            return false;
        }
        //开启
        if (type == 1) {
            //如果活动已经结束 可以操作开始
            return pintuan.getStatus().equals(PromotionStatusEnum.END.name());
        } else {
            //如果活动进行中 可以操作停止
            return pintuan.getStatus().equals(PromotionStatusEnum.UNDERWAY.name());
        }
    }

    /**
     * 验证参数
     *
     * @param startTime 活动开始时间
     * @param endTime   活动结束时间
     */
    private void verifyParam(long startTime, long endTime) {

        // 开始时间不能大于结束时间
        if (startTime > endTime) {
            throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER, "活动起始时间不能大于活动结束时间");
        }

    }

}

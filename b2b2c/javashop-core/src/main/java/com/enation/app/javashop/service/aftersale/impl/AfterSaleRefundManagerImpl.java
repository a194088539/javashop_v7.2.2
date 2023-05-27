package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleRefundMapper;
import com.enation.app.javashop.mapper.trade.aftersale.RefundMapper;
import com.enation.app.javashop.model.errorcode.AftersaleErrorCode;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleRefundDO;
import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.dto.RefundQueryParam;
import com.enation.app.javashop.model.aftersale.enums.*;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.aftersale.vo.RefundApplyVO;
import com.enation.app.javashop.model.aftersale.vo.RefundRecordVO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.aftersale.*;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.client.payment.RefundClient;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.support.FlowCheckOperate;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 退款单业务接口实现
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-22
 */
@SuppressWarnings("Duplicates")
@Service
public class AfterSaleRefundManagerImpl implements AfterSaleRefundManager {
    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private AfterSaleRefundMapper afterSaleRefundMapper;

    @Autowired
    private RefundClient refundClient;

    @Autowired
    private AfterSaleQueryManager afterSaleQueryManager;

    @Autowired
    private AfterSaleManager afterSaleManager;

    @Autowired
    private AfterSaleLogManager afterSaleLogManager;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private AfterSaleDataCheckManager afterSaleDataCheckManager;

    @Autowired
    private DepositeClient depositeClient;

    @Autowired
    private MessageSender messageSender;

    @Override
    public WebPage list(RefundQueryParam param) {
        //创建查询条件包装器
        QueryWrapper<RefundDO> wrapper = this.createQueryWrapper(param);
        //获取退款单分页列表数据
        IPage<RefundDO> iPage = refundMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);

        //转换商品数据
        List<RefundDO> refundDOList = iPage.getRecords();
        List<RefundRecordVO> recordVOList = new ArrayList<>();
        //循环初始化退款单对象
        for (RefundDO refund : refundDOList) {
            RefundRecordVO recordVO = new RefundRecordVO(refund);
            recordVOList.add(recordVO);
        }

        //转换分页对象
        WebPage<RefundRecordVO> webPage = new WebPage(param.getPageNo(), iPage.getTotal(), param.getPageSize(), recordVOList);
        return webPage;
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void adminRefund(String serviceSn, Double refundPrice, String remark, ServiceOperateTypeEnum typeEnum) {

        //参数校验
        this.afterSaleDataCheckManager.checkAdminRefund(serviceSn, refundPrice, remark);

        //获取售后服务单详细信息
        Permission permission = Permission.CLIENT;
        if (ServiceOperateTypeEnum.SELLER_REFUND.equals(typeEnum)) {
            permission = Permission.SELLER;
        } else if (ServiceOperateTypeEnum.ADMIN_REFUND.equals(typeEnum)) {
            permission = Permission.ADMIN;
        }
        ApplyAfterSaleVO applyAfterSaleVO = this.afterSaleQueryManager.detail(serviceSn, permission);

        //操作权限验证
        if (!FlowCheckOperate.checkOperate(applyAfterSaleVO.getServiceType(), applyAfterSaleVO.getServiceStatus(), typeEnum.value())) {
            throw new ServiceException(AftersaleErrorCode.E601.name(), "当前售后服务单状态不允许进行退款操作");
        }

        //根据售后服务单号获取退款单信息并校验
        RefundDO refund = this.getModel(serviceSn);
        if (refund == null) {
            throw new ServiceException(AftersaleErrorCode.E603.name(), "售后退款单信息不存在");
        }

        //获取退款时间
        long refundTime = DateUtil.getDateline();

        //新建修改条件包装器
        UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
        //修改退款单状态为已完成
        wrapper.set("refund_status", RefundStatusEnum.COMPLETED.value());
        //修改退款时间
        wrapper.set("refund_time", refundTime);
        //修改实际退款金额
        wrapper.set("actual_price", refundPrice);
        //以售后服务单号为修改条件
        wrapper.eq("sn", serviceSn);
        //修改退款单信息
        refundMapper.update(new RefundDO(), wrapper);

        //新建修改条件包装器
        UpdateWrapper<AfterSaleRefundDO> asWrapper = new UpdateWrapper<>();
        //修改退款时间
        asWrapper.set("refund_time", refundTime);
        //修改实际退款金额
        asWrapper.set("actual_price", refundPrice);
        //以售后服务单号为修改条件
        asWrapper.eq("service_sn", serviceSn);
        //修改退款单信息
        afterSaleRefundMapper.update(new AfterSaleRefundDO(), asWrapper);

        //将售后服务单状态和退款备注
        this.afterSaleManager.updateServiceStatus(serviceSn, ServiceStatusEnum.COMPLETED.value(), null, null, remark, null);

        //发送售后服务完成消息
        AfterSaleChangeMessage changeMessage = new AfterSaleChangeMessage(serviceSn, ServiceTypeEnum.valueOf(applyAfterSaleVO.getServiceType()), ServiceStatusEnum.COMPLETED);
        messageSender.send(new MqMessage(AmqpExchange.AS_STATUS_CHANGE, AmqpExchange.AS_STATUS_CHANGE + "_QUEUE", changeMessage));

        //新增退款操作日志
        String log = "已成功将退款退还给买家，当前售后服务已完成。";
        this.afterSaleLogManager.add(serviceSn, log, "系统");
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void sellerRefund(String serviceSn) {
        //售后服务单号不允许为空
        if (StringUtil.isEmpty(serviceSn)) {
            throw new ServiceException(AftersaleErrorCode.E618.name(), "售后服务单号不能为空");
        }

        //获取售后服务单详细信息
        ApplyAfterSaleVO applyAfterSaleVO = this.afterSaleQueryManager.detail(serviceSn, Permission.SELLER);

        Seller seller = UserContext.getSeller();
        if (seller == null || seller.getSellerId().intValue() != applyAfterSaleVO.getSellerId().intValue()) {
            throw new ServiceException(AftersaleErrorCode.E614.name(), "售后服务单信息不存在");
        }

        //获取售后服务单状态
        String serviceStatus = applyAfterSaleVO.getServiceStatus();

        //操作权限验证
        if (!FlowCheckOperate.checkOperate(applyAfterSaleVO.getServiceType(), serviceStatus, ServiceOperateTypeEnum.SELLER_REFUND.value())) {
            throw new ServiceException(AftersaleErrorCode.E601.name(), "当前售后服务单状态不允许进行商家退款操作");
        }

        //获取退款单相关信息
        RefundDO refund = this.getModel(serviceSn);
        if (refund == null) {
            throw new ServiceException(AftersaleErrorCode.E606.name(), "退款单信息不存在");
        }

        //日志详细
        String log = "";

        //获取退款时间
        long refundTime = DateUtil.getDateline();
        //如果是退款至预存款
        if (RefundWayEnum.BALANCE.name().equals(refund.getRefundWay())) {
            boolean bool = depositeClient.increase(refund.getAgreePrice(), refund.getMemberId(), "退款成功，退款单号:" + refund.getSn());
            if (bool) {
                serviceStatus = ServiceStatusEnum.COMPLETED.value();

                //新建修改条件包装器
                UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
                //修改退款单状态为已完成
                wrapper.set("refund_status", serviceStatus);
                //修改退款时间
                wrapper.set("refund_time", refundTime);
                //修改实际退款金额
                wrapper.set("actual_price", refund.getAgreePrice());
                //以售后服务单号为修改条件
                wrapper.eq("sn", serviceSn);
                //如果退款至与承诺款操作成功，就将退款单状态修改为已完成并修改退款日期以及实际退款字段值
                refundMapper.update(new RefundDO(), wrapper);

                //新建修改条件包装器
                UpdateWrapper<AfterSaleRefundDO> asWrapper = new UpdateWrapper<>();
                //修改退款时间
                asWrapper.set("refund_time", refundTime);
                //修改实际退款金额
                asWrapper.set("actual_price", refund.getAgreePrice());
                //以售后服务单号为修改条件
                asWrapper.eq("service_sn", serviceSn);
                //修改售后服务退款相关信息
                afterSaleRefundMapper.update(new AfterSaleRefundDO(), asWrapper);

                log = "售后服务退款至预存款中已退款成功，当前售后服务已完成，如有疑问请及时联系商家或平台。";
            } else {
                serviceStatus = ServiceStatusEnum.WAIT_FOR_MANUAL.value();
                //新建修改条件包装器
                UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
                //修改退款单状态为已完成
                wrapper.set("refund_status", RefundStatusEnum.REFUNDING.value());
                //以售后服务单号为修改条件
                wrapper.eq("sn", serviceSn);
                //如果退款至预存款操作失败，就将退款单状态修改为退款中，等待平台线下退款
                refundMapper.update(new RefundDO(), wrapper);

                log = "售后服务退款至预存款操作失败，需要商家或平台进行人工退款处理。";
            }
        } else if (refund.getRefundWay().equals(RefundWayEnum.ORIGINAL.value())) {
            //如果支持原路退回，则商品入库后直接原路退款
            //原路退款操作
            Map map = refundClient.originRefund(refund.getPayOrderNo(), refund.getSn(), refund.getAgreePrice(), "b2b2c");

            //如果原路退款成功
            if ("true".equals(map.get("result").toString())) {
                serviceStatus = ServiceStatusEnum.REFUNDING.value();

                //新建修改条件包装器
                UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
                //修改退款单状态为已完成
                wrapper.set("refund_status", serviceStatus);
                //修改退款时间
                wrapper.set("refund_time", refundTime);
                //修改实际退款金额
                wrapper.set("actual_price", refund.getAgreePrice());
                //以售后服务单号为修改条件
                wrapper.eq("sn", serviceSn);
                //如果原路退款操作成功，就将退款单状态修改为已完成并修改退款日期以及实际退款字段值
                refundMapper.update(new RefundDO(), wrapper);

                //新建修改条件包装器
                UpdateWrapper<AfterSaleRefundDO> asWrapper = new UpdateWrapper<>();
                //修改退款时间
                asWrapper.set("refund_time", refundTime);
                //修改实际退款金额
                asWrapper.set("actual_price", refund.getAgreePrice());
                //以售后服务单号为修改条件
                asWrapper.eq("service_sn", serviceSn);
                //修改售后服务退款相关信息
                afterSaleRefundMapper.update(new AfterSaleRefundDO(), asWrapper);

                //新增售后操作日志
                log = "售后服务已退款成功，请关注您账户退款到账信息。";
            } else {
                serviceStatus = ServiceStatusEnum.WAIT_FOR_MANUAL.value();

                //新建修改条件包装器
                UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
                //修改退款单状态为已完成
                wrapper.set("refund_status", RefundStatusEnum.REFUNDING.value());
                //以售后服务单号为修改条件
                wrapper.eq("sn", serviceSn);
                //如果原路退款操作失败，就将退款单状态修改为退款中，等待平台线下退款
                refundMapper.update(new RefundDO(), wrapper);

                log = "售后服务原路退款操作失败，原因：" + map.get("fail_reason") + "，需要商家或平台进行人工退款处理。";
            }
        } else {
            serviceStatus = ServiceStatusEnum.WAIT_FOR_MANUAL.value();

            //新建修改条件包装器
            UpdateWrapper<RefundDO> wrapper = new UpdateWrapper<>();
            //修改退款单状态为已完成
            wrapper.set("refund_status", RefundStatusEnum.REFUNDING.value());
            //以售后服务单号为修改条件
            wrapper.eq("sn", serviceSn);
            //如果退款方式是线下支付，就将退款单状态修改为退款中，等待平台线下退款
            refundMapper.update(new RefundDO(), wrapper);

            log = "商家已同意退款，等待平台进行人工退款处理。";
        }

        //修改售后服务单状态
        this.afterSaleManager.updateServiceStatus(serviceSn, serviceStatus);

        //新增售后日志
        this.afterSaleLogManager.add(serviceSn, log, seller.getUsername());

        //发送售后服务商家退款消息
        AfterSaleChangeMessage afterSaleChangeMessage = new AfterSaleChangeMessage(serviceSn, ServiceTypeEnum.valueOf(applyAfterSaleVO.getServiceType()), ServiceStatusEnum.valueOf(serviceStatus));
        messageSender.send(new MqMessage(AmqpExchange.AS_STATUS_CHANGE, AmqpExchange.AS_STATUS_CHANGE + "_QUEUE", afterSaleChangeMessage));
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RefundDO fillRefund(Double refundPrice, ApplyAfterSaleVO applyAfterSaleVO) {
        RefundDO refundDO = new RefundDO();
        BeanUtil.copyProperties(applyAfterSaleVO, refundDO);
        refundDO.setRefundWay(applyAfterSaleVO.getRefundInfo().getRefundWay());
        refundDO.setAccountType(applyAfterSaleVO.getRefundInfo().getAccountType());
        refundDO.setReturnAccount(applyAfterSaleVO.getRefundInfo().getReturnAccount());
        refundDO.setBankName(applyAfterSaleVO.getRefundInfo().getBankName());
        refundDO.setBankAccountNumber(applyAfterSaleVO.getRefundInfo().getBankAccountNumber());
        refundDO.setBankAccountName(applyAfterSaleVO.getRefundInfo().getBankAccountName());
        refundDO.setBankDepositName(applyAfterSaleVO.getRefundInfo().getBankDepositName());
        refundDO.setRefundPrice(applyAfterSaleVO.getRefundInfo().getRefundPrice());
        refundDO.setAgreePrice(refundPrice);
        refundDO.setRefundStatus(RefundStatusEnum.APPLY.value());
        refundDO.setDisabled(DeleteStatusEnum.NORMAL.value());
        refundDO.setPayOrderNo(applyAfterSaleVO.getRefundInfo().getPayOrderNo());
        Long id = this.addRefund(refundDO);
        refundDO.setId(id);

        //新建修改条件包装器
        UpdateWrapper<AfterSaleRefundDO> asWrapper = new UpdateWrapper<>();
        //修改商家同意的退款金额
        asWrapper.set("agree_price", refundPrice);
        //以售后服务单号为修改条件
        asWrapper.eq("service_sn", applyAfterSaleVO.getSn());
        //修改售后服务退款相关信息
        afterSaleRefundMapper.update(new AfterSaleRefundDO(), asWrapper);

        return refundDO;
    }

    @Override
    public Long addRefund(RefundDO refundDO) {
        //退款单信息入库
        refundMapper.insert(refundDO);
        //返回退款单主键
        return refundDO.getId();
    }

    @Override
    public RefundDO getModel(String serviceSn) {
        //创建查询条件包装器
        QueryWrapper<RefundDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("sn", serviceSn);
        //返回按售后服务单号查询出的退款单信息
        return refundMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void fillAfterSaleRefund(String serviceSn, Integer returnNum, OrderDO orderDO, OrderItemsDO itemsDO, AfterSaleRefundDO refundDO) {
        //填充售后退款账户相关信息
        refundDO.setServiceSn(serviceSn);
        refundDO.setPayOrderNo(orderDO.getPayOrderNo());

        //计算用户申请退货的可退款金额
        double refundPrice = 0.00;
        if (returnNum.intValue() == itemsDO.getNum().intValue()) {
            refundPrice = itemsDO.getRefundPrice();
        } else {
            refundPrice = CurrencyUtil.mul(returnNum, CurrencyUtil.div(itemsDO.getRefundPrice(), itemsDO.getNum(), 4));
        }
        refundDO.setRefundPrice(refundPrice);

        //获取订单的支付方式
        PaymentMethodDO paymentMethodDO = null;
        if (orderDO.getPaymentPluginId() != null) {
            paymentMethodDO = this.paymentClient.getByPluginId(orderDO.getPaymentPluginId());
        }

        if (orderDO.getBalance() != null && orderDO.getBalance() > 0) {
            refundDO.setRefundWay(RefundWayEnum.BALANCE.value());
        } else if (paymentMethodDO == null || paymentMethodDO.getIsRetrace() == 0) {
            //如果订单没有支付方式信息或者支付方式不支持原路退款
            //校验退款信息
            this.afterSaleDataCheckManager.checkRefundInfo(refundDO);

            //退款方式为账户退款
            refundDO.setRefundWay(RefundWayEnum.ACCOUNT.value());

        } else {
            //如果支付方式支持原路退回，填充退款账号信息
            fillAccountInfo(refundDO, paymentMethodDO.getPluginId());
        }

        //退款相关信息入库
        this.addAfterSaleRefund(refundDO);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void fillCancelOrderRefund(String serviceSn, OrderDO orderDO, RefundApplyVO refundApplyVO) {
        //填充并校验售后退款相关信息
        AfterSaleRefundDO afterSaleRefundDO = new AfterSaleRefundDO();
        BeanUtil.copyProperties(refundApplyVO, afterSaleRefundDO);
        afterSaleRefundDO.setServiceSn(serviceSn);
        afterSaleRefundDO.setPayOrderNo(orderDO.getPayOrderNo());

        //获取订单的支付方式
        PaymentMethodDO paymentMethodDO = null;
        if (orderDO.getPaymentPluginId() != null) {
            paymentMethodDO = this.paymentClient.getByPluginId(orderDO.getPaymentPluginId());
        }

        if (orderDO.getBalance() != null && orderDO.getBalance() > 0) {
            afterSaleRefundDO.setRefundWay(RefundWayEnum.BALANCE.value());
        } else if (paymentMethodDO == null || paymentMethodDO.getIsRetrace() == 0) {
            //如果订单没有支付方式信息或者支付方式不支持原路退款
            if (OrderTypeEnum.PINTUAN.name().equals(orderDO.getOrderType())) {
                //退款方式为线下退款
                afterSaleRefundDO.setRefundWay(RefundWayEnum.OFFLINE.value());
            } else {
                //如果是普通订单申请取消需要校验申请参数
                this.afterSaleDataCheckManager.checkRefundInfo(afterSaleRefundDO);

                //退款方式为账户退款
                afterSaleRefundDO.setRefundWay(RefundWayEnum.ACCOUNT.value());
            }

        } else {

            //如果支付方式支持原路退回，填充退款账号信息
            fillAccountInfo(afterSaleRefundDO, paymentMethodDO.getPluginId());
        }

        //退款信息入库
        this.addAfterSaleRefund(afterSaleRefundDO);
    }

    @Override
    public void addAfterSaleRefund(AfterSaleRefundDO refundDO) {
        //售后退款/退货信息入库
        afterSaleRefundMapper.insert(refundDO);
    }

    @Override
    public AfterSaleRefundDO getAfterSaleRefund(String serviceSn) {
        //创建查询条件包装器
        QueryWrapper<AfterSaleRefundDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("service_sn", serviceSn);
        //返回根据售后服务单号查询出来的信息
        return afterSaleRefundMapper.selectOne(wrapper);
    }

    @Override
    public List<RefundRecordVO> exportExcel(RefundQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<RefundDO> wrapper = this.createQueryWrapper(param);
        //获取要导出的退款单信息集合
        List<RefundDO> refundDOList = this.refundMapper.selectList(wrapper);

        //转换商品数据
        List<RefundRecordVO> recordVOList = new ArrayList<>();

        for (RefundDO refund : refundDOList) {
            RefundRecordVO recordVO = new RefundRecordVO(refund);
            recordVOList.add(recordVO);
        }

        return recordVOList;
    }

    /**
     * 初始化查询条件包装器
     *
     * @param param 查询条件参数
     * @return
     */
    private QueryWrapper<RefundDO> createQueryWrapper(RefundQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<RefundDO> wrapper = new QueryWrapper<>();
        //以售后服务状态为正常状态作为查询条件
        wrapper.eq("disabled", DeleteStatusEnum.NORMAL.value());
        //如果会员ID不为空并且不等于0，则以会员ID为查询条件
        wrapper.eq(param.getMemberId() != null && param.getMemberId() != 0, "member_id", param.getMemberId());
        //如果商家ID不为空并且不等于0，则以商家ID为查询条件
        wrapper.eq(param.getSellerId() != null && param.getSellerId() != 0, "seller_id", param.getSellerId());
        //如果查询关键字不为空，以售后服务单号或订单编号或售后商品信息作为条件进行模糊查询
        wrapper.and(StringUtil.notEmpty(param.getKeyword()), ew -> {
            ew.like("sn", param.getKeyword()).or().like("order_sn", param.getKeyword()).or().like("goods_json", param.getKeyword());
        });
        //如果售后服务单号不为空，则以售后服务单号作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getServiceSn()), "sn", param.getServiceSn());
        //如果订单编号不为空，则以订单编号作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getOrderSn()), "order_sn", param.getOrderSn());
        //如果商品名称不为空，则以商品名称作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getGoodsName()), "goods_json", param.getGoodsName());
        //如果退款单状态不为空，则以退款单状态为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getRefundStatus()), "refund_status", param.getRefundStatus());
        //如果退款方式不为空，则以退款方式为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getRefundWay()), "refund_way", param.getRefundWay());
        //如果退款单创建时间-起始时间不为空也不等于0，则按退款单创建时间大于等于当前这个起始时间进行查询
        wrapper.ge(param.getStartTime() != null && param.getStartTime() != 0, "create_time", param.getStartTime());
        //如果退款单创建时间-结束时间不为空也不等于0，则按退款单创建时间小于等于当前这个结束时间进行查询
        wrapper.le(param.getEndTime() != null && param.getEndTime() != 0, "create_time", param.getEndTime());
        //如果退款单创建渠道不为空，则以退款单创建渠道为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getCreateChannel()), "create_channel", param.getCreateChannel());
        //按照退款单创建时间倒序排序
        wrapper.orderByDesc("create_time");
        return wrapper;
    }

    /**
     * 填充退款账号信息
     *
     * @param afterSaleRefundDO
     * @param pluginId
     */
    private void fillAccountInfo(AfterSaleRefundDO afterSaleRefundDO, String pluginId) {
        //如果支付方式支持原路退回
        String accountType = null;

        if ("weixinPayPlugin".equals(pluginId)) {
            accountType = AccountTypeEnum.WEIXINPAY.value();
        } else if ("alipayDirectPlugin".equals(pluginId)) {
            accountType = AccountTypeEnum.ALIPAY.value();
        } else if ("unionpayPlugin".equals(pluginId)) {
            accountType = AccountTypeEnum.BANKTRANSFER.value();
        } else if ("chinapayPlugin".equals(pluginId)) {
            accountType = AccountTypeEnum.BANKTRANSFER.value();
        }

        afterSaleRefundDO.setAccountType(accountType);
        //退款方式为原路退回
        afterSaleRefundDO.setRefundWay(RefundWayEnum.ORIGINAL.value());
    }
}

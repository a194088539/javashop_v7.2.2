package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.client.member.MemberCommentClient;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.OrderTaskMapper;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.vo.*;
import com.enation.app.javashop.service.trade.order.OrderTaskManager;
import com.enation.app.javashop.service.trade.order.OrderOperateManager;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.model.trade.order.dto.OrderSkuDTO;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单任务
 *
 * @author Snow create in 2018/7/13
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class OrderTaskManagerImpl implements OrderTaskManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderTaskMapper orderTaskMapper;

    @Autowired
    private SettingClient settingClient;

    @Autowired
    private OrderOperateManager orderOperateManager;

    @Autowired
    private MemberCommentClient memberCommentClient;

    @Autowired
    private OrderClient orderClient;


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void cancelTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        int time = this.dayConversionSecond(settingVO.getCancelOrderDay());

        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<OrderDO>()
                //查询订单编号
                .select("sn")
                //拼接支付方式查询条件
                .ne("payment_type", PaymentTypeEnum.COD.value())
                //拼接订单时间查询条件
                .lt("create_time", DateUtil.getDateline() - time)
                //拼接订单状态查询条件  and (order_status = ? or order_status = ?)
                .and(i -> i.eq("order_status", OrderStatusEnum.NEW.value()).or().eq("order_status", OrderStatusEnum.CONFIRM.value()));

        List<Map<String, Object>> list = orderMapper.selectMaps(queryWrapper);

        for (Map map : list) {
            CancelVO cancel = new CancelVO();
            cancel.setOrderSn(map.get("sn").toString());
            cancel.setReason("超时未付款");
            cancel.setOperator("系统检测");
            this.orderOperateManager.cancel(cancel, OrderPermission.client);
        }

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void rogTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        //系统时间
        long unixTime = DateUtil.getDateline();
        int time = this.dayConversionSecond(settingVO.getRogOrderDay());

        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<OrderDO>()
                //查询订单编号
                .select("sn")
                //拼接支付方式查询条件
                .eq("order_status", OrderStatusEnum.SHIPPED.value())
                //拼接订单时间查询条件
                .lt("ship_time", unixTime-time);

        List<Map<String, Object>> list = orderMapper.selectMaps(queryWrapper);

        for (Map map : list) {
            RogVO rog = new RogVO();
            rog.setOrderSn(map.get("sn").toString());
            rog.setOperator("系统检测");
            this.orderOperateManager.rog(rog, OrderPermission.client);
        }

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void completeTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        //系统时间
        long unixTime = DateUtil.getDateline();
        int time = this.dayConversionSecond(settingVO.getCompleteOrderDay());

        //获取要取消的订单编号集合：订单状态不等于已完成 并且 ((收货时间加上自动完成设置的时间小于当前时间 并且 订单付款类型为在线支付 并且 订单收货状态为已收货)
        // 或者 (付款时间加上自动完成设置的时间小于当前时间 并且 订单付款类型为货到付款 并且 订单付款状态为已付款))
        List<Map> list = orderTaskMapper.selectCompleteTaskList(unixTime-time);

        for (Map map : list) {
            CompleteVO complete = new CompleteVO();
            complete.setOrderSn(map.get("sn").toString());
            complete.setOperator("系统检测");
            try {
                this.orderOperateManager.complete(complete, OrderPermission.client);
            } catch (Exception e) {
                logger.error("订单自动标记为完成出错：订单编号"+complete.getOrderSn(), e);
            }
        }
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void payTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        int time = this.dayConversionSecond(settingVO.getCompleteOrderPay());

        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<OrderDO>()
                //查询订单编号，订单价格
                .select("sn", "order_price")
                //拼接签收时间查询条件
                .lt("signing_time", DateUtil.getDateline() - time)
                //拼接支付类型查询条件
                .eq("payment_type", PaymentTypeEnum.COD.value())
                //拼接订单状态查询条件
                .eq("order_status", OrderStatusEnum.ROG.value());

        List<Map<String, Object>> list = orderMapper.selectMaps(queryWrapper);

        for (Map map : list) {
            this.orderOperateManager.payOrder(map.get("sn").toString(), StringUtil.toDouble(map.get("order_price"), false), "", OrderPermission.client);
        }

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void serviceTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        //系统时间
        long unixTime = DateUtil.getDateline();
        int time = this.dayConversionSecond(settingVO.getServiceExpiredDay());

        //检测订单状态售后状态的同时也需要检测订单中ItemJson售后状态

        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<OrderDO>()
                //查询订单编号
                .select("sn")
                //拼接完成时间查询条件
                .lt("complete_time", unixTime - time)
                //拼接订单状态查询条件
                .eq("order_status", OrderStatusEnum.COMPLETE.value())
                //拼接订单申请售后服务查询条件
                .like("items_json", OrderServiceStatusEnum.NOT_APPLY.value());

        List<Map<String, Object>> list = orderMapper.selectMaps(queryWrapper);

        String sn = "";
        for (Map map : list) {
            sn = map.get("sn").toString();
            this.orderOperateManager.updateServiceStatus(sn, OrderServiceStatusEnum.EXPIRED);

            //修改订单项(itemJson)中售后状态 为已过期
            OrderDetailDTO order = this.orderClient.getModel(sn);
            //获取订单SKU信息
            List<OrderSkuDTO> orderSkuList = order.getOrderSkuList();

            orderSkuList.forEach(orderSkuDTO -> {
                orderSkuDTO.setServiceStatus(OrderServiceStatusEnum.EXPIRED.value());
            });

            this.orderOperateManager.updateItemJson(JsonUtil.objectToJson(orderSkuList), sn);
        }

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void commentTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        int time = this.dayConversionSecond(settingVO.getCommentOrderDay());

        List<OrderDetailVO> detailList = orderTaskMapper.selectCommentTaskList(DateUtil.getDateline()-time);

        List<OrderDetailDTO> detailDTOList = new ArrayList<>();
        for (OrderDetailVO orderDetail : detailList) {
            this.orderOperateManager.updateCommentStatus(orderDetail.getSn(), CommentStatusEnum.FINISHED);

            OrderDetailDTO detailDTO = new OrderDetailDTO();
            BeanUtils.copyProperties(orderDetail, detailDTO);
            detailDTOList.add(detailDTO);
        }
        this.memberCommentClient.autoGoodComments(detailDTOList);

    }

    @Override
    public void complainTask() {
        OrderSettingVO settingVO = this.getOrderSetting();
        int time = this.dayConversionSecond(settingVO.getComplainExpiredDay());
        long unixTime = DateUtil.getDateline();

        List<OrderDO> orderList = new QueryChainWrapper<>(orderMapper)
                //拼接完成时间查询条件
                .lt("complete_time", unixTime-time)
                //拼接订单状态查询条件
                .eq("order_status", OrderStatusEnum.COMPLETE.value())
                //列表查询
                .list();

        for(OrderDO order : orderList){

            List<OrderSkuVO> skuList = JsonUtil.jsonToList(order.getItemsJson(), OrderSkuVO.class);

            for(OrderSkuVO sku : skuList){
                this.orderOperateManager.updateOrderItemsComplainStatus(order.getSn(),sku.getSkuId(), null, ComplainSkuStatusEnum.EXPIRED);
            }

        }

    }


    /**
     * 读取订单设置
     *
     * @return
     */
    private OrderSettingVO getOrderSetting() {
        String settingVOJson = this.settingClient.get(SettingGroup.TRADE);

        OrderSettingVO settingVO = JsonUtil.jsonToObject(settingVOJson, OrderSettingVO.class);
        return settingVO;
    }

    /**
     * 将天数转换为相应的秒数
     * 如果是测试模式，默认为1秒
     *
     * @param day
     * @return
     */
    private Integer dayConversionSecond(int day) {
        Integer time = day * 24 * 60 * 60;
        String siteSettingJson = settingClient.get(SettingGroup.SITE);

        SiteSetting siteSetting = JsonUtil.jsonToObject(siteSettingJson, SiteSetting.class);
        if (siteSetting.getTestMode().intValue() == 1) {
            time = 1;
        }

        return time;
    }


}

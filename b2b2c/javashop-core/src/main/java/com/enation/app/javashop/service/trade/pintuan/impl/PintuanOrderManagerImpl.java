package com.enation.app.javashop.service.trade.pintuan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanChildOrderMapper;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanGoodsMapper;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanMapper;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanOrderMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.promotion.pintuan.*;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.dto.PersonalizedData;
import com.enation.app.javashop.model.trade.order.enums.OrderDataKey;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.service.aftersale.AfterSaleManager;
import com.enation.app.javashop.service.trade.order.TradeIntodbManager;
import com.enation.app.javashop.service.trade.pintuan.PinTuanSearchManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanOrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * Created by kingapex on 2019-01-24.
 * 拼团订单业务实现类<br/>
 * 实现了拼团订单开团和参团 *
 *
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-24
 */

@Service
public class PintuanOrderManagerImpl implements PintuanOrderManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PintuanOrderMapper pintuanOrderMapper;

    @Autowired
    private PintuanChildOrderMapper pintuanChildOrderMapper;

    @Autowired
    private PintuanMapper pintuanMapper;

    @Autowired
    private PintuanGoodsMapper pintuanGoodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PintuanGoodsManager pintuanGoodsManager;

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private PintuanManager pintuanManager;

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private PinTuanSearchManager pinTuanSearchManager;

    @Autowired
    private AfterSaleManager afterSaleManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private TradeIntodbManager tradeIntodbManager;


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PintuanOrder createOrder(TradeVO tradeVO, OrderDTO order, Long skuId, Long pinTuanOrderId) {

        PintuanOrder pintuanOrder;
        PinTuanGoodsVO pinTuanGoodsVO = pintuanGoodsManager.getDetail(skuId, null);

        //拼团订单不为空，表示要参团
        if (pinTuanOrderId != null) {
            pintuanOrder = this.getModel(pinTuanOrderId);
            if (pintuanOrder == null) {
                logger.error("试图参加拼团，但拼团订单[" + pinTuanOrderId + "]不存在");
                throw new ResourceNotFoundException("拼团订单[" + pinTuanOrderId + "]不存在");
            }

            logger.debug("参加拼团订单：");
            logger.debug(pintuanOrder);
        } else {

            //创建拼团
            pintuanOrder = new PintuanOrder();
            pintuanOrder.setEndTime(pinTuanGoodsVO.getEndTime());
            pintuanOrder.setOfferedNum(0);
            pintuanOrder.setPintuanId(pinTuanGoodsVO.getPintuanId());
            pintuanOrder.setRequiredNum(pinTuanGoodsVO.getRequiredNum());
            pintuanOrder.setSkuId(skuId);
            pintuanOrder.setGoodsId(pinTuanGoodsVO.getGoodsId());
            pintuanOrder.setThumbnail(pinTuanGoodsVO.getThumbnail());
            pintuanOrder.setOrderStatus(PintuanOrderStatus.new_order.name());
            pintuanOrder.setGoodsName(pinTuanGoodsVO.getGoodsName());

            //新增一个拼团订单
            this.pintuanOrderMapper.insert(pintuanOrder);

            logger.debug("创建一个新的拼团订单：");
            logger.debug(pintuanOrder);
        }

        //创建子订单
        PintuanChildOrder childOrder = new PintuanChildOrder();
        childOrder.setSkuId(skuId);
        childOrder.setOrderStatus(PintuanOrderStatus.wait.name());

        //拼团活动id
        childOrder.setPintuanId(pinTuanGoodsVO.getPintuanId());
        childOrder.setOrderSn(order.getSn());

        //拼团订单id
        childOrder.setOrderId(pintuanOrder.getOrderId());
        childOrder.setMemberId(order.getMemberId());
        childOrder.setMemberName(order.getMemberName());
        childOrder.setOriginPrice(pinTuanGoodsVO.getOriginPrice());
        childOrder.setSalesPrice(pinTuanGoodsVO.getSalesPrice());

        pintuanChildOrderMapper.insert(childOrder);

        // 交易入库
        tradeIntodbManager.intoDB(tradeVO);

        logger.debug("创建一个新的子订单：");
        logger.debug(childOrder);
        return pintuanOrder;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void payOrder(OrderDO order) {

        String orderSn = order.getSn();
        //查找子订单
        PintuanChildOrder childOrder = this.getChildByOrderSn(orderSn);

        logger.debug("订单【" + order.getSn() + "】支付成功，获得其对应拼团子订单为：");
        logger.debug(childOrder);

        //查找主订单
        PintuanOrder pintuanOrder = this.getModel(childOrder.getOrderId());

        //加入一个参团者
        Member member = memberClient.getModel(order.getMemberId());
        Participant participant = new Participant();
        participant.setId(member.getMemberId());
        participant.setName(member.getUname());
        participant.setFace(member.getFace());
        pintuanOrder.appendParticipant(participant);


        //成团人数
        Integer requiredNum = pintuanOrder.getRequiredNum();

        //已参团人数
        Integer offeredNum = pintuanOrder.getOfferedNum();

        //新增一人
        offeredNum++;
        pintuanOrder.setOfferedNum(offeredNum);

        //如果已经成团 如果系统设置两人成团，实际三人付款，那么也成团
        if (offeredNum >= requiredNum) {

            logger.debug("拼团订单：" + pintuanOrder + "已经成团");

            pintuanOrder.setOrderStatus(PintuanOrderStatus.formed.name());

            //更新拼团订单
            this.pintuanOrderMapper.updateById(pintuanOrder);

            formed(pintuanOrder.getOrderId());

            logger.debug("更新所有子订单及普通订单为已成团");
        } else {

            logger.debug("offeredNum[" + offeredNum + "],requiredNum[" + requiredNum + "]");
            logger.debug("拼团订单：" + pintuanOrder + "尚未成团");

            //更新拼团订单为待成团
            pintuanOrder.setOrderStatus(PintuanOrderStatus.wait.name());

            //更新子订单为已支付状态
            this.pintuanChildOrderMapper.update(null, new UpdateWrapper<PintuanChildOrder>()
                    .set("order_status", PintuanOrderStatus.pay_off.name())
                    .eq("order_id", pintuanOrder.getOrderId())
                    .eq("child_order_id", childOrder.getChildOrderId()));

            //更新拼团订单
            this.pintuanOrderMapper.updateById(pintuanOrder);
        }

        //第一个人，即为创建拼团的人，那么配置定时任务进行处理
        if (pintuanOrder.getOfferedNum() == 1) {
            //标记延时任务处理这个订单，活动结束时   延时取消/自动成团
            timeTrigger.add("pintuanOrderHandlerTriggerExecuter", pintuanOrder.getOrderId(), pintuanOrder.getEndTime(), "pintuan_order_handler_" + pintuanOrder.getOrderId());
        }

        Integer count = requiredNum - offeredNum;
        if (count < 0) {
            count = 0;
        }
        //更新拼团成团人数，更新本订单，也要更新整个团的订单
        updatePintuanPerson(pintuanOrder, count);

    }

    /**
     * 根据id获取模型
     *
     * @param id
     * @return
     */
    @Override
    public PintuanOrder getModel(Long id) {
        return this.pintuanOrderMapper.selectById(id);
    }

    /**
     * 更新拼团成团人数
     *
     * @param pintuanOrder 拼团主订单
     * @param num          数量
     */
    private void updatePintuanPerson(PintuanOrder pintuanOrder, Integer num) {

        //根据主订单查询所有的子订单
        List<Map<String, Object>> ptList = this.pintuanChildOrderMapper.selectMaps(new QueryWrapper<PintuanChildOrder>()
                .select("order_sn")
                .eq("order_id", pintuanOrder.getOrderId()));

        //循环取出订单号集合
        List<String> orderSnList = ptList.stream().map(map -> map.get("order_sn").toString()).collect(toList());

        //查询以上订单号的订单数据
        List<Map<String, Object>> list = this.orderMapper.selectMaps(new QueryWrapper<OrderDO>()
                .select("order_data,sn")
                .in("sn", orderSnList));

        for (Map orderMap : list) {

            String orderData = orderMap.get("order_data") == null ? "" : orderMap.get("order_data").toString();
            String sn = orderMap.get("sn").toString();

            PersonalizedData personalizedData = new PersonalizedData(orderData);
            Map map = new HashMap();
            //还差几人成团
            map.put("owesPersonNums", num);
            personalizedData.setPersonalizedData(OrderDataKey.pintuan, map);

            logger.debug("拼团订单" + sn + "还差成团人数:" + num);

            //更新订单的个性化数据
            this.orderMapper.update(null, new UpdateWrapper<OrderDO>()
                    .set("order_data", personalizedData.getData())
                    .set("order_type", OrderTypeEnum.PINTUAN.name())
                    .eq("sn", sn));

        }


    }

    /**
     * 某个拼团订单成团操作
     *
     * @param pinTuanOrderId
     */
    private void formed(Long pinTuanOrderId) {

        //订单
        PintuanOrder pintuanOrder = this.getModel(pinTuanOrderId);

        List<Map<String, Object>> list = this.pintuanChildOrderMapper.selectMaps(new QueryWrapper<PintuanChildOrder>()
                .select("order_sn")
                .eq("order_id", pinTuanOrderId));

        list.forEach(map -> {
            String orderSn = map.get("order_sn").toString();
            //更新订单状态为已成团
            this.orderMapper.update(null, new UpdateWrapper<OrderDO>()
                    .set("order_status", OrderStatusEnum.FORMED.name())
                    .eq("sn", orderSn)
                    .eq("pay_status", PayStatusEnum.PAY_YES.name()));
        });

        //更新所有子订单为已经成团
        this.pintuanChildOrderMapper.update(null, new UpdateWrapper<PintuanChildOrder>()
                .set("order_status", PintuanOrderStatus.formed.name())
                .eq("order_id", pinTuanOrderId));

        PintuanGoodsDO goodsDO = pintuanGoodsManager.getModel(pintuanOrder.getPintuanId(), pintuanOrder.getSkuId());
        pintuanGoodsManager.addQuantity(goodsDO.getId(), pintuanOrder.getOfferedNum());

        //延时任务执行-拼团活动结束时，获取拼团商品，
        PinTuanGoodsVO goodsVO = pintuanGoodsManager.getDetail(goodsDO.getSkuId(), pintuanOrder.getEndTime() - 1);
        pinTuanSearchManager.addIndex(goodsVO);

        this.messageSender.send(new MqMessage(AmqpExchange.PINTUAN_SUCCESS, AmqpExchange.PINTUAN_SUCCESS + "_ROUTING", pinTuanOrderId));


    }


    @Override
    public PintuanOrderDetailVo getMainOrderBySn(String orderSn) {

        return this.pintuanOrderMapper.getMainOrderBySn(orderSn);

    }

    @Override
    public List<PintuanOrder> getWaitOrder(Long goodsId, Long skuId) {


        List<Map<String, Object>> pintuanGoodes = this.pintuanGoodsMapper.selectMaps(new QueryWrapper<PintuanGoodsDO>()
                .select("pintuan_id")
                .eq("sku_id",skuId));

        List<Map<String, Object>> pintuans = this.pintuanMapper.selectMaps(new QueryWrapper<Pintuan>()
                .gt("end_time", DateUtil.getDateline())
                .lt("start_time", DateUtil.getDateline()));

        List<Long> pintuanIds = pintuans.stream().distinct().map(pintuan -> Long.parseLong(pintuan.get("promotion_id").toString())).collect(toList());
        Long id = pintuanGoodes.stream()
                .map(goods -> Long.parseLong(goods.get("pintuan_id").toString()))
                .filter(pintuanId -> pintuanIds.contains(pintuanId))
                .findAny()
                .orElse(0L);

        return this.pintuanOrderMapper.selectList(new QueryWrapper<PintuanOrder>()
                .eq("sku_id", skuId)
                .eq("order_status", PintuanOrderStatus.wait.name())
                .eq("pintuan_id", id));
    }

    /**
     * 读取某订单的所有子订单
     *
     * @param orderId
     * @return
     */
    @Override
    public List<PintuanChildOrder> getPintuanChild(Long orderId) {

        return this.pintuanChildOrderMapper.selectList(new QueryWrapper<PintuanChildOrder>().eq("order_id", orderId));
    }

    /**
     * 处理拼团订单
     *
     * @param orderId 订单id
     */
    @Override
    public void handle(Long orderId) {

        //处理订单
        PintuanOrder pintuanOrder = this.getModel(orderId);

        Pintuan pintuan = pintuanManager.getModel(pintuanOrder.getPintuanId());

        List<PintuanChildOrder> pintuanChildOrders = this.getPintuanChild(pintuanOrder.getOrderId());

        //成团人数
        int requiredNum = pintuanOrder.getRequiredNum();
        //已参团人数
        int offeredNum = pintuanOrder.getOfferedNum();

        //如果未开启虚拟成团
        if (pintuan.getEnableMocker().equals(0)) {
            //成团人数<参团人数
            if (pintuanOrder.getOfferedNum() < pintuanOrder.getRequiredNum()) {
                pintuanChildOrders.forEach(child -> {
                    afterSaleManager.cancelPintuanOrder(child.getOrderSn(), "拼团活动结束未成团，系统自动取消订单");
                });
            }
        }//开启虚拟成团 && 未成团
        else if (requiredNum - offeredNum > 0) {

            //根据主订单查询所有的子订单
            List<Map<String, Object>> ptList = this.pintuanChildOrderMapper.selectMaps(new QueryWrapper<PintuanChildOrder>()
                    .select("order_sn")
                    .eq("order_id", pintuanOrder.getOrderId()));

            //循环取出订单号集合
            List<String> orderSnList = ptList.stream().map(map -> map.get("order_sn").toString()).collect(toList());
            List<Map<String, Object>> list = this.orderMapper.selectMaps(new QueryWrapper<OrderDO>()
                    .select("pay_status,sn")
                    .in("sn", orderSnList));

            List<String> sns = new ArrayList<>();

            list.forEach(map -> {
                String orderSn = map.get("sn").toString();
                String payStatus = map.get("pay_status").toString();

                //如果是未付款的订单,则更新订单状态为取消
                if (PayStatusEnum.PAY_NO.name().equals(payStatus)) {
                    afterSaleManager.cancelPintuanOrder(orderSn, "拼团活动结束订单未付款，系统自动取消订单");
                } else {
                    sns.add(orderSn);
                }
            });

            //实际参团人数 = 未取消的订单数
            int actualNum = sns.size();

            //如果实际参团人数不为0，则需要自动成团；如果实际参团人数为0，证明参团订单都未付款并且已取消，就不需要自动成团了。
            if (actualNum != 0) {
                //应系统自动成团的数量 = 成团人数 - 实际参团人数
                int num = pintuanOrder.getRequiredNum() - actualNum;
                pintuanOrder.setOfferedNum(pintuanOrder.getRequiredNum());
                //匿名参团人
                for (int i = 0; i < num; i++) {
                    Participant participant = new Participant();
                    participant.setId(-1L);
                    participant.setName("小强");
                    participant.setFace("http://javashop-statics.oss-cn-beijing.aliyuncs.com/v70/normal/912BDD3146AE4BE19831DB9F357A34D8.jpeg");
                    pintuanOrder.appendParticipant(participant);
                }

                //该商品已经成团
                pintuanOrder.setOrderStatus(PintuanOrderStatus.formed.name());
                this.pintuanOrderMapper.updateById(pintuanOrder);

                //虚拟成团更显订单中待成团人数
                this.updatePintuanPerson(pintuanOrder, pintuanOrder.getRequiredNum() - pintuanOrder.getParticipants().size());

                //循环修改未被系统自动取消的订单状态为已成团
                for (String sn : sns) {
                    //更新订单状态为已成团
                    this.orderMapper.update(null, new UpdateWrapper<OrderDO>()
                            .set("order_status", OrderStatusEnum.FORMED.name())
                            .eq("sn", sn));
                    //更新未取消的子订单为已经成团
                    this.pintuanChildOrderMapper.update(null, new UpdateWrapper<PintuanChildOrder>()
                            .set("order_status", PintuanOrderStatus.formed.name())
                            .eq("order_sn", sn)
                            .eq("order_id", orderId));
                }

                PintuanGoodsDO goodsDO = pintuanGoodsManager.getModel(pintuanOrder.getPintuanId(), pintuanOrder.getSkuId());
                pintuanGoodsManager.addQuantity(goodsDO.getId(), actualNum);

                //延时任务执行-拼团活动结束时，获取拼团商品，
                PinTuanGoodsVO goodsVO = pintuanGoodsManager.getDetail(goodsDO.getSkuId(), pintuanOrder.getEndTime() - 1);
                pinTuanSearchManager.addIndex(goodsVO);

                this.messageSender.send(new MqMessage(AmqpExchange.PINTUAN_SUCCESS, AmqpExchange.PINTUAN_SUCCESS + "_ROUTING", orderId));
            }
        }
    }

    @Override
    public void cancelOrder(String orderSn) {
        PintuanChildOrder childByOrderSn = this.getChildByOrderSn(orderSn);
        if (childByOrderSn == null || this.getModel(childByOrderSn.getOrderId()) == null) {
            throw new ResourceNotFoundException("拼团订单不存在");
        }

        //取消拼团订单,修改主订单中参团人列表和参团人数  add by liuyulei 2019-07-25
        PintuanOrder mainPinTuanOrder = this.getModel(childByOrderSn.getOrderId());

        List<Participant> list = mainPinTuanOrder.getParticipants();

        mainPinTuanOrder.getParticipants().forEach(participant -> {
            if (participant.getId().equals(childByOrderSn.getMemberId())) {
                list.remove(participant);
            }
        });


        //拼团订单取消,修改子订单状态为取消
        this.pintuanChildOrderMapper.update(null, new UpdateWrapper<PintuanChildOrder>()
                .set("order_status", PintuanOrderStatus.cancel.name())
                .eq("order_sn", orderSn));

        //修改主订单参团人数信息
        this.pintuanOrderMapper.update(null, new UpdateWrapper<PintuanOrder>()
                //如果所有子订单都被取消了  那么主订单也需要取消  add by liuyulei 2019-07-31
                .set(this.checkCancelStatus(childByOrderSn.getOrderId()), "order_status", PintuanOrderStatus.cancel.name())
                .set("offered_num", list.size())
                .set("offered_persons", JsonUtil.objectToJson(list))
                //where条件
                .eq("order_id", childByOrderSn.getOrderId()));
    }

    /**
     * 根据订单编号找到拼团子订单
     *
     * @param orderSn
     * @return
     */
    private PintuanChildOrder getChildByOrderSn(String orderSn) {

        return this.pintuanChildOrderMapper.selectOne(new QueryWrapper<PintuanChildOrder>().eq("order_sn", orderSn));
    }


    /**
     * 监测所有子订单是否全部取消
     * <p>
     * add by liuyulei 2019-07-31
     */
    private boolean checkCancelStatus(Long orderId) {
        List<PintuanChildOrder> list = this.getPintuanChild(orderId);

        AtomicInteger count = new AtomicInteger();

        //计算取消状态的子订单数量
        list.forEach(pintuanChildOrder -> {

            if (PintuanOrderStatus.cancel.name().equals(pintuanChildOrder.getOrderStatus())) {
                count.getAndIncrement();

            }
        });

        //如果所有子订单都是取消状态,那么取消主订单
        return count.intValue() == list.size();
    }

}

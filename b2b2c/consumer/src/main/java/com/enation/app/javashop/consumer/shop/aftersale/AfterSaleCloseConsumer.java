package com.enation.app.javashop.consumer.shop.aftersale;

import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleGoodsDO;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 售后服务单关闭
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-05
 */
@Component
public class AfterSaleCloseConsumer implements AfterSaleChangeEvent {

    @Autowired
    private AfterSaleClient afterSaleClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) {
        //获取售后服务单详细信息
        ApplyAfterSaleVO applyAfterSaleVO = this.afterSaleClient.detail(afterSaleChangeMessage.getServiceSn());
        //获取售后服务单状态
        ServiceStatusEnum serviceStatus = afterSaleChangeMessage.getServiceStatus();
        //获取售后服务类型
        ServiceTypeEnum serviceType = afterSaleChangeMessage.getServiceType();

        boolean flag = (ServiceTypeEnum.CHANGE_GOODS.equals(serviceType) || ServiceTypeEnum.SUPPLY_AGAIN_GOODS.equals(serviceType)) && ServiceStatusEnum.CLOSED.equals(serviceStatus);

        //目前只有售后服务类型为换货或补发商品才允许关闭
        if (flag) {

            //获取申请售后的商品集合
            List<AfterSaleGoodsDO> goodsList = applyAfterSaleVO.getGoodsList();

            //获取申请售后的商品信息（除取消订单之外，其它类型售后服务与申请售后服务的商品都是一对一的关系，因此这里直接取商品集合的第一个值即可）
            AfterSaleGoodsDO goodsDO = goodsList.get(0);

            OrderDetailVO order = orderClient.getOrderVO(applyAfterSaleVO.getOrderSn());

            List<OrderSkuVO> skuList = order.getOrderSkuList();

            for(OrderSkuVO sku :skuList){
                //判断订单商品集合中的商品skuID是否和申请售后的商品skuID相同，如果相同，则恢复售后状态为未申请
                if (sku.getSkuId().equals(goodsDO.getSkuId())) {
                    sku.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
                }
            }

            //更改订单的item_json数据
            this.orderOperateClient.updateItemJson(JsonUtil.objectToJson(skuList), applyAfterSaleVO.getOrderSn());
        }
    }
}

package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.client.statistics.RefundDataClient;
import com.enation.app.javashop.model.statistics.dto.RefundData;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 订单申请通过
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/20 下午2:21
 */
@Component
public class DataRefundConsumer implements AfterSaleChangeEvent {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RefundDataClient refundDataClient;

    @Autowired
    private AfterSaleClient afterSaleClient;

    /**
     * 售后服务消息处理
     * @param afterSaleChangeMessage
     */
    @Override
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) {
        try {
            //如果售后服务单状态为审核通过 并且 售后服务单类型为退货或者取消订单
            boolean flag = ServiceStatusEnum.PASS.equals(afterSaleChangeMessage.getServiceStatus()) &&
                    (ServiceTypeEnum.RETURN_GOODS.equals(afterSaleChangeMessage.getServiceType()) || ServiceTypeEnum.ORDER_CANCEL.equals(afterSaleChangeMessage.getServiceType()));

            if (flag) {
                RefundDO refundDO = this.afterSaleClient.getAfterSaleRefundModel(afterSaleChangeMessage.getServiceSn());

                if (refundDO != null) {
                    this.refundDataClient.put(new RefundData(refundDO));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单售后服务异常：", e);
        }

    }
}

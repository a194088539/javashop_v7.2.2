package com.enation.app.javashop.consumer.shop.distribution;

import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.client.distribution.DistributionOrderClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分销订单退款
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-11-25
 */

@Component
public class DistributionRefundConsumer implements AfterSaleChangeEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DistributionOrderClient distributionOrderClient;

    @Autowired
    private AfterSaleClient afterSaleClient;

    @Override
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) {

        //如果售后服务单状态为已完成 并且 售后服务单类型为退货
        //取消订单不计算退款返利，因为返利是在收货后计算的 add by fk 2020年07月20日14:35:08
        boolean flag = ServiceStatusEnum.COMPLETED.equals(afterSaleChangeMessage.getServiceStatus()) &&
                ServiceTypeEnum.RETURN_GOODS.equals(afterSaleChangeMessage.getServiceType());

        if (flag) {
            //获取售后服务单详细信息
            ApplyAfterSaleVO applyAfterSaleVO = this.afterSaleClient.detail(afterSaleChangeMessage.getServiceSn());

            //获取售后服务退款单信息
            RefundDO refundDO = this.afterSaleClient.getAfterSaleRefundModel(afterSaleChangeMessage.getServiceSn());

            // 售后服务完成时算好各个级别需要退的返利金额 放入数据库
            this.distributionOrderClient.calReturnCommission(applyAfterSaleVO.getOrderSn(), refundDO.getActualPrice());
        }

    }
}

package com.enation.app.javashop.service.payment.plugin.weixin.executor;

import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.model.payment.vo.Form;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.service.payment.plugin.weixin.WeixinPuginConfig;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author fk
 * @version v2.0
 * @Description: 微信wap端
 * @date 2018/4/1810:12
 * @since v7.0.0
 */
@Service
public class WeixinPaymentWapExecutor extends WeixinPuginConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DomainHelper domainHelper;

    /**
     * 支付
     *
     * @param bill
     * @return
     */
    public Map onPay(PayBill bill) {

        Map<String, String> params = new TreeMap<>();
        Map<String, String> result = new TreeMap<>();
        params.put("spbill_create_ip", getIpAddress());
        params.put("trade_type", "MWEB");

        try {

            Map<String, String> map = super.createUnifiedOrder(bill,params);
            // 返回结果
            String resultCode = map.get("result_code");
            if (SUCCESS.equals(resultCode)) {
                String codeUrl = map.get("mweb_url");
                result.put("gateway_url", codeUrl + "&redirect_url=" + getPayWapSuccessUrl(bill.getTradeType().name(), bill.getSubSn()));
                return result;
            }
        } catch (Exception e) {
            this.logger.error("生成参数失败", e);

        }
        return null;

    }


    /**
     * 获取支付成功调取页面
     *
     * @param tradeType
     * @return
     */
    private String getPayWapSuccessUrl(String tradeType, String subSn) {

        StringBuffer url = new StringBuffer(domainHelper.getMobileDomain() + "/checkout/cashier?");
        if (TradeTypeEnum.TRADE.name().equals(tradeType)) {
            url.append("trade_sn=" + subSn);
        } else {
            url.append("order_sn=" + subSn);
        }

        return url.toString();
    }

}

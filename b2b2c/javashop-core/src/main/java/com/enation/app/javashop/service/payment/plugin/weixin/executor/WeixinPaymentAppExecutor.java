package com.enation.app.javashop.service.payment.plugin.weixin.executor;

import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.service.payment.plugin.weixin.WeixinPuginConfig;
import com.enation.app.javashop.service.payment.plugin.weixin.WeixinUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.stereotype.Service;

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
public class WeixinPaymentAppExecutor extends WeixinPuginConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 支付
     *
     * @param bill
     * @return
     */
    public Map onPay(PayBill bill) {

        try {
            Map<String, String> params = new TreeMap<>();
            params.put("spbill_create_ip", getIpAddress());
            params.put("trade_type", "APP");

            Map<String, String> map = super.createUnifiedOrder(bill,params);
            String resultCode = map.get("result_code");
            String retureCode = map.get("return_code");

            if(FAIL.equals(retureCode)){
                logger.error(map.get("return_msg"));
            }

            if (SUCCESS.equals(resultCode)) {
                String prepayId = map.get("prepay_id");
                Map<String, String> result = new TreeMap();
                result.put("appid", map.get("appid"));
                result.put("partnerid", map.get("mchid"));
                result.put("prepayid", prepayId);
                result.put("package", "Sign=WXPay");
                result.put("noncestr", StringUtil.getRandStr(10));
                result.put("timestamp", DateUtil.getDateline() + "");
                result.put("sign", WeixinUtil.createSign(result, map.get("key")));
                return result;
            }
        } catch (Exception e) {
            logger.error("返回参数转换失败", e);
        }
        return null;
    }


}

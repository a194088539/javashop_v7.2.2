package com.enation.app.javashop.model.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 域名配置
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/6/20
 */
@Configuration
@ConfigurationProperties(prefix = "javashop")
@SuppressWarnings("ConfigurationProperties")
public class DomainSettings {

    /**
     * 买家端域名
     */
    @Value("${javashop.domain.buyer:#{null}}")
    private String buyer;

    /**
     * 手机买家端域名
     */
    @Value("${javashop.domain.mobileBuyer:#{null}}")
    private String mobileBuyer;


    /**
     * 支付回调地址
     *
     * @return
     */
    @Value("${javashop.domain.callback:#{null}}")
    private String callback;

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getMobileBuyer() {
        return mobileBuyer;
    }

    public void setMobileBuyer(String mobileBuyer) {
        this.mobileBuyer = mobileBuyer;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }



    @Override
    public String toString() {
        return "DomainSettings{" +
                "buyer='" + buyer + '\'' +
                ", mobileBuyer='" + mobileBuyer + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }
}

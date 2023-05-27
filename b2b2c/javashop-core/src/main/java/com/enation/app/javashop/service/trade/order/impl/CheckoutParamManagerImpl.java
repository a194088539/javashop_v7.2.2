package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.client.member.MemberAddressClient;
import com.enation.app.javashop.client.system.RegionsClient;
import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.model.trade.order.enums.PaymentTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.CheckoutParamVO;
import com.enation.app.javashop.service.trade.order.CheckoutParamManager;
import com.enation.app.javashop.model.trade.order.support.CheckoutParamName;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.security.model.Buyer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算参数 业务层实现类
 *
 * @author Snow create in 2018/4/8
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class CheckoutParamManagerImpl implements CheckoutParamManager {

    @Autowired
    private Cache cache;

    @Autowired
    private MemberAddressClient memberAddressClient;

    @Autowired
    private RegionsClient regionsClient;


    @Override
    public CheckoutParamVO getParam() {
        CheckoutParamVO param = this.read();

        //如果session中没有 new一个，并赋给默认值
        if (param == null) {

            param = new CheckoutParamVO();
            Buyer buyer = UserContext.getBuyer();

            MemberAddress address = this.memberAddressClient.getDefaultAddress(buyer.getUid());
            Long addrId = 0L;
            if (address != null) {
                addrId = address.getAddrId();
            }
            //默认配送地址
            param.setAddressId(addrId);

            //默认支付方式
            param.setPaymentType(PaymentTypeEnum.defaultType());

            //默认不需要发票
            ReceiptHistory receipt = new ReceiptHistory();
            param.setReceipt(receipt);

            //默认时间
            param.setReceiveTime("任意时间");

            this.write(param);
        }
        return param;
    }

    @Override
    public void setAddressId(Long addressId) {
        String key = getRedisKey();
        Map<String, Object> map = this.cache.getHash(key);
        PaymentTypeEnum paymentType = (PaymentTypeEnum) map.get(CheckoutParamName.PAYMENT_TYPE);
        //验证一下是否支持货到付款
        this.checkCod(paymentType,addressId);

        this.cache.putHash(getRedisKey(), CheckoutParamName.ADDRESS_ID, addressId);
    }

    @Override
    public void setPaymentType(PaymentTypeEnum paymentTypeEnum) {
        this.cache.putHash(getRedisKey(), CheckoutParamName.PAYMENT_TYPE, paymentTypeEnum);
    }

    @Override
    public void setReceipt(ReceiptHistory receipt) {
        this.cache.putHash(getRedisKey(), CheckoutParamName.RECEIPT, receipt);
    }

    @Override
    public void setReceiveTime(String receiveTime) {
        this.cache.putHash(getRedisKey(), CheckoutParamName.RECEIVE_TIME, receiveTime);
    }

    @Override
    public void setRemark(String remark) {
        this.cache.putHash(getRedisKey(), CheckoutParamName.REMARK, remark);
    }


    @Override
    public void setClientType(String clientType) {
        this.cache.putHash(getRedisKey(), CheckoutParamName.CLIENT_TYPE, clientType);
    }

    @Override
    public void deleteReceipt() {
        this.cache.putHash(getRedisKey(), CheckoutParamName.RECEIPT, null);
    }

    @Override
    public void setAll(CheckoutParamVO paramVO) {
        this.write(paramVO);
    }

    @Override
    public void checkCod(PaymentTypeEnum paymentTypeEnum, Long addressId) {
        if(!PaymentTypeEnum.COD.equals(paymentTypeEnum)){
            return ;
        }

        if(addressId == null){
            CheckoutParamVO paramVO = this.getParam();
            addressId = paramVO.getAddressId();
        }

        MemberAddress memberAddress = this.memberAddressClient.getModel(addressId);

        if(memberAddress == null){
            return;
        }

        List<Long> addIds = new ArrayList<>();
        addIds.add(memberAddress.getProvinceId());
        addIds.add(memberAddress.getCityId());
        addIds.add(memberAddress.getCountyId());
        addIds.add(memberAddress.getTownId());

        for (Long region: addIds) {
            Regions regions = this.regionsClient.getModel(region);
            if(regions == null){
                continue;
            }
            if(regions.getCod() == 0){
                throw new NoPermissionException("["+regions.getLocalName() + "]不支持货到付款");
            }
        }


    }


    /**
     * 读取Key
     *
     * @return
     */
    private String getRedisKey() {
        Buyer buyer = UserContext.getBuyer();
        return CachePrefix.CHECKOUT_PARAM_ID_PREFIX.getPrefix() + buyer.getUid();
    }


    /**
     * 写入map值
     *
     * @param paramVO
     */
    private void write(CheckoutParamVO paramVO) {
        String redisKey = getRedisKey();
        Map map = new HashMap<>(4);

        if (paramVO.getAddressId() != null) {
            map.put(CheckoutParamName.ADDRESS_ID, paramVO.getAddressId());
        }

        if (paramVO.getReceiveTime() != null) {
            map.put(CheckoutParamName.RECEIVE_TIME, paramVO.getReceiveTime());
        }

        if (paramVO.getPaymentType() != null) {
            map.put(CheckoutParamName.PAYMENT_TYPE, paramVO.getPaymentType());
        }
        if (paramVO.getReceipt() != null) {
            map.put(CheckoutParamName.RECEIPT, paramVO.getReceipt());
        }
        if (paramVO.getRemark() != null) {
            map.put(CheckoutParamName.REMARK, paramVO.getRemark());
        }
        if (paramVO.getClientType() != null) {
            map.put(CheckoutParamName.CLIENT_TYPE, paramVO.getClientType());
        }

        this.cache.putAllHash(redisKey, map);
    }


    /**
     * 由Reids中读取出参数
     */
    private CheckoutParamVO read() {
        String key = getRedisKey();
        Map<String, Object> map = this.cache.getHash(key);

        //如果还没有存过则返回null
        if (map == null || map.isEmpty()) {
            return null;
        }

        //如果取到了，则取出来生成param
        Long addressId = (Long) map.get(CheckoutParamName.ADDRESS_ID);
        PaymentTypeEnum paymentType = (PaymentTypeEnum) map.get(CheckoutParamName.PAYMENT_TYPE);
        ReceiptHistory receipt = (ReceiptHistory) map.get(CheckoutParamName.RECEIPT);
        String receiveTime = (String) map.get(CheckoutParamName.RECEIVE_TIME);
        String remark = (String) map.get(CheckoutParamName.REMARK);
        String clientType = (String) map.get(CheckoutParamName.CLIENT_TYPE);


        CheckoutParamVO param = new CheckoutParamVO();

        param.setAddressId(addressId);
        param.setReceipt(receipt);
        if (receiveTime == null) {
            receiveTime = "任意时间";
        }
        param.setReceiveTime(receiveTime);
        param.setRemark(remark);
        if (paymentType == null) {
            paymentType = PaymentTypeEnum.defaultType();
        }

        param.setPaymentType(paymentType);
        param.setClientType(clientType);
        return param;
    }

}

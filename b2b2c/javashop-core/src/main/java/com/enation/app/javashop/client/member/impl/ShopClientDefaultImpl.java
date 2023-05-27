package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.model.shop.dos.ShopDO;
import com.enation.app.javashop.model.shop.dto.ShopBankDTO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.service.shop.ShopManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjp
 * @version v7.0
 * @Description
 * @ClassName ShopClientDefaultImpl
 * @since v7.0 上午10:02 2018/7/13
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class ShopClientDefaultImpl implements ShopClient {

    @Autowired
    private ShopManager shopManager;

    @Override
    public ShopVO getShop(Long shopId) {
        return shopManager.getShop(shopId);
    }

    @Override
    public List<ShopBankDTO> listShopBankInfo() {
        return shopManager.listShopBankInfo();
    }

    @Override
    public void addCollectNum(Long shopId) {
        shopManager.addcollectNum(shopId);
    }

    @Override
    public void reduceCollectNum(Long shopId) {
        shopManager.reduceCollectNum(shopId);
    }

    @Override
    public void calculateShopScore() {
        shopManager.calculateShopScore();
    }

    @Override
    public void updateShopGoodsNum(Long sellerId, Integer sellerGoodsCount) {
        shopManager.updateShopGoodsNum(sellerId, sellerGoodsCount);

    }

    @Override
    public int queryShopCount() {

        return shopManager.queryShopCount();
    }

    @Override
    public List<ShopDO> queryShopByRange(Long i, Long pageSize) {
        return shopManager.queryShopByRange(i, pageSize);

    }


}

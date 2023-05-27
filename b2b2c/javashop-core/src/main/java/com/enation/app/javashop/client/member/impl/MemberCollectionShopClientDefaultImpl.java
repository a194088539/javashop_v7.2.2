package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberCollectionShopClient;
import com.enation.app.javashop.service.member.MemberCollectionShopManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 会员收藏店铺client
 *
 * @author fk
 * @version v.2.0
 * @date 20/2/23 下午4:42
 * @since v7.2.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberCollectionShopClientDefaultImpl implements MemberCollectionShopClient {

    @Autowired
    private MemberCollectionShopManager memberCollectionShopManager;

    @Override
    public void changeSellerName(Long sellerId, String sellerName) {

        memberCollectionShopManager.changeSellerName(sellerId, sellerName);
    }
}

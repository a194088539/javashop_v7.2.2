package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.ShopRoleClient;
import com.enation.app.javashop.service.shop.ShopRoleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description:
 * @date 2018/8/17 14:44
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class ShopRoleClientDefaultImpl implements ShopRoleClient {

    @Autowired
    private ShopRoleManager shopRoleManager;

    @Override
    public Map<String, List<String>> getRoleMap(Long sellerId) {

        return shopRoleManager.getRoleMap(sellerId);
    }
}

package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.ShipTemplateClient;
import com.enation.app.javashop.model.shop.vo.ShipTemplateVO;
import com.enation.app.javashop.service.shop.ShipTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version v7.0
 * @Description:
 * @Author: zjp
 * @Date: 2018/7/25 16:24
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class ShipTemplateClientDefaultImpl implements ShipTemplateClient {

    @Autowired
    private ShipTemplateManager shipTemplateManager;

    @Override
    public ShipTemplateVO get(Long id) {
        return shipTemplateManager.getFromCache(id);
    }

    @Override
    public List<String> getScripts(Long id) {


        return shipTemplateManager.getScripts(id);
    }

    @Override
    public void cacheShipTemplateScript(ShipTemplateVO shipTemplateVO) {

        this.shipTemplateManager.cacheShipTemplateScript(shipTemplateVO);
    }
}

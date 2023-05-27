package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.client.system.RegionsClient;
import com.enation.app.javashop.model.member.vo.RegionVO;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.service.system.RegionsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version v7.0
 * @Description: 地区Client默认实现
 * @Author: zjp
 * @Date: 2018/7/27 11:18
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class RegionsClientDefaultImpl implements RegionsClient {

    @Autowired
    private RegionsManager regionsManager;

    @Override
    public List<Regions> getRegionsChildren(Long regionId) {
        return regionsManager.getRegionsChildren(regionId);
    }


    @Override
    public Regions getModel(Long id) {
        return regionsManager.getModel(id);
    }


    @Override
    public List<RegionVO> getRegionByDepth(Integer depth) {
        return regionsManager.getRegionByDepth(depth);
    }
}

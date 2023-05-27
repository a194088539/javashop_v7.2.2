package com.enation.app.javashop.service.system;

import com.baomidou.mybatisplus.extension.api.R;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.model.system.vo.RegionsVO;
import com.enation.app.javashop.service.statistics.CollectFrontStatisticsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 商家中心，商品收藏统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class RegionsManagerTest {

    @Autowired
    private RegionsManager regionsManager;


    @Autowired
    private Cache cache;

    @Test
    public void edit() {

        Regions regions = new Regions();

        regions.setId(2799L);
        regions.setLocalName("三环以内111");
        regions.setParentId(72L);
//        regions.setRegionPath(",1,72,2799,");
//        regions.setRegionGrade(3);
        regions.setCod(1);

        regionsManager.edit(regions, 2799L);
    }

    @Test
    public void add() {

        RegionsVO regionsVO = new RegionsVO();
        regionsVO.setCod(1);
        regionsVO.setLocalName("333");
        regionsVO.setParentId(2799L);
        regionsVO.setZipcode("xxx");

        regionsManager.add(regionsVO);
    }


    @Test
    public void getRegionsChildren() {

        System.out.println(regionsManager.getRegionsChildren(0L));
        System.out.println(regionsManager.getRegionsChildren(1L));
        System.out.println(regionsManager.getRegionsChildren(72L));
        System.out.println(regionsManager.getRegionsChildren(2799L));
    }

    @Test
    public void clearRegionsCache() {
        Integer depth = 1;
        Object obj = this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + depth);
        List<Regions> regions = (List<Regions>) obj;
        System.out.println(regions==null?null:regions.size());

        depth = 2;
        obj = this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + depth);
        regions = (List<Regions>) obj;
        System.out.println(regions==null?null:regions.size());

        depth = 3;
        obj = this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + depth);
        regions = (List<Regions>) obj;
        System.out.println(regions==null?null:regions.size());

        depth = 4;
        obj = this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + depth);
        regions = (List<Regions>) obj;
        System.out.println(regions==null?null:regions.size());
    }

}

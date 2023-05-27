package com.enation.app.javashop.service.promotion.minus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillApplyMapper;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 单品立减业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class MinusManagerTest {

    @Autowired
    private MinusManager minusManager;

    @Test
    public void list() {

        System.out.println(minusManager.list(1l, 10l, null));
        System.out.println(minusManager.list(1l, 10l, "活动"));
    }

}

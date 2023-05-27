package com.enation.app.javashop.service.promotion.tool;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyActiveMapper;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.service.promotion.tool.impl.AbstractPromotionRuleManagerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 限时抢购申请业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class AbstractPromotionRuleManagerTest extends AbstractPromotionRuleManagerImpl{

    @Autowired
    private GroupbuyActiveMapper groupbuyActiveMapper;

    @Test
    public void verifyName() {
        Integer count = new QueryChainWrapper<>(groupbuyActiveMapper)
                //拼接活动名称查询条件
                .eq("act_name", "11111")
                //拼接活动id查询条件
                .ne("act_id", "29333652064325633")
                //拼接删除状态查询条件
                .eq("delete_status", DeleteStatusEnum.NORMAL.value())
                //查询数量
                .count();

        System.out.println(count);
    }

    @Test
    public void verifyTime() {
        try {
            verifyTime(1594032025l, 1594032145l, PromotionTypeEnum.HALF_PRICE, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.HALF_PRICE+e.getMessage());}

        try {
            verifyTime(1594032296l, 1594032416l, PromotionTypeEnum.MINUS, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.MINUS+e.getMessage());}

        try {
            verifyTime(1594031900l, 1594032020l, PromotionTypeEnum.FULL_DISCOUNT, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.FULL_DISCOUNT+e.getMessage());}

        try {
            verifyTime(1594086998l, 1594090598l, PromotionTypeEnum.GROUPBUY, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.GROUPBUY+e.getMessage());}

        try {
            verifyTime(1594051200l, 0l, PromotionTypeEnum.SECKILL, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.SECKILL+e.getMessage());}

        try {
            verifyTime(1594051200l, 0l, PromotionTypeEnum.EXCHANGE, null);
        }catch (ServiceException e){System.out.println(PromotionTypeEnum.EXCHANGE+e.getMessage());}

    }


}

package com.enation.app.javashop.service.promotion.seckill;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillApplyMapper;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 限时抢购入库业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class SeckillManagerTest {

    @Autowired
    private SeckillManager seckillManager;
    @Autowired
    private SeckillApplyMapper seckillApplyMapper;

    @Test
    public void list() {

        SeckillQueryParam param = new SeckillQueryParam();

        param.setPageNo(1l);
        param.setPageSize(10l);
        System.out.println(seckillManager.list(param));

        param.setSeckillName("限时");
        System.out.println(seckillManager.list(param));

        param.setStartTime(1l);
        param.setEndTime(9999999999l);
        System.out.println(seckillManager.list(param));

        param.setStatus("RELEASE");
        System.out.println(seckillManager.list(param));
    }

    @Test
    public void add() {

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSeckillName("啊啊啊");
        seckillVO.setStartDay(1597115347l);
        seckillVO.setRangeList(Arrays.asList(1,2));

        seckillManager.add(seckillVO);
    }

    @Test
    public void edit() {

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSeckillName("啊啊啊");
        seckillVO.setStartDay(1597115347l);
        seckillVO.setRangeList(Arrays.asList(1,2));

        seckillManager.edit(seckillVO, 1l);
    }

    @Test
    public void delete() {

        seckillManager.delete(1l);
    }

    @Test
    public void getModel() {

        System.out.println(seckillManager.getModel(29042538040152065l));
    }

    @Test
    public void batchAuditGoods() {

        SeckillApplyDO seckillApplyDO = new SeckillApplyDO();
        seckillApplyDO.setGoodsId(123l);

        new UpdateChainWrapper<>(seckillApplyMapper)
                //按活动id修改
                .eq("apply_id", 2)
                //提交修改
                .update(seckillApplyDO);
    }

}

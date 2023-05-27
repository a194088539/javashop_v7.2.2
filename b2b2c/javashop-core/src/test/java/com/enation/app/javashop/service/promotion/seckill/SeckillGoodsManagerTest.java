package com.enation.app.javashop.service.promotion.seckill;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillApplyMapper;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

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
public class SeckillGoodsManagerTest {

    @Autowired
    private SeckillGoodsManager seckillGoodsManager;

    @Autowired
    private SeckillApplyMapper seckillApplyMapper;

    @Test
    public void list() {

        SeckillQueryParam param = new SeckillQueryParam();
        param.setSeckillId(29042538040152065l);
        param.setPageNo(1l);
        param.setPageSize(10l);

        System.out.println(seckillGoodsManager.list(param));

        param.setGoodsName("商品");

        System.out.println(seckillGoodsManager.list(param));
    }

    @Test
    public void delete() {

        seckillGoodsManager.delete(1l);
    }

    @Test
    public void getModel() {

        System.out.println(seckillGoodsManager.getModel(29046760051716127l));
    }

    @Test
    public void addApply() {

        SeckillApplyDO seckillApplyDO = new SeckillApplyDO();
        seckillApplyDO.setSellerId(17l);
        seckillApplyDO.setShopName("啊啊啊");
        seckillApplyDO.setSeckillId(29042538040152065l);
        seckillApplyDO.setSkuId(29043977416224775l);
        seckillApplyDO.setSoldQuantity(1);
        seckillApplyDO.setTimeLine(0);

        seckillGoodsManager.addApply(Arrays.asList(seckillApplyDO));
    }

    @Test
    public void addSoldNum() {

        boolean updateResult = new UpdateChainWrapper<>(seckillApplyMapper)
                //设置售空数量
                .setSql("sold_quantity = sold_quantity - " + 1)
                //设置已售数量
                .setSql("sales_num = sales_num + " + 1)
                //拼接商品id修改条件
                .eq("goods_id", 29044074849906697l)
                //拼接秒杀活动id修改条件
                .eq("seckill_id", 29042538040152065l)
                //拼接售空数量修改条件
                .ge("sold_quantity", 1)
                //提交修改
                .update();
        System.out.println(updateResult);


        boolean updateResult1 = new UpdateChainWrapper<>(seckillApplyMapper)
                //设置售空数量
                .setSql("sold_quantity = sold_quantity - " + 1000)
                //设置已售数量
                .setSql("sales_num = sales_num + " + 1000)
                //拼接商品id修改条件
                .eq("goods_id", 29044074849906697l)
                //拼接秒杀活动id修改条件
                .eq("seckill_id", 29042538040152065l)
                //拼接售空数量修改条件
                .ge("sold_quantity", 1000)
                //提交修改
                .update();
        System.out.println(updateResult1);
    }

    @Test
    public void getSeckillGoodsList() {

        System.out.println(seckillGoodsManager.getSeckillGoodsList());
    }

    @Test
    public void getSeckillGoodsList1() {

        System.out.println(seckillGoodsManager.getSeckillGoodsList(0, 1L, 10L));
    }

    @Test
    public void rollbackStock() {

        new UpdateChainWrapper<>(seckillApplyMapper)
                //设置售空数量
                .setSql("sold_quantity = sold_quantity + " + 0)
                //设置已售数量
                .setSql("sales_num = sales_num - " + 10)
                //拼接商品id修改条件
                .eq("goods_id", 29044074849906697l)
                //拼接秒杀活动ID修改条件
                .eq("seckill_id", 29042538040152065l)
                //提交修改
                .update();
    }

    @Test
    public void deleteSeckillGoods() {

        seckillApplyMapper.delete(new QueryWrapper<SeckillApplyDO>()
                //拼接sku删除条件
                .in("sku_id", Arrays.asList(555))
                //拼接活动开始日期删除条件
                .ge("start_day", DateUtil.startOfTodDay())
        );
    }

    @Test
    public void deleteBySeckillId() {

        seckillGoodsManager.deleteBySeckillId(666l);
    }

    @Test
    public void getGoodsList() {

        System.out.println(seckillGoodsManager.getGoodsList(29042538040152065l, "PASS"));
    }

}

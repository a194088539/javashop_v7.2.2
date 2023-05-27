package com.enation.app.javashop.service.trade.order;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单流程操作业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderOperateManagerTest {

    @Autowired
    private OrderOperateManager orderOperateManager;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Test
    public void updateServiceStatus() {

        orderOperateManager.updateServiceStatus("37419452492709890", OrderServiceStatusEnum.COMPLETE);
    }

    @Test
    public void updateCommentStatus() {

        orderOperateManager.updateCommentStatus("37419452492709890", CommentStatusEnum.FINISHED);
    }

    @Test
    public void updateItemJson() {

        String itemsJson = "[{\"seller_id\":17,\"seller_name\":\"化妆品店铺1\",\"goods_id\":29322628481294337,\"sku_id\":29322827865923593,\"sku_sn\":\"1\",\"cat_id\":5,\"num\":1,\"goods_weight\":1.0,\"original_price\":10.0,\"purchase_price\":10.0,\"subtotal\":10.0,\"name\":\"规格商品\",\"goods_image\":\"http://javashop-statics.oss-cn-beijing.aliyuncs.com/test/goods/A22CBE12BAF34FA8924C17384C3AA03C.jpeg_300x300\",\"spec_list\":[{\"spec_id\":29322679597277189,\"spec_value\":\"白色\",\"seller_id\":null,\"spec_name\":\"颜色\",\"sku_id\":null,\"big\":null,\"small\":null,\"thumbnail\":null,\"tiny\":null,\"spec_type\":0,\"spec_image\":\"\",\"spec_value_id\":29322699419557895},{\"spec_id\":5,\"spec_value\":\"100g\",\"seller_id\":null,\"spec_name\":\"重量\",\"sku_id\":null,\"big\":null,\"small\":null,\"thumbnail\":null,\"tiny\":null,\"spec_type\":0,\"spec_image\":\"\",\"spec_value_id\":103}],\"point\":0,\"snapshot_id\":37419456288419857,\"service_status\":\"NOT_APPLY\",\"complain_status\":\"NO_APPLY\",\"complain_id\":null,\"single_list\":[],\"group_list\":[],\"goods_operate_allowable_vo\":null,\"promotion_tags\":[],\"promotion_type\":null,\"purchase_num\":0,\"actual_pay_total\":10.0}]";

        orderOperateManager.updateItemJson(itemsJson,"37419452492709890");
    }

    @Test
    public void updateOrderStatus() {

        orderOperateManager.updateOrderStatus("37419452492709890", OrderStatusEnum.INTODB_ERROR);
    }

    @Test
    public void updateTradeStatus() {

        orderOperateManager.updateTradeStatus("37419452492709890", OrderStatusEnum.INTODB_ERROR);
    }

    @Test
    public void updateOrderPrice() {

        orderOperateManager.updateOrderPrice("37419452492709890", 1.58);
    }

    @Test
    public void updateRefundPrice() {

        new UpdateChainWrapper<>(orderItemsMapper)
                //设置可退款金额
                .set("refund_price", 1.58)
                //拼接订单编号修改条件
                .eq("order_sn", "37419452492709890")
                //拼接商品id修改条件
                .eq("goods_id", "29322628481294337")
                //拼接货品id修改条件
                .eq("product_id", "29322827865923593")
                //提交修改
                .update();
    }

}

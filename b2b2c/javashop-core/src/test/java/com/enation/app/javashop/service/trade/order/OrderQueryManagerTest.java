package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.trade.order.dto.OrderQueryParam;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderFlowNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 订单相关业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderQueryManagerTest {

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Test
    public void list() {

        OrderQueryParam paramDTO = new OrderQueryParam();
        paramDTO.setPageNo(1l);
        paramDTO.setPageSize(10l);

        System.out.println(orderQueryManager.list(paramDTO));
    }

    @Test
    public void export() {

        OrderQueryParam paramDTO = new OrderQueryParam();
        paramDTO.setPageNo(1l);
        paramDTO.setPageSize(10l);

        System.out.println(orderQueryManager.export(paramDTO));

    }

    @Test
    public void getOrderStatusNum() {

        OrderQueryParam paramDTO = new OrderQueryParam();
        paramDTO.setPageNo(1l);
        paramDTO.setPageSize(10l);

        System.out.println(orderQueryManager.getOrderStatusNum(null,null));

    }

    @Test
    public void getOrderNumByMemberId() {

        System.out.println(orderQueryManager.getOrderNumByMemberId(1l));
    }

    @Test
    public void getOrderCommentNumByMemberId() {

        System.out.println(orderQueryManager.getOrderCommentNumByMemberId(1l, CommentStatusEnum.FINISHED.name()));
    }

    @Test
    public void getOrderByTradeSn() {

        System.out.println(orderQueryManager.getOrderByTradeSn("37419452492709890", null));
    }

    @Test
    public void getModel() {

        System.out.println(orderQueryManager.getModel("37419452492709890"));
    }

    @Test
    public void listOrderByGoods() {

        System.out.println(orderQueryManager.listOrderByGoods(29322628481294337l, 1l, 10));
    }

    @Test
    public void getItemsPromotionTypeandNum() {

        System.out.println(orderQueryManager.getItemsPromotionTypeandNum("37419452492709890"));
    }

    @Test
    public void getOrderFlow() {

        List<OrderFlowNode> orderFlow = orderQueryManager.getOrderFlow("43181208032784395");

        System.out.println(orderFlow);
    }

}

package com.enation.app.javashop.model.trade.order.vo;


import com.enation.app.javashop.model.trade.complain.enums.ComplainStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-03-11
 */

public class MyList {
    private List<OrderFlowNode> list;

    public MyList( ) {
        this.list = new ArrayList<>();
    }

    public MyList add(OrderStatusEnum status) {
        OrderFlowNode orderCreateFlow = new OrderFlowNode(status);
        list.add(orderCreateFlow);
        return this;
    }


    public MyList addComplain(ComplainStatusEnum status) {
        OrderFlowNode orderCreateFlow = new OrderFlowNode(status);
        list.add(orderCreateFlow);
        return this;
    }

    public MyList add(OrderServiceStatusEnum status) {
        OrderFlowNode orderCreateFlow = new OrderFlowNode(status);
        list.add(orderCreateFlow);
        return this;
    }

    public List<OrderFlowNode>  getList() {

        return list;
    }


}

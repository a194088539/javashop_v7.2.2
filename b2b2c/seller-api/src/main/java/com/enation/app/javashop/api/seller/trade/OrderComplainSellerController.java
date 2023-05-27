package com.enation.app.javashop.api.seller.trade;

import com.enation.app.javashop.model.trade.complain.dos.OrderComplain;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplainCommunication;
import com.enation.app.javashop.model.trade.complain.dto.ComplainQueryParam;
import com.enation.app.javashop.model.trade.complain.vo.OrderComplainVO;
import com.enation.app.javashop.service.trade.complain.OrderComplainCommunicationManager;
import com.enation.app.javashop.service.trade.complain.OrderComplainManager;
import com.enation.app.javashop.model.trade.order.vo.OrderFlowNode;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Seller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 交易投诉表控制器
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-27 16:48:27
 */
@RestController
@RequestMapping("/seller/trade/order-complains")
@Api(description = "交易投诉表相关API")
public class OrderComplainSellerController {

    @Autowired
    private OrderComplainManager orderComplainManager;

    @Autowired
    private OrderComplainCommunicationManager orderComplainCommunicationManager;


    @ApiOperation(value = "查询交易投诉表列表", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, ComplainQueryParam param) {

        param.setPageNo(pageNo);
        param.setPageSize(pageSize);

        Seller seller = UserContext.getSeller();
        param.setSellerId(seller.getSellerId());

        return this.orderComplainManager.list(param);
    }


    @PutMapping(value = "/{id}/appeal")
    @ApiOperation(value = "商家申诉", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "appeal_content", value = "申诉内容", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "images", value = "申诉图片，多图逗号分隔", required = true, dataType = "string[]", paramType = "query"),
    })
    public OrderComplain auth(@PathVariable Long id,@ApiIgnore String appealContent,@ApiIgnore @RequestParam(value = "images",required = false)String[] images) {

        return this.orderComplainManager.appeal(id,appealContent,images);
    }


    @PutMapping(value = "/{id}/to-arbitration")
    @ApiOperation(value = "对话中提交仲裁，随时可以提交仲裁", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
    })
    public OrderComplain complete(@PathVariable Long id) {

        return this.orderComplainManager.arbitrate(id);
    }


    @PutMapping(value = "/{id}/communication")
    @ApiOperation(value = "提交对话", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "content", value = "对话内容", required = true, dataType = "string", paramType = "query"),
    })
    public OrderComplainCommunication communicate(@PathVariable Long id,String content) {

        Seller seller = UserContext.getSeller();
        OrderComplainCommunication communication = new OrderComplainCommunication(id,content,"卖家",seller.getSellerName(),seller.getSellerId());

        return this.orderComplainCommunicationManager.add(communication);
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个交易投诉")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的交易投诉表主键", required = true, dataType = "int", paramType = "path")
    })
    public OrderComplainVO get(@PathVariable Long id) {

        OrderComplainVO orderComplain = this.orderComplainManager.getModelAndCommunication(id);

        return orderComplain;
    }

    @GetMapping(value = "/{id}/flow")
    @ApiOperation(value = "查询一个交易投诉的流程图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的交易投诉表主键", required = true, dataType = "int", paramType = "path")
    })
    public List<OrderFlowNode> getFlow(@PathVariable Long id) {

        return this.orderComplainManager.getComplainFlow(id);
    }



}

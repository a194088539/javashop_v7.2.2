package com.enation.app.javashop.api.manager.trade;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplain;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplainCommunication;
import com.enation.app.javashop.model.trade.complain.dto.ComplainQueryParam;
import com.enation.app.javashop.model.trade.complain.vo.OrderComplainVO;
import com.enation.app.javashop.service.trade.complain.OrderComplainCommunicationManager;
import com.enation.app.javashop.service.trade.complain.OrderComplainManager;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.security.model.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 交易投诉表控制器
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-27 16:48:27
 */
@RestController
@RequestMapping("/admin/trade/order-complains")
@Api(description = "交易投诉表相关API")
public class OrderComplainManagerController {

    @Autowired
    private OrderComplainManager orderComplainManager;

    @Autowired
    private OrderComplainCommunicationManager orderComplainCommunicationManager;


    @ApiOperation(value = "查询交易投诉表列表", response = OrderComplain.class)
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, ComplainQueryParam param) {

        param.setPageNo(pageNo);
        param.setPageSize(pageSize);

        return this.orderComplainManager.list(param);
    }


    @PutMapping(value = "/{id}/to-appeal")
    @ApiOperation(value = "审核并交由商家申诉", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public OrderComplain auth(@PathVariable Long id) {

        return this.orderComplainManager.auth(id);
    }


    @PutMapping(value = "/{id}/complete")
    @ApiOperation(value = "直接仲裁结束流程", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "arbitration_result", value = "仲裁意见", required = true, dataType = "string", paramType = "query"),
    })
    public OrderComplain complete(@PathVariable Long id, String arbitrationResult) {

        return this.orderComplainManager.complete(id, arbitrationResult);
    }

    @PutMapping(value = "/{id}/communication")
    @ApiOperation(value = "提交对话", response = OrderComplain.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "content", value = "对话内容", required = true, dataType = "string", paramType = "query"),
    })
    public OrderComplainCommunication communicate(@PathVariable Long id, String content) {

        Admin admin = AdminUserContext.getAdmin();
        OrderComplainCommunication communication = new OrderComplainCommunication(id,content,"平台",admin.getUsername(),admin.getUid());

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

}

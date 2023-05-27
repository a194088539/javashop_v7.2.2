package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.client.trade.RechargeClient;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description: 会员充值API
 * @author: liuyulei
 * @create: 2019-12-31 09:26
 * @version:1.0
 * @since:7.1.4
 **/
@Api(description = "会员充值API")
@RestController
@RequestMapping("/members/recharge")
@Validated
public class RechargeBuyerController {

    @Autowired
    private RechargeClient rechargeClient;



    @GetMapping(value = "/list")
    @ApiOperation(value	= "查询会员预存款充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "页面显示大小", required = true, dataType = "int", paramType = "query")
    })
    public WebPage list(@Valid DepositeParamDTO paramDTO){
        Buyer buyer = UserContext.getBuyer();
        paramDTO.setMemberId(buyer.getUid());
        return this.rechargeClient.list(paramDTO);
    }


}

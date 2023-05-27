package com.enation.app.javashop.api.buyer.aftersale;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.aftersale.dto.RefundQueryParam;
import com.enation.app.javashop.model.aftersale.enums.CreateChannelEnum;
import com.enation.app.javashop.model.aftersale.vo.RefundRecordVO;
import com.enation.app.javashop.service.aftersale.AfterSaleRefundManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 售后退款相关API
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-24
 */
@Api(description="售后退款相关API")
@RestController
@RequestMapping("/buyer/after-sales/refund")
@Validated
public class RefundBuyerController {

    @Autowired
    private AfterSaleRefundManager afterSaleRefundManager;

    @ApiOperation(value = "获取售后退款单列表", response = RefundRecordVO.class)
    @GetMapping()
    public WebPage list(@Valid RefundQueryParam param){
        param.setMemberId(UserContext.getBuyer().getUid());
        param.setCreateChannel(CreateChannelEnum.NORMAL.value());
        return afterSaleRefundManager.list(param);
    }

}

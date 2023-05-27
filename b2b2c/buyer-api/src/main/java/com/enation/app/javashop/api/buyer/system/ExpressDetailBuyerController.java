package com.enation.app.javashop.api.buyer.system;

import com.enation.app.javashop.model.system.vo.ExpressDetailVO;
import com.enation.app.javashop.service.system.ExpressPlatformManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 物流查询接口
 *
 * @author zh
 * @version v7.0
 * @date 18/7/12 上午10:30
 * @since v7.0
 */
@Api(description = "物流查询接口")
@RestController
@RequestMapping("/express")
@Validated
public class ExpressDetailBuyerController {

    @Autowired
    private ExpressPlatformManager expressPlatformManager;

    @ApiOperation(value = "查询物流详细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "物流公司id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "快递单号", dataType = "String", paramType = "query"),
    })
    @GetMapping
    public ExpressDetailVO list(@ApiIgnore Long id, @ApiIgnore String num) {
        return this.expressPlatformManager.getExpressDetail(id, num);
    }
}

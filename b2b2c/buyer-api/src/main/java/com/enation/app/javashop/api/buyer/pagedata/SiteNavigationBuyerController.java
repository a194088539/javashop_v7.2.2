package com.enation.app.javashop.api.buyer.pagedata;

import com.enation.app.javashop.model.pagedata.SiteNavigation;
import com.enation.app.javashop.service.pagedata.SiteNavigationManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 导航栏控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-12 17:07:22
 */
@RestController
@RequestMapping("/pages/site-navigations")
@Api(description = "导航栏相关API")
public class SiteNavigationBuyerController {

    @Autowired
    private SiteNavigationManager siteNavigationManager;


    @ApiOperation(value = "查询导航栏列表", response = SiteNavigation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "client_type", value = "客户端类型", required = true, dataType = "string", paramType = "query", allowableValues = "PC,MOBILE"),
    })
    @GetMapping
    public List<SiteNavigation> list(@ApiIgnore String clientType) {

        return this.siteNavigationManager.listByClientType(clientType);
    }
}
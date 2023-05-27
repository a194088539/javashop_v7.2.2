package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.UpgradeLogDO;
import com.enation.app.javashop.service.distribution.UpgradeLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 升级日志
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/24 下午4:08
 */

@RestController
@RequestMapping("/admin/distribution/upgradelog")
@Api(description = "升级日志")
public class UpgradeLogManagerController {

    @Autowired
    private UpgradeLogManager upgradeLogManager;

    @ApiOperation("获取升级日志")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "member_name", value = "会员名", required = false, paramType = "query", dataType = "String", allowMultiple = false),
    })
    public WebPage<UpgradeLogDO> page(@ApiIgnore Long pageNo, @ApiIgnore  Long pageSize, @ApiIgnore  String memberName) {
        return upgradeLogManager.page(pageNo, pageSize, memberName);
    }


}

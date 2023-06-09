package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.model.distribution.dos.BillTotalDO;
import com.enation.app.javashop.service.distribution.BillTotalManager;
import com.enation.app.javashop.framework.database.WebPage;
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
 * 分销商总结算单
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/24 下午2:47
 */
@Api(description = "分销商总结算单")
@RestController
@RequestMapping("/admin/distribution/bill/total")
public class BillTotalManagerController {

    @Autowired
    private BillTotalManager billTotalManager;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
    })
    @GetMapping
    @ApiOperation("结算单分页")
    public WebPage<BillTotalDO> page(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return billTotalManager.page(pageNo, pageSize);
    }



}

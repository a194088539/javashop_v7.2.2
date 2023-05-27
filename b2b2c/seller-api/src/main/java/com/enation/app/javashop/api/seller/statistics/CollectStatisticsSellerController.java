package com.enation.app.javashop.api.seller.statistics;

import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.CollectFrontStatisticsManager;
import com.enation.app.javashop.framework.context.user.UserContext;
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
 * 商家统计 商品收藏前50
 *
 * @author mengyuanming
 * @version 2.0
 * @since 7.0
 * 2018年4月18日下午5:02:50
 */
@RestController
@RequestMapping("/seller/statistics/collect")
@Api(description = "商家统计  商品收藏前50")
public class CollectStatisticsSellerController {

    @Autowired
    private CollectFrontStatisticsManager collectFrontStatisticsManager;

    @ApiOperation(value = "收藏统计", response = SimpleChart.class)
    @GetMapping(value = "/chart")
    public SimpleChart getChart() {
        Long sellerId = UserContext.getSeller().getSellerId();
        return this.collectFrontStatisticsManager.getChart(sellerId);
    }

    @ApiOperation(value = "收藏列表数据", response = WebPage.class)
    @GetMapping(value = "/page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "当前页码，默认为1", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "页内数据量，默认为10", dataType = "int", paramType = "query")})
    public WebPage getPage(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        Long sellerId = UserContext.getSeller().getSellerId();
        return this.collectFrontStatisticsManager.getPage(pageNo, pageSize, sellerId);
    }

}

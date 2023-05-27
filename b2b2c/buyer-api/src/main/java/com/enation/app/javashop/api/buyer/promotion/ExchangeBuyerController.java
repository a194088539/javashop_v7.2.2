package com.enation.app.javashop.api.buyer.promotion;

import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeCat;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.exchange.dto.ExchangeQueryParam;
import com.enation.app.javashop.service.promotion.exchange.ExchangeCatManager;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.DateUtil;
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

import java.util.List;

/**
 * 积分商品相关API
 *
 * @author Snow create in 2018/7/23
 * @version v2.0
 * @since v7.0.0
 */
@RestController
@RequestMapping("/promotions/exchange")
@Api(description = "积分商品相关API")
@Validated
public class ExchangeBuyerController {

    @Autowired
    private ExchangeCatManager exchangeCatManager;

    @Autowired
    private ExchangeGoodsManager exchangeGoodsManager;

    @ApiOperation(value = "查询积分分类集合")
    @GetMapping("/cats")
    public List<ExchangeCat> getCat(){
        List<ExchangeCat> catList = this.exchangeCatManager.list(0L);
        return catList;
    }


    @ApiOperation(value = "查询积分商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name	= "cat_id", value = "积分分类id",dataType = "int",paramType =	"query"),
            @ApiImplicitParam(name	= "page_no", value = "页码", dataType = "int",	paramType =	"query"),
            @ApiImplicitParam(name	= "page_size", value = "条数", dataType = "int",	paramType =	"query")
    })
    @GetMapping("/goods")
    public WebPage<ExchangeDO> list(@ApiIgnore Long catId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        ExchangeQueryParam param = new ExchangeQueryParam();
        param.setCatId(catId);
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);

        param.setStartTime(DateUtil.getDateline());
        param.setEndTime(DateUtil.getDateline());
        WebPage webPage = this.exchangeGoodsManager.list(param);
        return webPage;
    }

}

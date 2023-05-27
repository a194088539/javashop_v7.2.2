package com.enation.app.javashop.api.buyer.pagedata;

import com.enation.app.javashop.model.pagedata.HotKeyword;
import com.enation.app.javashop.service.pagedata.HotKeywordManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 热门关键字控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-04 10:43:23
 */
@RestController
@RequestMapping("/pages/hot-keywords")
@Api(description = "热门关键字相关API")
public class HotKeywordBuyerController {

    @Autowired
    private HotKeywordManager hotKeywordManager;


    @ApiOperation(value = "查询热门关键字列表", response = HotKeyword.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "查询的数量", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping
    public List<HotKeyword> list(Integer num) {

        return this.hotKeywordManager.listByNum(num);
    }


}
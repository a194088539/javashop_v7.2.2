package com.enation.app.javashop.api.manager.statistics;

import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.base.vo.BackendIndexModelVO;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.vo.SalesTotal;
import com.enation.app.javashop.service.statistics.OrderStatisticManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 后台首页api
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-06-29 上午8:13
 */
@RestController
@RequestMapping("/admin/index/page")
@Api(description = "后台首页api")
@Validated
public class IndexPageManagerController {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private OrderStatisticManager orderStatisticManager;

    @GetMapping
    @ApiOperation(value = "首页响应")
    public BackendIndexModelVO index() {
        SearchCriteria searchCriteria = new SearchCriteria();
        LocalDate localDate = LocalDate.now();
        searchCriteria.setYear(localDate.getYear());
        searchCriteria.setMonth(localDate.getMonthValue());
        searchCriteria.setCycleType(QueryDateType.MONTH.value());
        SalesTotal salesTotal = orderStatisticManager.getSalesMoneyTotal(searchCriteria);

        BackendIndexModelVO backendIndexModelVO = new BackendIndexModelVO();
        backendIndexModelVO.setSalesTotal(salesTotal);
        backendIndexModelVO.setGoodsVos(goodsClient.newGoods(5));
        backendIndexModelVO.setMemberVos(memberClient.newMember(5));
        return backendIndexModelVO;
    }


}

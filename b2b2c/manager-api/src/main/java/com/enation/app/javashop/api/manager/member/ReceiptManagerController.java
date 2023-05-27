package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dto.HistoryQueryParam;
import com.enation.app.javashop.model.member.vo.ReceiptHistoryVO;
import com.enation.app.javashop.service.member.ReceiptHistoryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 会员开票历史记录API
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-16
 */
@Api(description = "会员开票历史记录API")
@RestController
@RequestMapping("/admin/members/receipts")
@Validated
public class ReceiptManagerController {

    @Autowired
    private ReceiptHistoryManager receiptHistoryManager;

    @ApiOperation(value = "查询会员开票历史记录信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页数", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "条数", dataType = "int", paramType = "query"),
    })
    @GetMapping()
    public WebPage list(@Valid HistoryQueryParam params, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        WebPage page = this.receiptHistoryManager.list(pageNo, pageSize, params);
        return page;
    }


    @ApiOperation(value = "查询会员开票历史记录详细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "history_id", value = "主键ID", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping("/{history_id}")
    public ReceiptHistoryVO get(@PathVariable("history_id") Long historyId) {
        return this.receiptHistoryManager.get(historyId);
    }


}

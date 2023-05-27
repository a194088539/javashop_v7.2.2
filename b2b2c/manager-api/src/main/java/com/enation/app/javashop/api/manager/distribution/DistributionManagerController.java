package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.vo.DistributionVO;
import com.enation.app.javashop.service.distribution.DistributionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 后台分销商管理
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/25 上午10:55
 */
@RestController
@RequestMapping("/admin/distribution/member")
@Api(description = "分销商")
public class DistributionManagerController {

    @Autowired
    private DistributionManager distributionManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation("分销商列表")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "member_name", value = "会员名字", required = false, paramType = "query", dataType = "String", allowMultiple = false),
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
    })
    public WebPage<DistributionVO> page(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String memberName) {

        try {
            return distributionManager.page(pageNo, pageSize, memberName);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取模版异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }



    @ApiOperation("修改模版")
    @PutMapping("/tpl")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "member_id", value = "会员ID", required = true, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "tpl_id", value = "模版id", required = true, paramType = "query", dataType = "int", allowMultiple = false),
    })
    public void changeTpl(@ApiIgnore Long memberId, @ApiIgnore Integer tplId) throws Exception {
        if (memberId == null || tplId == null) {
            throw new DistributionException(DistributionErrorCode.E1011.code(), DistributionErrorCode.E1011.des());
        }
        try {
            distributionManager.changeTpl(memberId, tplId);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("修改模版异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


}

package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.vo.BillMemberVO;
import com.enation.app.javashop.service.distribution.BillMemberManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 分销会员结算单控制器
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/24 下午2:39
 */
@Api(description = "分销会员结算单控制器")
@RestController
@RequestMapping("/admin/distribution/bill/member")
public class BillMemberManagerController {

    @Autowired
    private BillMemberManager billMemberManager;

    @ApiOperation("分销商分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "total_id", value = "总结算单id", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "uname", value = "会员名", required = false, paramType = "query", dataType = "String", allowMultiple = false)
    })
    @GetMapping
    public WebPage<BillMemberVO> page(@ApiIgnore Long totalId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize, String uname) {

        if (totalId == null) {
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
        try {
            return billMemberManager.page(pageNo, pageSize, totalId, uname);
        } catch (Exception e) {
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取某个业绩详情")
    @ApiImplicitParam(name = "id", value = "业绩单id", required = false, paramType = "query", dataType = "int", allowMultiple = false)
    public BillMemberVO billMemberVO(@PathVariable Long id) {
        try {
            return new BillMemberVO(billMemberManager.getBillMember(id));
        } catch (Exception e) {

            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


    @GetMapping("/down")
    @ApiOperation("获取某个分销商下级业绩")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "当前页面 业绩单id", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "member_id", value = "会员id", required = false, paramType = "query", dataType = "int", allowMultiple = false)
    })
    public List<BillMemberVO> downBillMemberVO(Long id, @ApiIgnore Long memberId) {
        try {
            List<BillMemberVO> list = billMemberManager.allDown(memberId, id);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @ApiOperation("导出会员结算单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "total_id", value = "总结算单id", required = false, paramType = "query", dataType = "int", allowMultiple = false),
    })
    @GetMapping("/export")
    public List<BillMemberVO> export(@ApiIgnore Long totalId) {
        return billMemberManager.page(1L, 99999L, totalId, "").getData();
    }

}

package com.enation.app.javashop.api.buyer.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.vo.BankParamsVO;
import com.enation.app.javashop.model.distribution.vo.WithdrawApplyVO;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.service.distribution.WithdrawManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 提现api
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/24 上午7:09
 */

@RestController
@RequestMapping("/distribution/withdraw")
@Api(description = "提现api")
public class WithdrawBuyerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WithdrawManager withdrawManager;
    @Autowired
    private DistributionManager distributionManager;

    @ApiOperation("保存 提现参数")
    @PutMapping(value = "/params")
    public BankParamsVO saveWithdrawWay(BankParamsVO bankParamsVO) {

        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        try {
            withdrawManager.saveWithdrawWay(bankParamsVO);
            return bankParamsVO;
        } catch (Exception e) {
            logger.error("保存失败", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


    @ApiOperation("获取 提现参数")
    @GetMapping(value = "/params")
    public BankParamsVO getWithdrawWay() {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        try {
            return withdrawManager.getWithdrawSetting(buyer.getUid());
        } catch (Exception e) {
            logger.error("获取失败", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @ApiOperation("提现申请")
    @PostMapping(value = "/apply-withdraw")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apply_money", value = "申请金额", required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query", dataType = "String"),

    })
    public SuccessMessage applyWithdraw(@ApiIgnore Double applyMoney, @ApiIgnore String remark) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        if (applyMoney == null) {
            throw new DistributionException(DistributionErrorCode.E1011.code(), DistributionErrorCode.E1011.des());
        }
        double rebate = distributionManager.getCanRebate(buyer.getUid());

        //如果提现申请金额值非法
        if (applyMoney <= 0) {
            throw new DistributionException(DistributionErrorCode.E1006.code(), DistributionErrorCode.E1006.des());
        }
        //如果申请金额小于当前可提现金额
        if (applyMoney <= rebate) {
            this.withdrawManager.applyWithdraw(buyer.getUid(), applyMoney, remark);
            return new SuccessMessage("已提交申请");

        } else {
            throw new DistributionException(DistributionErrorCode.E1003.code(), DistributionErrorCode.E1003.des());
        }


    }

    @ApiOperation("提现记录")
    @GetMapping(value = "/apply-history")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page_size", value = "分页大小", required = true, paramType = "query", dataType = "int"),
    })
    public WebPage<WithdrawApplyVO> applyWithdraw(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        return withdrawManager.pageWithdrawApply(buyer.getUid(), pageNo, pageSize);
    }


    @ApiOperation("可提现金额")
    @GetMapping(value = "/can-rebate")
    public SuccessMessage canRebate() {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        return new SuccessMessage(withdrawManager.getRebate(buyer.getUid()));
    }


}

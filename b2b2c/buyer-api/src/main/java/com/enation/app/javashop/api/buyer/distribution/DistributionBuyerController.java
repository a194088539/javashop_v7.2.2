package com.enation.app.javashop.api.buyer.distribution;

import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.distribution.vo.DistributionVO;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分销商控制器
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/23 上午8:34
 */

@RestController
@Api(description = "分销商api")
@RequestMapping("/distribution")
public class DistributionBuyerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DistributionManager distributionManager;


    @GetMapping(value = "/lower-list")
    @ApiOperation("获取下级分销商")
    public List<DistributionVO> getLowerDistributorList() {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        try {
            return this.distributionManager.getLowerDistributorTree(buyer.getUid());
        } catch (Exception e) {
            logger.error("获取下级的分销商列表出错", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @ApiOperation("获取推荐我的人")
    @GetMapping(value = "/recommend-me")
    public SuccessMessage recommendMe() {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        try {
            DistributionDO distributor = this.distributionManager.getDistributorByMemberId(buyer.getUid());
            //对分销商做非空校验
            if (distributor == null) {
                throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
            }
            return new SuccessMessage(this.distributionManager.getUpMember());
        } catch (Exception e) {
            logger.error("获取下级的分销商列表出错", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


}

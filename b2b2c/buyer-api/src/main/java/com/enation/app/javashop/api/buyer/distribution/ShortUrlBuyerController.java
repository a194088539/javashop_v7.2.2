package com.enation.app.javashop.api.buyer.distribution;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.dos.ShortUrlDO;
import com.enation.app.javashop.service.distribution.ShortUrlManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短链接识别
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/23 上午8:35
 */
@RestController
@RequestMapping("/distribution/su")
@Api(description = "短链接api")
public class ShortUrlBuyerController {


    @Resource
    private ShortUrlManager shortUrlManager;
    @Autowired
    private Cache cache;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 访问短链接 把会员id加入session中，并跳转页面
     *
     * @return
     */
    @GetMapping(value = "/visit")
    @ApiOperation("访问短链接 把会员id加入session中，并跳转页面")
    @ApiImplicitParam(name = "su", value = "短链接", required = true, paramType = "query", dataType = "String")
    public SuccessMessage visit(String su, @RequestHeader(required = false) String uuid) throws Exception {
        try {
            ShortUrlDO shortUrlDO = shortUrlManager.getLongUrl(su);
            if (shortUrlDO == null) {
                return null;
            }
            if (uuid != null) {
                cache.put(CachePrefix.DISTRIBUTION_UP.getPrefix()+uuid, getMemberId(shortUrlDO.getUrl()));
            }
            return new SuccessMessage(shortUrlDO.getUrl());
        } catch (Exception e) {
            logger.error("短连接验证出错", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());

        }

    }


    /**
     * url中提取member id
     *
     * @param url
     * @return
     */
    private Long getMemberId(String url) {
        String pattern = "(member_id=)(\\d+)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(url);
        if (m.find()) {
            return new Long(m.group(2));
        }
        return 0L;
    }


    @ApiOperation("生成短链接， 必须登录")
    @PostMapping(value = "/get-short-url")
    @ApiImplicitParam(name = "goods_id", value = "商品id", required = false, paramType = "query", dataType = "int")
    public SuccessMessage getShortUrl(@ApiIgnore Long goodsId) {

        Buyer buyer = UserContext.getBuyer();
        // 没登录不能生成短链接
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        try {
            long memberId = buyer.getUid();
            ShortUrlDO shortUrlDO = this.shortUrlManager.createShortUrl(memberId, goodsId);
            SuccessMessage successMessage = new SuccessMessage();
            successMessage.setMessage("/distribution/su/visit?su=" + shortUrlDO.getSu());
            return successMessage;
        } catch (Exception e) {
            logger.error("生成短连接出错", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

}

package com.enation.app.javashop.api.seller.distribution;

import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.distribution.dos.DistributionGoods;
import com.enation.app.javashop.model.distribution.dos.DistributionSetting;
import com.enation.app.javashop.service.distribution.DistributionGoodsManager;
import com.enation.app.javashop.framework.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分销商品
 * @author liushuai
 * @version v1.0
 * @since v7.0
 * 2018/8/30 上午9:42
 * @Description:
 *
 */
@RestController
@RequestMapping("/seller/distribution")
@Api(description = "分销商品API")
public class DistributionGoodsSellerController {


    @Autowired
    private DistributionGoodsManager distributionGoodsManager;


    @Autowired
    private SettingClient settingClient;

    @ApiOperation(value = "分销商品返利获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping(value = "/goods/{goods_id}")
    public DistributionGoods querySetting(@PathVariable("goods_id") Long goodsId) {
        return distributionGoodsManager.getModel(goodsId);
    }


    @ApiOperation(value = "分销商品返利设置")
    @PutMapping(value = "/goods")
    public DistributionGoods settingGoods(DistributionGoods distributionGoods) {
        return distributionGoodsManager.edit(distributionGoods);
    }


    @ApiOperation(value = "获取分销设置:1开启/0关闭")
    @GetMapping(value = "/setting")
    public SuccessMessage setting() {
        DistributionSetting ds = JsonUtil.jsonToObject(settingClient.get(SettingGroup.DISTRIBUTION),DistributionSetting.class);
        return new SuccessMessage(ds.getGoodsModel());
    }


}

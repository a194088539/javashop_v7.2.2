package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.pintuan.PinTuanGoodsVO;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrder;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanOrderManager;
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
 * Created by kingapex on 2019-01-22.
 * 拼团商品API
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2019-01-22
 */
@Api(description = "拼团商品API")
@RestController
@RequestMapping("/pintuan/goods")
public class PinTuanGoodsController {

    @Autowired
    private PintuanGoodsManager pintuanGoodsManager;

    @Autowired
    private PintuanOrderManager pintuanOrderManager;

    @GetMapping("/skus/{sku_id}")
    @ApiOperation(value = "获取某个拼团的详细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_id", value = "skuid", required = true, dataType = "long", paramType = "path")

    })
    public PinTuanGoodsVO detail(@ApiIgnore @PathVariable(name = "sku_id") Long skuId) {

        PinTuanGoodsVO pinTuanGoodsVO = pintuanGoodsManager.getDetail(skuId, null);

        return pinTuanGoodsVO;
    }

    @ApiOperation(value = "获取此商品拼团的所有参与的sku信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "pintuan_id", value = "拼团id", required = true, dataType = "long", paramType = "query")
    })
    @GetMapping("/{goods_id}/skus")
    public List<GoodsSkuVO> skus(@ApiIgnore @PathVariable(name = "goods_id") Long goodsId, @ApiIgnore Long pintuanId) {

        return pintuanGoodsManager.skus(goodsId, pintuanId);
    }


    @ApiOperation(value = "获取此商品待成团的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "sku_id", value = "skuid", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/{goods_id}/orders")
    public List<PintuanOrder> orders(@ApiIgnore @PathVariable(name = "goods_id") Long goodsId, @ApiIgnore Long skuId) {

        return pintuanOrderManager.getWaitOrder(goodsId, skuId);
    }


}

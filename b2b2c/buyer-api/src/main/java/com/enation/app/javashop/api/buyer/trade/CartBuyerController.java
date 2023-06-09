package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartView;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.enation.app.javashop.service.trade.cart.CartOriginDataManager;
import com.enation.app.javashop.service.trade.cart.CartReadManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

/**
 * 购物车接口
 *
 * @author Snow
 * @version v1.0
 * 2018年03月19日21:40:52
 * @since v7.0.0
 */
@Api(description = "购物车接口模块")
@RestController
@RequestMapping("/trade/carts")
@Validated
public class CartBuyerController {

    @Autowired
    private CartReadManager cartReadManager;


    @Autowired
    private CartOriginDataManager cartOriginDataManager;


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @ApiOperation(value = "向购物车中添加一个产品", response = CartSkuVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_id", value = "产品ID", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "此产品的购买数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "activity_id", value = "默认参与的活动id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "promotion_type", value = "活动类型", dataType = "string", paramType = "query")
    })
    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CartSkuOriginVo add(@ApiIgnore @NotNull(message = "产品id不能为空") Long skuId,
                               @ApiIgnore @NotNull(message = "购买数量不能为空") @Min(value = 1, message = "加入购物车数量必须大于0") Integer num,
                               @ApiIgnore Long activityId,
                               @ApiIgnore String promotionType) {
        return cartOriginDataManager.add(skuId, num, activityId,promotionType);
    }


    @ApiOperation(value = "立即购买", response = CartSkuVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_id", value = "产品ID", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "此产品的购买数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "activity_id", value = "默认参与的活动id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "promotion_type", value = "活动类型", dataType = "string", paramType = "query"),
    })
    @ResponseBody
    @PostMapping("/buy")
    public void buy(@ApiIgnore @NotNull(message = "产品id不能为空") Long skuId,
                    @ApiIgnore @NotNull(message = "购买数量不能为空") @Min(value = 1, message = "购买数量必须大于0") Integer num,
                    @ApiIgnore Long activityId,
                    @ApiIgnore String promotionType) {
        cartOriginDataManager.buy(skuId,num,activityId,promotionType);
     }


    @ApiOperation(value = "获取购物车页面购物车详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "结算页面方式，BUY_NOW：立即购买，CART：购物车", required = true, dataType = "String"),
    })
    @GetMapping("/all")
    public CartView cartAll(String way) {

        try{

            return this.cartReadManager.getCartListAndCountPrice(CheckedWay.valueOf(way));

        }catch (Exception e){
            logger.error("读取购物车异常",e);
            return new CartView(new ArrayList<>(),new PriceDetailVO());
        }

    }


    @ApiOperation(value = "获取结算页面购物车详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "way", value = "结算页面方式，BUY_NOW：立即购买，CART：购物车", required = true, dataType = "String"),
    })
    @GetMapping("/checked")
    public CartView cartChecked(String way) {

        try{

            // 读取选中的列表
           return  this.cartReadManager.getCheckedItems(CheckedWay.valueOf(way));

        }catch (Exception e){
            logger.error("读取结算页的购物车异常",e);
            return new CartView(new ArrayList<>(),new PriceDetailVO());
        }


    }


    @ApiOperation(value = "更新购物车中的多个产品", notes = "更新购物车中的多个产品的数量或选中状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_id", value = "产品id数组", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "checked", value = "是否选中", dataType = "int", paramType = "query", allowableValues = "0,1"),
            @ApiImplicitParam(name = "num", value = "产品数量", dataType = "int", paramType = "query"),
    })
    @ResponseBody
    @PostMapping(value = "/sku/{sku_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@ApiIgnore @NotNull(message = "产品id不能为空") @PathVariable(name = "sku_id") Long skuId,
                         @Min(value = 0) @Max(value = 1) Integer checked, Integer num) {
        if (checked != null) {
            cartOriginDataManager.checked(skuId, checked);

        } else if (num != null) {
            cartOriginDataManager.updateNum(skuId, num);

        }
    }


    @ApiOperation(value = "设置全部商为选中或不选中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checked", value = "是否选中", required = true, dataType = "int", paramType = "query", allowableValues = "0,1"),
    })
    @ResponseBody
    @PostMapping(value = "/checked", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateAll(@NotNull(message = "必须指定是否选中") @Min(value = 0, message = "是否选中参数异常") @Max(value = 1, message = "是否选中参数异常") Integer checked) {
        if (checked != null) {
           cartOriginDataManager.checkedAll(checked, CheckedWay.CART);
        }

    }


    @ApiOperation(value = "批量设置某商家的商品为选中或不选中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seller_id", value = "卖家id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "checked", value = "是否选中", required = true, dataType = "int", paramType = "query", allowableValues = "0,1"),
    })
    @ResponseBody
    @PostMapping(value = "/seller/{seller_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateSellerAll(@NotNull(message = "卖家id不能为空") @PathVariable(name = "seller_id") Long sellerId,
                                @NotNull(message = "必须指定是否选中") @Min(value = 0) @Max(value = 1) Integer checked) {
        if (checked != null && sellerId != null) {
            cartOriginDataManager.checkedSeller(sellerId,checked);
        }
    }


    @ApiOperation(value = "清空购物车")
    @DeleteMapping()
    public void clean() {
        cartOriginDataManager.clean();
    }


    @ApiOperation(value = "删除购物车中的一个或多个产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_ids", value = "产品id，多个产品可以用英文逗号：(,) 隔开", required = true, dataType = "int", paramType = "path", allowMultiple = true),
    })
    @DeleteMapping(value = "/{sku_ids}/sku")
    public void delete(@PathVariable(name = "sku_ids") Long[] skuIds) {

        if (skuIds.length == 0) {
            throw new ServiceException(TradeErrorCode.E455.code(), "参数异常");
        }
        cartOriginDataManager.delete(skuIds,CheckedWay.CART);

    }

}

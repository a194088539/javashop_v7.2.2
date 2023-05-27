package com.enation.app.javashop.api.seller.goods;

import java.util.List;

import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.service.goods.GoodsQueryManager;
import com.enation.app.javashop.service.goods.GoodsSkuManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 商品sku控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:48:40
 */
@RestController
@RequestMapping("/seller/goods")
@Api(description = "商品sku相关API")
public class GoodsSkuSellerController {

	@Autowired
	private GoodsQueryManager goodsQueryManager;

	@Autowired
	private GoodsSkuManager goodsSkuManager;

	@ApiOperation(value = "商品sku信息信息获取api")
	@ApiImplicitParam(name	= "goods_id", value = "商品id",	required = true, dataType = "int",	paramType =	"path")
	@GetMapping("/{goods_id}/skus")
	public List<GoodsSkuVO> queryByGoodsId(@PathVariable(name = "goods_id") Long goodsId) {

		CacheGoods goods = goodsQueryManager.getFromCache(goodsId);

		return  goods.getSkuList();
	}

	@ApiOperation(value = "更新商品的sku信息")
	@ApiImplicitParam(name	= "goods_id", value = "商品id",	required = true, dataType = "int",	paramType =	"path")
	@PutMapping("/{goods_id}/skus")
	public List<GoodsSkuVO> updateGoodsSku(@PathVariable(name = "goods_id") Long goodsId,@RequestBody List<GoodsSkuVO> skuList){

		goodsSkuManager.editSkus(skuList,goodsId);

		return  skuList;
	}

	@GetMapping(value = "/skus/{sku_ids}/details")
	@ApiOperation(value = "查询多个商品的基本信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sku_ids", value = "要查询的SKU主键", required = true, dataType = "int", paramType = "path",allowMultiple = true) })
	public List<GoodsSkuVO> getGoodsDetail(@PathVariable("sku_ids") Long[] skuIds) {

		return this.goodsSkuManager.query(skuIds);
	}


	@ApiOperation(value = "查询SKU列表", response = GoodsSkuVO.class)
	@GetMapping("/skus")
	public WebPage list(GoodsQueryParam param, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
		param.setPageNo(pageNo);
		param.setPageSize(pageSize);
		param.setSellerId(UserContext.getSeller().getSellerId());
		return this.goodsSkuManager.list(param);
	}

}

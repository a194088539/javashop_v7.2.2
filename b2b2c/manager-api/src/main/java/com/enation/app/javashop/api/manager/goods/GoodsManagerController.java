package com.enation.app.javashop.api.manager.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dto.GoodsAuditParam;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.service.goods.GoodsManager;
import com.enation.app.javashop.service.goods.GoodsQueryManager;
import com.enation.app.javashop.service.goods.GoodsSkuManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 商品控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@RestController
@RequestMapping("/admin/goods")
@Api(description = "商品相关API")
@Validated
public class GoodsManagerController {

	@Autowired
	private GoodsManager goodsManager;
	@Autowired
	private GoodsQueryManager goodsQueryManager;
	@Autowired
	private GoodsSkuManager goodsSkuManager;

	@ApiOperation(value = "查询商品或者审核列表")
	@GetMapping
	public WebPage list(GoodsQueryParam param, @ApiIgnore Long pageSize, @ApiIgnore Long pageNo) {

		param.setPageNo(pageNo);
		param.setPageSize(pageSize);
		return this.goodsQueryManager.list(param);
	}

	@ApiOperation(value = "查询SKU列表", response = GoodsSkuVO.class)
	@GetMapping("/skus")
	public WebPage skus(GoodsQueryParam param, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
		param.setPageNo(pageNo);
		param.setPageSize(pageSize);
		return this.goodsSkuManager.list(param);
	}


	@ApiOperation(value = "管理员下架商品",notes = "管理员下架商品时使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_id",value="商品ID",required=true,paramType="path",dataType="int"),
		@ApiImplicitParam(name="reason",value="下架理由",required=true,paramType="query",dataType="string")
	})
	@PutMapping(value = "/{goods_id}/under")
	public String underGoods(@PathVariable("goods_id") Long goodsId,@NotEmpty(message = "下架原因不能为空") String reason){

		this.goodsManager.under(new Long[]{goodsId},reason,Permission.ADMIN);

		return null;
	}

	@ApiOperation(value = "管理员上架商品",notes = "管理员上架商品时使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_id",value="商品ID",required=true,paramType="path",dataType="int"),
	})
	@PutMapping(value = "/{goods_id}/up")
	public String unpGoods(@PathVariable("goods_id") Long goodsId){

		this.goodsManager.up(goodsId);

		return null;
	}


	@ApiOperation(value = "管理员批量审核商品",notes = "审核商品时使用")
	@PostMapping(value = "/batch/audit")
	public String batchAudit(@Valid @RequestBody GoodsAuditParam param) {

		this.goodsManager.batchAuditGoods(param);

		return null;
	}

	@GetMapping(value = "/{goods_ids}/details")
	@ApiOperation(value = "查询多个商品的基本信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "goods_ids", value = "要查询的商品主键", required = true, dataType = "int", paramType = "path",allowMultiple = true),
			@ApiImplicitParam(name = "param", value = "查询条件参数", dataType = "GoodsQueryParam", paramType = "query")
	})
	public List<GoodsSelectLine> getGoodsDetail(@PathVariable("goods_ids") Long[] goodsIds, GoodsQueryParam param) {

		return this.goodsQueryManager.queryGoodsLines(goodsIds, param);
	}

	@GetMapping(value = "/skus/{sku_ids}/details")
	@ApiOperation(value = "查询多个商品的基本信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sku_ids", value = "要查询的SKU主键", required = true, dataType = "int", paramType = "path",allowMultiple = true) })
	public List<GoodsSkuVO> getGoodsSkuDetail(@PathVariable("sku_ids") Long[] skuIds) {

		return this.goodsSkuManager.query(skuIds);
	}

}

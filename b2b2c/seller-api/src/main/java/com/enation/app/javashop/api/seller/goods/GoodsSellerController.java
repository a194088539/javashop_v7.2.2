package com.enation.app.javashop.api.seller.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.GoodsVO;
import com.enation.app.javashop.service.goods.GoodsManager;
import com.enation.app.javashop.service.goods.GoodsQueryManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@RestController
@RequestMapping("/seller/goods")
@Api(description = "商品相关API")
@Validated
@Scope("request")
public class GoodsSellerController {

	@Autowired
	private GoodsQueryManager goodsQueryManager;

	@Autowired
	private GoodsManager goodsManager;

	@ApiOperation(value = "查询商品列表", response = GoodsDO.class)
	@GetMapping
	public WebPage list(GoodsQueryParam param, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {

		param.setPageNo(pageNo);
		param.setPageSize(pageSize);

		Seller seller = UserContext.getSeller();
		param.setSellerId(seller.getSellerId());
		return this.goodsQueryManager.list(param);
	}

	@ApiOperation(value = "查询预警商品列表", response = GoodsDO.class)
	@GetMapping("/warning")
	public WebPage warningList(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, GoodsQueryParam param){

		param.setPageNo(pageNo);
		param.setPageSize(pageSize);

		Seller seller = UserContext.getSeller();
		param.setSellerId(seller.getSellerId());
		return this.goodsQueryManager.warningGoodsList(param);

	}


	@ApiOperation(value = "添加商品", response = GoodsDO.class)
	@ApiImplicitParam(name = "goods", value = "商品信息", required = true, dataType = "GoodsDTO", paramType = "body")
	@PostMapping
	public GoodsDO add(@ApiIgnore @Valid @RequestBody GoodsDTO goods) {
		GoodsDO  goodsDO = this.goodsManager.add(goods);
		return goodsDO;
	}

	@PutMapping(value = "/{id}")
	@ApiOperation(value = "修改商品", response = GoodsDO.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
		@ApiImplicitParam(name = "goods", value = "商品信息", required = true, dataType = "GoodsDTO", paramType = "body")
	})
	public GoodsDO edit(@Valid @RequestBody GoodsDTO goods, @PathVariable Long id) {
		GoodsDO goodsDO = this.goodsManager.edit(goods, id);
		return goodsDO;
	}

	@GetMapping(value = "/{id}")
	@ApiOperation(value = "查询一个商品,商家编辑时使用")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "要查询的商品主键", required = true, dataType = "int", paramType = "path") })
	public GoodsVO get(@PathVariable Long id) {

		GoodsVO goods = this.goodsQueryManager.sellerQueryGoods(id);

		return goods;
	}

	@ApiOperation(value = "商家下架商品",notes = "商家下架商品时使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_ids",value="商品ID集合",required=true,paramType="path",dataType="int",allowMultiple=true)
	})
	@PutMapping(value = "/{goods_ids}/under")
	public String underGoods(@PathVariable("goods_ids") Long[] goodsIds,String reason){
		if(StringUtil.isEmpty(reason)){
			reason = "自行下架，无原因";
		}
		this.goodsManager.under(goodsIds,reason,Permission.SELLER);

		return null;
	}

	@ApiOperation(value = "商家将商品放入回收站",notes = "下架的商品才能放入回收站")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_ids",value="商品ID集合",required=true,paramType="path",dataType="int",allowMultiple=true)
	})
	@PutMapping(value = "/{goods_ids}/recycle")
	public String deleteGoods(@PathVariable("goods_ids") Long[] goodsIds){

		this.goodsManager.inRecycle(goodsIds);

		return null;
	}

	@ApiOperation(value = "商家还原商品",notes = "商家回收站回收商品时使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_ids",value="商品ID集合",required=true,paramType="path",dataType="int",allowMultiple=true)
	})
	@PutMapping(value="/{goods_ids}/revert")
	public String revertGoods(@PathVariable("goods_ids") Long[] goodsIds){

		this.goodsManager.revert(goodsIds);

		return null;
	}

	@ApiOperation(value = "商家彻底删除商品",notes = "商家回收站删除商品时使用")
	@ApiImplicitParams({
		@ApiImplicitParam(name="goods_ids",value="商品ID集合",required=true,paramType="path",dataType="int",allowMultiple=true)
	})
	@DeleteMapping(value="/{goods_ids}")
	public String cleanGoods(@PathVariable("goods_ids") Long[] goodsIds){

		this.goodsManager.delete(goodsIds);

		return "";
	}


	@GetMapping(value = "/{goods_ids}/details")
	@ApiOperation(value = "查询多个商品的基本信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "goods_ids", value = "要查询的商品主键", required = true, dataType = "int", paramType = "path",allowMultiple = true),
			@ApiImplicitParam(name = "param", value = "查询条件参数", dataType = "GoodsQueryParam", paramType = "query")
	})
	public List<GoodsSelectLine> getGoodsDetail(@PathVariable("goods_ids") Long[] goodsIds, GoodsQueryParam param) {

		Seller seller = UserContext.getSeller();
		param.setSellerId(seller.getSellerId());
		return this.goodsQueryManager.queryGoodsLines(goodsIds, param);
	}

}

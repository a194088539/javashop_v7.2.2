package com.enation.app.javashop.api.seller.promotion;

import com.enation.app.javashop.model.promotion.halfprice.dos.HalfPriceDO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.security.model.Seller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 第二件半价控制器
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 19:53:42
 */
@RestController
@RequestMapping("/seller/promotion/half-prices")
@Api(description = "第二件半价相关API")
@Validated
public class HalfPriceSellerController	{

	@Autowired
	private HalfPriceManager halfPriceManager;


	@ApiOperation(value	= "查询第二件半价列表", response = HalfPriceDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码",dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量", dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "keywords",	value =	"关键字", dataType = "String",	paramType =	"query"),
	})
	@GetMapping
	public WebPage<HalfPriceVO> list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String keywords)	{

		return	this.halfPriceManager.list(pageNo,pageSize,keywords);
	}


	@ApiOperation(value	= "添加第二件半价", response = HalfPriceVO.class)
	@PostMapping
	public HalfPriceVO add(@Valid @RequestBody HalfPriceVO halfPrice)	{

		PromotionValid.paramValid(halfPrice.getStartTime(),halfPrice.getEndTime(),
				halfPrice.getRangeType(),halfPrice.getGoodsList());

		// 获取当前登录的店铺ID
		Seller seller = UserContext.getSeller();
		Long sellerId = seller.getSellerId();
		halfPrice.setSellerId(sellerId);

		this.halfPriceManager.add(halfPrice);
		return	halfPrice;
	}


	@PutMapping(value = "/{id}")
	@ApiOperation(value	= "修改第二件半价", response = HalfPriceDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	HalfPriceVO edit(@Valid @RequestBody HalfPriceVO halfPrice, @PathVariable Long id) {

		PromotionValid.paramValid(halfPrice.getStartTime(),halfPrice.getEndTime(),
				halfPrice.getRangeType(),halfPrice.getGoodsList());

		halfPrice.setHpId(id);
		this.halfPriceManager.verifyAuth(id);
		this.halfPriceManager.edit(halfPrice,id);

		return	halfPrice;
	}


	@DeleteMapping(value = "/{id}")
	@ApiOperation(value	= "删除第二件半价")
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"要删除的第二件半价主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	String	delete(@PathVariable Long id) {

		this.halfPriceManager.verifyAuth(id);
		this.halfPriceManager.delete(id);

		return "";
	}


	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个第二件半价")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的第二件半价主键",	required = true, dataType = "int",	paramType = "path")
	})
	public	HalfPriceVO get(@PathVariable Long id)	{

		HalfPriceVO halfPrice = this.halfPriceManager.getFromDB(id);
		Seller seller = UserContext.getSeller();

		//验证越权操作
		if (halfPrice == null || !seller.getSellerId().equals(halfPrice.getSellerId())){
			throw new NoPermissionException("无权操作");
		}


		return	halfPrice;
	}

}

package com.enation.app.javashop.api.seller.promotion;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.minus.dos.MinusDO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.service.promotion.minus.MinusManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.model.util.PromotionValid;
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
 * 单品立减控制器
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 19:52:27
 */
@RestController
@RequestMapping("/seller/promotion/minus")
@Api(description = "单品立减相关API")
@Validated
public class MinusSellerController {

	@Autowired
	private MinusManager minusManager;


	@ApiOperation(value	= "查询单品立减列表", response = MinusDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码", dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量", dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "keywords",	value =	"关键字", dataType = "String",	paramType =	"query"),
	})
	@GetMapping
	public WebPage<MinusVO> list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, String keywords)	{
		return	this.minusManager.list(pageNo,pageSize,keywords);
	}


	@ApiOperation(value	= "添加单品立减", response = MinusVO.class)
	@ApiImplicitParam(name = "minus", value = "单品立减信息", required = true, dataType = "MinusVO", paramType = "body")
	@PostMapping
	public MinusVO add(@ApiIgnore @Valid @RequestBody MinusVO minus) {

		PromotionValid.paramValid(minus.getStartTime(),minus.getEndTime(),minus.getRangeType(),minus.getGoodsList());
		// 获取当前登录的店铺ID
		Seller seller = UserContext.getSeller();
		Long sellerId = seller.getSellerId();
		minus.setSellerId(sellerId);
		this.minusManager.add(minus);
		return minus;

	}

	@PutMapping(value = "/{id}")
	@ApiOperation(value	= "修改单品立减", response = MinusVO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	MinusVO edit(@Valid @RequestBody MinusVO minus, @PathVariable Long id) {

		this.minusManager.verifyAuth(id);
		PromotionValid.paramValid(minus.getStartTime(),minus.getEndTime(),minus.getRangeType(),minus.getGoodsList());
		minus.setMinusId(id);
		this.minusManager.edit(minus,id);

		return	minus;
	}


	@DeleteMapping(value = "/{id}")
	@ApiOperation(value	= "删除单品立减")
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"要删除的单品立减主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	String	delete(@PathVariable Long id) {

		this.minusManager.verifyAuth(id);
		this.minusManager.delete(id);

		return "";
	}


	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个单品立减")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的单品立减主键",	required = true, dataType = "int",	paramType = "path")
	})
	public	MinusVO get(@PathVariable Long id)	{

		MinusVO minusVO = this.minusManager.getFromDB(id);
		Seller seller = UserContext.getSeller();

		//验证越权操作
		if (minusVO == null || !seller.getSellerId().equals(minusVO.getSellerId()) ){
			throw new NoPermissionException("无权操作");
		}

		return	minusVO;
	}


}

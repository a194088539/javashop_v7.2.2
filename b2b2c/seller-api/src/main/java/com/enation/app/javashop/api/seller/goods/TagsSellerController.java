package com.enation.app.javashop.api.seller.goods;

import com.enation.app.javashop.model.goods.dos.TagsDO;
import com.enation.app.javashop.service.goods.TagsManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 商品标签控制器
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-28 14:49:36
 */
@RestController
@RequestMapping("/seller/goods/tags")
@Api(description = "商品标签相关API")
public class TagsSellerController	{

	@Autowired
	private	TagsManager tagsManager;


	@ApiOperation(value	= "查询商品标签列表", response = TagsDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码",	required = true, dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量",	required = true, dataType = "int",	paramType =	"query")
	})
	@GetMapping
	public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize)	{

		return	this.tagsManager.list(pageNo,pageSize);
	}

	@ApiOperation(value	= "查询某标签下的商品")
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码",	required = true, dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量",	required = true, dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "tag_id",	value =	"标签id",	required = true, dataType = "int",	paramType =	"path")
	})
	@GetMapping("/{tag_id}/goods")
	public WebPage listGoods(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @PathVariable("tag_id") Long tagId){

		return this.tagsManager.queryTagGoods(tagId,pageNo,pageSize);
	}

	@ApiOperation(value	= "保存某标签下的商品")
	@ApiImplicitParams({
		@ApiImplicitParam(name	= "tag_id",	value =	"标签id",	required = true, dataType = "int",	paramType =	"path"),
		@ApiImplicitParam(name	= "goods_ids",	value =	"要保存的商品id",	required = true, dataType = "int",	paramType =	"path"),
	})
	@PutMapping("/{tag_id}/goods/{goods_ids}")
	public String saveGoods( @PathVariable("tag_id") Long tagId,@PathVariable("goods_ids")Long[] goodsIds){

		this.tagsManager.saveTagGoods(tagId,goodsIds);

		return null;
	}




}

package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.trade.snapshot.SnapshotVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import com.enation.app.javashop.service.trade.snapshot.GoodsSnapshotManager;

/**
 * 交易快照控制器
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-01 14:55:26
 */
@RestController
@RequestMapping("/trade/snapshots")
@Api(description = "交易快照相关API")
@Validated
public class GoodsSnapshotBuyerController	{

	@Autowired
	private	GoodsSnapshotManager goodsSnapshotManager;


	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个交易快照")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的交易快照主键",	required = true, dataType = "int",	paramType = "path"),
	})
	public SnapshotVO get(@PathVariable	Long	id)	{

		SnapshotVO goodsSnapshot = this.goodsSnapshotManager.get(id, Permission.BUYER.name());

		return	goodsSnapshot;
	}

}

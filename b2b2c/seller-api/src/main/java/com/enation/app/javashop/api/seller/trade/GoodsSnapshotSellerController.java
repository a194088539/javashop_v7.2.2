package com.enation.app.javashop.api.seller.trade;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.trade.snapshot.SnapshotVO;
import com.enation.app.javashop.service.trade.snapshot.GoodsSnapshotManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易快照控制器
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-01 14:55:26
 */
@RestController
@RequestMapping("/seller/trade/snapshots")
@Api(description = "交易快照相关API")
@Validated
public class GoodsSnapshotSellerController {

	@Autowired
	private	GoodsSnapshotManager goodsSnapshotManager;


	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个交易快照")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的交易快照主键",	required = true, dataType = "int",	paramType = "path"),
	})
	public SnapshotVO get(@PathVariable	Long	id)	{

		SnapshotVO goodsSnapshot = this.goodsSnapshotManager.get(id, Permission.SELLER.name());

		return	goodsSnapshot;
	}

}

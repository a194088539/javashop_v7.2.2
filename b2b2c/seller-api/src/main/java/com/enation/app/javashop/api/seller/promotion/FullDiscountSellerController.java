package com.enation.app.javashop.api.seller.promotion;

import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountDO;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.model.util.PromotionValid;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
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
 * 满优惠活动控制器
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 17:34:32
 */
@RestController
@RequestMapping("/seller/promotion/full-discounts")
@Api(description = "满优惠活动相关API")
@Validated
public class FullDiscountSellerController	{

	@Autowired
	private FullDiscountManager fullDiscountManager;


	@ApiOperation(value	= "查询满优惠活动列表", response = FullDiscountDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码",	dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量",	 dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "keywords",	value =	"关键字", dataType = "String",	paramType =	"query"),
	})
	@GetMapping
	public WebPage<FullDiscountVO> list(@ApiIgnore  Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String keywords)	{

		return	this.fullDiscountManager.list(pageNo,pageSize,keywords);
	}


	@ApiOperation(value	= "添加满优惠活动", response = FullDiscountVO.class)
	@PostMapping
	public FullDiscountVO add(@Valid @RequestBody FullDiscountVO fullDiscountVO)	{

		this.verifyFullDiscountParam(fullDiscountVO);
		// 获取当前登录的店铺ID
		Seller seller = UserContext.getSeller();
		Long sellerId = seller.getSellerId();
		fullDiscountVO.setSellerId(sellerId);
		this.fullDiscountManager.add(fullDiscountVO);

		return	fullDiscountVO;
	}

	@PutMapping(value = "/{id}")
	@ApiOperation(value	= "修改满优惠活动", response = FullDiscountDO.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	FullDiscountVO edit(@Valid @RequestBody FullDiscountVO fullDiscountVO, @PathVariable Long id) {

		fullDiscountVO.setFdId(id);
		this.verifyFullDiscountParam(fullDiscountVO);
		this.fullDiscountManager.verifyAuth(id);
		this.fullDiscountManager.edit(fullDiscountVO,id);

		return	fullDiscountVO;
	}


	@DeleteMapping(value = "/{id}")
	@ApiOperation(value	= "删除满优惠活动")
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"要删除的满优惠活动主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	String	delete(@PathVariable Long id) {

		this.fullDiscountManager.verifyAuth(id);
		this.fullDiscountManager.delete(id);

		return "";
	}


	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个满优惠活动")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的满优惠活动主键",	required = true, dataType = "int",	paramType = "path")
	})
	public	FullDiscountVO get(@PathVariable Long id)	{

		FullDiscountVO fullDiscount = this.fullDiscountManager.getModel(id);

		Seller seller = UserContext.getSeller();
		//验证越权操作
		if (fullDiscount == null || !seller.getSellerId().equals(fullDiscount.getSellerId())){
			throw new NoPermissionException("无权操作");
		}

		return	fullDiscount;
	}


	/**
	 * 验证满优惠活动的参数信息
	 * @param fullDiscountVO
	 */
	private void verifyFullDiscountParam(FullDiscountVO fullDiscountVO){

		PromotionValid.paramValid(fullDiscountVO.getStartTime(),fullDiscountVO.getEndTime(),
				fullDiscountVO.getRangeType(),fullDiscountVO.getGoodsList());

		// 促销活动的优惠方式不能全部为空，至少要选择一项
		if (fullDiscountVO.getIsFullMinus() == null && fullDiscountVO.getIsFreeShip() == null && fullDiscountVO.getIsSendGift() == null
				&& fullDiscountVO.getIsSendBonus() == null && fullDiscountVO.getIsDiscount() == null) {

			throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"请选择优惠方式");
		}

		// 如果促销活动优惠详细是否包含满减不为空
		if (fullDiscountVO.getIsFullMinus() != null && fullDiscountVO.getIsFullMinus() == 1) {

			if (fullDiscountVO.getMinusValue() == null || fullDiscountVO.getMinusValue() == 0) {
				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"请填写减多少元");
			}

			// 减少的现金必须小于优惠门槛
			if (fullDiscountVO.getMinusValue() > fullDiscountVO.getFullMoney()) {
				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"减少的金额不能大于门槛金额");
			}

		}

		// 如果促销活动优惠详细是否包含满送赠品不为空
		if (fullDiscountVO.getIsSendGift() != null && fullDiscountVO.getIsSendGift() == 1) {
			// 赠品id不能为0
			if (fullDiscountVO.getGiftId() == null || fullDiscountVO.getGiftId() == 0) {
				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"请选择赠品");
			}
		}

		// 如果促销活动优惠详细是否包含满送优惠券不为空
		if (fullDiscountVO.getIsSendBonus() != null && fullDiscountVO.getIsSendBonus() == 1) {
			// 优惠券id不能为0
			if (fullDiscountVO.getBonusId() == null || fullDiscountVO.getBonusId() == 0) {
				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"请选择优惠券");
			}
		}

		// 如果促销活动优惠详细是否包含打折不为空
		if (fullDiscountVO.getIsDiscount() != null && fullDiscountVO.getIsDiscount() == 1) {
			// 打折的数值不能为空也不能为0
			if (fullDiscountVO.getDiscountValue() == null || fullDiscountVO.getDiscountValue() == 0) {

				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"请填写打折数值");
			}
			// 打折的数值应介于0-10之间
			if (fullDiscountVO.getDiscountValue() >= 10 || fullDiscountVO.getDiscountValue() <= 0) {
				throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"打折的数值应介于0到10之间");
			}
		}

	}
}

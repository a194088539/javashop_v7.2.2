package com.enation.app.javashop.api.buyer.goods;

import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsParamsGroupVO;
import com.enation.app.javashop.model.goods.vo.GoodsShowDetail;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.util.IPUtil;
import com.enation.app.javashop.service.goods.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@RestController
@RequestMapping("/goods")
@Api(description = "商品相关API")
public class GoodsBuyerController {

	@Autowired
	private GoodsQueryManager goodsQueryManager;
	@Autowired
	private GoodsManager goodsManager;
	@Autowired
	private GoodsParamsManager goodsParamsManager;
	@Autowired
	private CategoryManager categoryManager;
	@Autowired
	private GoodsGalleryManager goodsGalleryManager;
	@Autowired
	private GoodsSkuManager goodsSkuManager;

	@GetMapping("/ip")
	public Map ip() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		String hostName=addr.getHostName();
		Map map  = new HashMap();

		IPUtil.getIpAdrress();
		map.put("ip",IPUtil.getIpAdrress());
		map.put("hostName",hostName);
		return map;

	}

    @ApiOperation(value = "浏览商品的详情,静态部分使用")
    @ApiImplicitParam(name = "goods_id", value = "分类id，顶级为0", required = true, dataType = "long", paramType = "path")
    @GetMapping("/{goods_id}")
    public GoodsShowDetail getGoodsDetail(@PathVariable("goods_id") Long goodsId) {

		GoodsDO goods = goodsQueryManager.getModel(goodsId);
		GoodsShowDetail detail = new GoodsShowDetail();
		if (goods == null){
			throw new ResourceNotFoundException("不存在此商品");
		}
		BeanUtils.copyProperties(goods,detail);
		Integer goodsOff = 0;
		//商品不存在，直接返回
		if(goods == null){
			detail.setGoodsOff(goodsOff);
			return detail;
		}
		//分类
		CategoryDO category = categoryManager.getModel(goods.getCategoryId());
		detail.setCategoryName(category == null ? "":category.getName());
		//上架状态
		if(goods.getMarketEnable()==1){
			goodsOff = 1;
		}
		detail.setGoodsOff(goodsOff);
		//参数
		List<GoodsParamsGroupVO> list = this.goodsParamsManager.queryGoodsParams(goods.getCategoryId(),goodsId);
		detail.setParamList(list);
		//相册
		List<GoodsGalleryDO> galleryList = goodsGalleryManager.list(goodsId);
		detail.setGalleryList(galleryList);

		//商品好平率
		detail.setGrade(goodsQueryManager.getGoodsGrade(goodsId));

		return detail;
	}

    @ApiOperation(value = "获取sku信息，商品详情页动态部分")
    @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/{goods_id}/skus")
    public List<GoodsSkuVO> getGoodsSkus(@PathVariable("goods_id") Long goodsId) {

        CacheGoods goods = goodsQueryManager.getFromCache(goodsId);

        return goods.getSkuList();
    }

    @ApiOperation(value = "记录浏览器商品次数", notes = "记录浏览器商品次数")
    @ApiImplicitParam(name = "goods_id", value = "商品ID", required = true, paramType = "path", dataType = "long")
    @GetMapping(value = "/{goods_id}/visit")
    public Integer visitGoods(@PathVariable("goods_id") Long goodsId) {

        return this.goodsManager.visitedGoodsNum(goodsId);
    }

	@ApiOperation(value = "查看商品是否在配送区域 1 有货  0 无货", notes = "查看商品是否在配送区域")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "goods_id", value = "商品ID", required = true, paramType = "path", dataType = "long"),
			@ApiImplicitParam(name = "area_id", value = "地区ID", required = true, paramType = "path", dataType = "long")
	})
	@GetMapping(value = "/{goods_id}/area/{area_id}")
	public Integer checkGoodsArea(@PathVariable("goods_id") Long goodsId,@PathVariable("area_id") Long areaId) {

		return this.goodsQueryManager.checkArea(goodsId,areaId);
	}

	@ApiOperation(value = "根据skuid查询goods_id")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sku_id", value = "sku_id", required = true, paramType = "path", dataType = "int")
	})
	@GetMapping(value = "/sku/{sku_id}/goods-id")
	public Long queryGoods(@PathVariable("sku_id") Long skuId) {

		GoodsSkuDO sku = this.goodsSkuManager.getModel(skuId);
		if (sku != null){
			return sku.getGoodsId();
		}
		return null;
	}


}

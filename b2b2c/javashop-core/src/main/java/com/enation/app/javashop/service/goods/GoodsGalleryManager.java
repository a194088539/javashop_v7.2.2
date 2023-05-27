package com.enation.app.javashop.service.goods;

import java.util.List;

import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;

/**
 * 商品相册业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:39:54
 */
public interface GoodsGalleryManager {


	/**
	 * 添加商品相册
	 *
	 * @param goodsGallery
	 *            商品相册
	 * @return GoodsGallery 商品相册
	 */
	GoodsGalleryDO add(GoodsGalleryDO goodsGallery);

	/**
	 * 修改商品相册
	 *
	 * @param goodsGallery
	 *            商品相册
	 * @param id
	 *            商品相册主键
	 * @return GoodsGallery 商品相册
	 */
	GoodsGalleryDO edit(GoodsGalleryDO goodsGallery, Long id);

	/**
	 * 删除商品相册
	 *
	 * @param id
	 *            商品相册主键
	 */
	void delete(Long id);

	/**
	 * 获取商品相册
	 *
	 * @param id
	 *            商品相册主键
	 * @return GoodsGallery 商品相册
	 */
	GoodsGalleryDO getModel(Long id);

	/**
	 * 使用原始图片得到商品的其他规格的图片格式
	 *
	 * @param origin 图片原始路径
	 * @return
	 */
	GoodsGalleryDO getGoodsGallery(String origin);

	/**
	 * 添加商品的相册
	 *
	 * @param goodsGalleryList 图片集合
	 * @param goodsId 商品id
	 */
	void add(List<GoodsGalleryDO> goodsGalleryList, Long goodsId);

	/**
	 * 修改某商品的相册
	 * @param goodsGalleryList 图片集合
	 * @param goodsId 商品id
	 */
	void edit(List<GoodsGalleryDO> goodsGalleryList, Long goodsId);

	/**
	 * 查询某商品的相册
	 * @param goodsId 商品id
	 * @return
	 */
	List<GoodsGalleryDO> list(Long goodsId);

	/**
	 * 删除商品关联的相册
	 * @param goodsIds 商品id数组
	 */
	void delete(Long[] goodsIds);

}

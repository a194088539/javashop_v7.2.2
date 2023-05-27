package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsAuditParam;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.goods.vo.BackendGoodsVO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;

import java.util.List;

/**
 * 商品业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
public interface GoodsManager {


    /**
     * 修改商品
     *
     * @param goodsVo 商品
     * @param id      商品主键
     * @return Goods 商品
     */
    GoodsDO edit(GoodsDTO goodsVo, Long id);


    /**
     * 添加商品
     *
     * @param goodsVo 商品对象
     * @return
     */
    GoodsDO add(GoodsDTO goodsVo);

    /**
     * 商品下架
     *
     * @param goodsIds 商品id数组
     * @param reason 下架理由
     * @param permission 权限
     */
    void under(Long[] goodsIds, String reason, Permission permission);

    /**
     * 商品放入回收站
     *
     * @param goodsIds 商品id数组
     */
    void inRecycle(Long[] goodsIds);

    /**
     * 商品删除
     *
     * @param goodsIds 商品id数组
     */
    void delete(Long[] goodsIds);

    /**
     * 回收站还原商品
     *
     * @param goodsIds 商品id数组
     */
    void revert(Long[] goodsIds);

    /**
     * 上架商品
     *
     * @param goodsId 商品id
     */
    void up(Long goodsId);

    /**
     * 批量审核商品
     * 管理员使用
     * @param param 参数
     */
    void batchAuditGoods(GoodsAuditParam param);

    /**
     * 增加商品的浏览次数
     *
     * @param goodsId 商品id
     * @return
     */
    Integer visitedGoodsNum(Long goodsId);


    /**
     * 获取新赠商品
     *
     * @param length 长度
     * @return
     */
    List<BackendGoodsVO> newGoods(Integer length);

    /**
     * 下架某店铺的全部商品
     *
     * @param sellerId 商家id
     */
    void underShopGoods(Long sellerId);

    /**
     * 更新商品好平率
     */
    void updateGoodsGrade();

    /**
     * 获取商品是否使用检测的模版
     *
     * @param templateId 运费模板id
     * @return 商品
     */
    GoodsDO checkShipTemplate(Long templateId);

    /**
     * 修改某店铺商品店铺名称
     *
     * @param sellerId   商家id
     * @param sellerName 商家名称
     */
    void changeSellerName(Long sellerId, String sellerName);

    /**
     * 更改商品类型
     *
     * @param sellerId 商家id
     * @param type 类型
     */
    void updateGoodsType(Long sellerId, String type);

    /**
     * 修改商品优先级别
     * @param goodsId 商品id
     * @param priority 优先级
     */
    void updatePriority(Long goodsId,Integer priority);

    /**
     * 清除商品的关联<br/>
     * 在商品删除、下架要进行调用
     *
     * @param goodsId 商品id
     * @param markenable 上架
     */
    void cleanGoodsAssociated(long goodsId, Integer markenable);

    /**
     * 更新商品的评论数量
     * @param goodsId 商品id
     * @param num 数量
     */
    void updateCommentCount(Long goodsId, Integer num);

    /**
     * 更新商品的购买数量
     * @param list
     */
    void updateBuyCount(List<OrderSkuVO> list);
}

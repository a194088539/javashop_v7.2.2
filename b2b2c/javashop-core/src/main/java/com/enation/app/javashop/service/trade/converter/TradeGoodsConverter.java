package com.enation.app.javashop.service.trade.converter;

import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.TradeConvertGoodsSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.TradeConvertGoodsVO;
import com.enation.app.javashop.framework.util.BeanUtil;


/**
 * 转换goods包下，此包使用到的model及字段
 *
 * @author Snow create in 2018/3/20
 * @version v2.0
 * @since v7.0.0
 */
public class TradeGoodsConverter {

    /**
     * 转换goodsVO对象
     * @return
     */
    public static TradeConvertGoodsVO goodsVOConverter(CacheGoods goods){
        TradeConvertGoodsVO goodsVO = new TradeConvertGoodsVO();
        goodsVO.setIsAuth(goods.getIsAuth());
        goodsVO.setTemplateId(goods.getTemplateId());
        goodsVO.setLastModify(goods.getLastModify());
        return goodsVO;
    }

    /**
     * 转换goodsSkuVO对象
     * @return
     */
    public static TradeConvertGoodsSkuVO goodsSkuVOConverter(GoodsSkuVO goodsSkuVO){
        TradeConvertGoodsSkuVO skuVO = new TradeConvertGoodsSkuVO();
        BeanUtil.copyProperties(goodsSkuVO,skuVO);
        return skuVO;
    }
}

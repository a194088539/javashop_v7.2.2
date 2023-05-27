package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.model.trade.cart.enums.CartType;
import com.enation.app.javashop.model.trade.cart.vo.CartPromotionVo;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.SelectedPromotionVo;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartPromotionRenderer;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kingapex on 2018/12/10.
 * 购物促销信息渲染实现
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/10
 */
@Service("cartPromotionRendererImpl")
public class CartPromotionRendererImpl implements CartPromotionRenderer {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private CartPromotionManager cartPromotionManager;


    @Autowired
    private ScriptProcess scriptProcess;



    @Override
    public void render(List<CartVO> cartList) {

        renderPromotion(cartList);
        logger.debug("购物车处理完促销规则结果为：");
        logger.debug(cartList);
    }

    /**
     * 渲染促销提示信息
     * @param cartList
     */
    private void renderPromotion(List<CartVO> cartList){
        //循环购物车，取出购物车所有sku
        List<CartSkuVO> cartSkuVOS = new ArrayList<>();
        for (CartVO cartVO:cartList) {
            cartSkuVOS.addAll(cartVO.getSkuList());
        }
        //首先检测已使用的活动是否失效，如果失效则移除
        this.cartPromotionManager.checkPromotionInvalid();

        //获取购物车中所有商品参与的促销活动
        List<PromotionScriptVO> promotions = this.scriptProcess.readSkuScript(cartSkuVOS);
        if(promotions == null || promotions.isEmpty()){
            promotions = new ArrayList<>();
        }
        //获取选中的促销活动
        SelectedPromotionVo selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        //用户选择的组合活动
        Map<Long, CartPromotionVo> groupPromotionMap = selectedPromotionVo.getGroupPromotionMap();
        //用户选择的单品活动
        Map<Long, List<CartPromotionVo>> singlePromotionMap = selectedPromotionVo.getSinglePromotionMap();

        //循环购物车，渲染购物车中促销信息
        for (CartVO cartVO:cartList) {
            //获取当前购物车用户选择的组合活动
            CartPromotionVo groupPromotion = groupPromotionMap.get(cartVO.getSellerId());
            List<PromotionScriptVO> promotionsList = new ArrayList<>(promotions);
            //获取当前购物车用户选择的单品活动
            List<CartPromotionVo> singlePromotions = singlePromotionMap.get(cartVO.getSellerId());
            if(singlePromotions == null){
                singlePromotions = new ArrayList<>();
            }

            //全部商品参与的活动  读取"全部商品"参与的促销活动
            List<PromotionScriptVO> promotionScripts = this.scriptProcess.readCartScript(cartVO.getSellerId());
            //如果不为空，则将全部商品参与的促销活动加入促销活动列表中
            if(promotionScripts != null && !promotionScripts.isEmpty()){
                promotionsList.addAll(promotionScripts);
            }
            //渲染sku促销信息
            renderSkuPromotion(promotionsList, cartVO, groupPromotion, singlePromotions);
            //设置购物车促销提示
            cartVO.setPromotionNotice(this.createNotice(cartVO.getPromotionList()));
        }
    }

    /**
     * 渲染sku促销活动
     * @param promotions    促销活动列表
     * @param cartVO        购物车
     * @param groupPromotion  用户选择的组合活动
     * @param singlePromotions  用户选择的单品活动
     */
    private void renderSkuPromotion(List<PromotionScriptVO> promotions, CartVO cartVO, CartPromotionVo groupPromotion, List<CartPromotionVo> singlePromotions) {
        List<CartPromotionVo> groupPromotions = new ArrayList<>();
        if(groupPromotion != null ){
            Boolean bool = this.scriptProcess.validTime(groupPromotion.getPromotionScript());
            //检测促销活动是否有效
            if(bool != null &&  bool){
                groupPromotions.add(groupPromotion);
            }
        }
        //渲染购物车商品
        for (CartSkuVO cartSkuVO:cartVO.getSkuList()) {
            //商品参与的单品活动
            List<CartPromotionVo> singleList = new ArrayList<>();
            //商品参与的满减活动
            List<CartPromotionVo> groupList = new ArrayList<>();
            //用户选择的促销提示
            List<String> promotionTags = new ArrayList<>();
            //循环活动列表，渲染商品促销活动信息
            for (PromotionScriptVO script:promotions) {
                if(script == null){
                    continue;
                }
                //如果是拼团活动，且购物车类型不为拼图，则不渲染
                if(CartType.PINTUAN.name().equals(script.getPromotionType()) && !CartType.PINTUAN.equals(cartVO.getCartType())){
                    continue;
                }

                Boolean bool = this.scriptProcess.validTime(script.getPromotionScript());
                //检测促销活动是否有效
                if(bool == null || !bool){
                    continue;
                }
                //此促销活动是全部商品参与 || 当前商品参与了此促销活动
                if(script.getSkuId() == null || script.getSkuId().equals(cartSkuVO.getSkuId())){
                    CartPromotionVo promotionVo = new CartPromotionVo();
                    BeanUtil.copyProperties(script,promotionVo);
                    promotionVo.setSkuId(cartSkuVO.getSkuId());

                    //组合活动只有满减，且同一时间内只能存在一个活动,如果商品参与了满减活动，默认使用满减活动  且没有选中积分活动
                    if(PromotionTypeEnum.FULL_DISCOUNT.name().equals(script.getPromotionType()) && !this.includePointGoods(cartSkuVO.getSkuId(),singlePromotions)){
                        promotionVo.setIsCheck(1);
                        addPromotionTag(promotionTags, promotionVo.getPromotionType());
                        cartPromotionManager.usePromotion(cartVO.getSellerId(),cartSkuVO.getSkuId(),promotionVo);
                        groupList.add(promotionVo);
                    }else{
                        //选中了促销信息 && 此促销活动在选中的促销活动中
                        if(singlePromotions != null && singlePromotions.contains(promotionVo)){
                            promotionVo.setIsCheck(1);
                        }else{
                            promotionVo.setIsCheck(0);
                        }
                        singleList.add(promotionVo);
                    }
                }
            }
            cartVO.setPromotionList(groupPromotions);

            //判断是否存在已选中的促销活动
            if (singlePromotions != null){
                for (CartPromotionVo promotionVo:singlePromotions) {
                    if(cartSkuVO.getSkuId().equals(promotionVo.getSkuId()) && promotionVo.getIsCheck() == 1){
                        addPromotionTag(promotionTags, promotionVo.getPromotionType());
                    }
                }
            }
            //设置购物车SKU的促销相关信息
            cartSkuVO.setPromotionTags(promotionTags);
            cartSkuVO.setSingleList(singleList);
            cartSkuVO.setGroupList(groupList);
        }
    }


    /**
     * 检测选中的单品促销活动是否为积分兑换
     * @param skuId  skuid
     * @param singlePromotions 选中的单品活动集合
     * @return
     */
    private Boolean includePointGoods(Long skuId, List<CartPromotionVo> singlePromotions){

        for (CartPromotionVo promotionVo:singlePromotions) {
            if(promotionVo.getSkuId().equals(skuId) && PromotionTypeEnum.EXCHANGE.name().equals(promotionVo.getPromotionType())){
                return true;
            }
        }
        return false;
    }
    /**
     * 购物车中塞入促销提示
     * @param promotionTags 提示列表
     * @param promotionType 促销类型
     */
    private void addPromotionTag(List<String> promotionTags, String promotionType) {
        String tags = PromotionTypeEnum.myValueOf(promotionType).getPromotionName();
        if(!promotionTags.contains(tags) && !PromotionTypeEnum.EXCHANGE.name().equals(promotionTags)){
            promotionTags.add(tags);
        }
    }

    /**
     * 根据满减活动，生成促销提示
     *
     * @param cartPromotionList 满减活动列表
     * @return
     */
    private String createNotice(List<CartPromotionVo> cartPromotionList) {
        //促销文字提示
        StringBuffer promotionNotice = new StringBuffer();
        for (CartPromotionVo cartPromotionVo:cartPromotionList) {
            if(cartPromotionVo == null){
                continue;
            }
            promotionNotice.append(cartPromotionVo.getPromotionName());
        }
        return promotionNotice.toString();
    }


}

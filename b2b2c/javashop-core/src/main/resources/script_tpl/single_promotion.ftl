<#--
 验证促销活动是否在有效期内
 @param promotionActive 活动信息对象(内置常量)
        .startTime 获取开始时间
        .endTime 活动结束时间
 @param $currentTime 当前时间(变量)
 @returns {boolean}
 -->
function validTime(){
    if (${promotionActive.startTime} <= $currentTime && $currentTime <= ${promotionActive.endTime}) {
        return true;
    }
    return false;
}

<#--
活动金额计算
@param promotionActive 活动信息对象(内置常量)
       .price 商品促销活动价格
@param $sku 商品SKU信息对象(变量)
       .$num 商品数量
@returns {*}
-->
function countPrice() {
    var resultPrice = $sku.$num * ${promotionActive.price};
    return resultPrice < 0 ? 0 : resultPrice.toString();
}


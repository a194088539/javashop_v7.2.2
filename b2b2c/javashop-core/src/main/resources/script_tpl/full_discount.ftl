<#--
 验证促销活动是否在有效期内
 @param promotionActive 活动日期对象(内置常量)
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
 减现金或打折活动价格计算
 @param promotionActive 促销活动信息对象(内置常量)
        .fullMoney 优惠门槛金额
        .isFullMinus 是否立减现金 0：否，1：是
        .minusValue 立减金额
        .isDiscount 是否打折 0：否，1：是
        .discountValue 打多少折，例：打8折，值为0.8
 @param $price 参与活动的商品总金额(变量)
 @returns {*}
 -->
function countPrice() {
    <#--判断商品金额是否满足优惠条件 -->
    if (${promotionActive.fullMoney} <= $price) {
        var resultPrice = $price;
        if (${promotionActive.isFullMinus} == 1) {
            resultPrice = $price - ${promotionActive.minusValue};
        }
        if (${promotionActive.isDiscount} == 1) {
            resultPrice = $price * ${promotionActive.discountValue};
        }
        return resultPrice < 0 ? 0 : resultPrice.toString();
    }
    return $price;
}

<#--
 赠送赠品
 @param promotionActive 促销活动信息对象(内置常量)
        .fullMoney 优惠门槛金额
        .gift 赠品信息对象json
 @param $price 参与活动的商品总金额(变量)
 @returns {*}
 -->
function giveGift() {
    <#-- 判断商品金额是否满足优惠条件 -->
    if (${promotionActive.fullMoney} <= $price) {
        return JSON.stringify(${promotionActive.gift});
    }
    return null;
}
<#--
验证优惠券是否在有效期内
@param coupon 优惠券信息对象(内置常量)
       .startTime 有效期开始时间
       .endTime 有效期结束时间
@param $currentTime 当前时间(变量)
@return {boolean}
-->
function validTime(){
    if (${coupon.startTime} <= $currentTime && $currentTime <= ${coupon.endTime}) {
        return true;
    }
    return false;
}

<#--
优惠券金额计算
@param coupon 优惠券信息对象(内置常量)
       .couponPrice 优惠券面额
@param $price 商品总价(变量，如果有商品参与了其它促销活动，为商品优惠后的总价)
@returns {*}
-->
function countPrice() {
    var resultPrice = $price - ${coupon.couponPrice};
    return resultPrice < 0 ? 0 : resultPrice.toString();
}


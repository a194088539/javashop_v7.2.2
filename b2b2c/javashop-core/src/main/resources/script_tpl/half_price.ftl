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
 第二件半价促销活动金额计算
 @param $sku 商品SKU信息对象(变量)
        .$price 商品SKU单价
        .$num 商品数量
 @returns {*}
 -->
function countPrice() {
    <#--获取实际商品总金额-->
    var totalPrice = $sku.$price * $sku.$num;
    <#--应该优惠的金额-->
    var discountPrice = 0.00;

    <#--当商品数量大于1时才计算优惠-->
    if ($sku.$num > 1) {
        <#--如果商品数量对2进行取余得到的结果为0-->
        if ($sku.$num % 2 == 0) {
            <#--优惠金额 = 商品原价的一半 * 商品数量的一半-->
            discountPrice = ($sku.$price / 2) * ($sku.$num / 2);
        }
        <#--如果商品数量对2进行取余得到的结果为1-->
        if ($sku.$num % 2 == 1) {
            <#--优惠金额 = 商品原价的一半 * (商品数量 - 1)的一半-->
            discountPrice = ($sku.$price / 2) * (($sku.$num - 1) / 2);
        }
    }
    <#--最终返回优惠后的总金额 = 实际商品总金额 - 优惠金额-->
    totalPrice = totalPrice - discountPrice;
    return totalPrice < 0 ? 0 : totalPrice.toString();
}


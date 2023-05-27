<#--
此方法会直接返回true，积分兑换不涉及有效期，脚本中有此方法是为了脚本内容统一
@returns {boolean}
-->
function validTime(){
    return true;
}

<#--
计算兑换积分商品所需要的金额
@param goods 积分商品对象(内置常量)
       .price 兑换积分商品所需要的金额
@param $sku 商品SKU信息对象(变量)
       .$num 商品数量
@returns {*}
-->
function countPrice() {
    var resultPrice = $sku.$num * ${goods.price};
    return resultPrice < 0 ? 0 : resultPrice.toString();
}

<#--
计算兑换积分商品所需要的金额
@param goods 积分商品对象(内置常量)
       .point 兑换积分商品所需要的积分
@param $sku 商品SKU信息对象(变量)
       .$num 商品数量
@returns {*}
-->
function countPoint() {
    return ($sku.$num * ${goods.point}).toString();
}
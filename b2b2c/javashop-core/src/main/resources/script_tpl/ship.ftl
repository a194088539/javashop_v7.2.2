function getShipPrice(){
    if('${freeAreaIds}'.indexOf(','+$address+',') > 0){
            return 0;
    }

    if(${type} == 1 ){
        return countWeight();
    }

    if(${type} == 2 ){
        return countNum();
    }
}

function countWeight(){
    if('${area}'.indexOf(','+$address+',') < 0){
        return 0;
    }
    var shipPrice= ${firstPrice};

    if(${firstCompany} < $goodsWeight){
        var count = ($goodsWeight - ${firstCompany})/${continuedCompany};
        count = Math.ceil(count)
        shipPrice = shipPrice + count * ${continuedPrice};
    }

    return shipPrice;
}

function countNum(){
    if('${area}'.indexOf(','+$address+',') < 0){
        return 0;
    }

    var shipPrice= ${firstPrice};

    if(${firstCompany} < $goodsNum){
        var count = ($goodsNum - ${firstCompany})/${continuedCompany};
        count = Math.ceil(count)
        shipPrice = shipPrice + count * ${continuedPrice};
    }

    return shipPrice;
}
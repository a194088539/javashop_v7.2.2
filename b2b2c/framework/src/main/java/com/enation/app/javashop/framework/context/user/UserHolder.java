package com.enation.app.javashop.framework.context.user;

import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;

/**
 * 用户信息hold接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-05-28
 */
public interface UserHolder {

    /**
     * 获取seller
     * @return
     */
    Seller getSeller();

    /**
     * 获取buyer
     * @return
     */
    Buyer getBuyer();


}

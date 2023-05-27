package com.enation.app.javashop.framework.context.user;

import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;

/**
 * 用户上下文
 * Created by kingapex on 2018/3/12.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/12
 */
public class UserContext {

    private static UserHolder userHolder;

    public static void  setHolder(UserHolder _userHodler) {
        userHolder= _userHodler;
    }

    /**
     * 获取当前卖家
     *
     * @return
     */
    public static Seller getSeller() {
        return userHolder.getSeller();

    }

    /**
     * 为了方便在单元测试中测试已登录的情况，请使用此属性
     * 如果此属性有值，买家上下文中将会直接返回此模拟对象
     */
    public static Buyer mockBuyer =null;

    /**
     * 获取当前买家
     *
     * @return
     */
    public static Buyer getBuyer() {

        //如果有模拟对象，会直接返回此模拟对象
        if (mockBuyer != null) {
            return mockBuyer;
        }

        return userHolder.getBuyer();

    }


}

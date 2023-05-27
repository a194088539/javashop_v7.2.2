package com.enation.app.javashop.model.shop.vo;

import java.io.Serializable;
import java.util.Objects;

/**
 * 店铺名称改变消息
 *
 * @author zh
 * @version v7.0
 * @date 18/12/6 下午4:21
 * @since v7.0
 */

public class ShopChangeMsg implements Serializable {


    private static final long serialVersionUID = -109352767337144162L;

    public ShopChangeMsg() {

    }

    public ShopChangeMsg(ShopVO originalShop, ShopVO newShop, String messageType) {
        this.originalShop = originalShop;
        this.newShop = newShop;
        this.messageType = messageType;
    }

    /**
     * 原店铺信息
     */
    private ShopVO originalShop;

    /**
     * 现在店铺信息
     */
    private ShopVO newShop;

    /**
     *
     * @return
     */
    private String messageType;


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ShopVO getOriginalShop() {
        return originalShop;
    }

    public void setOriginalShop(ShopVO originalShop) {
        this.originalShop = originalShop;
    }

    public ShopVO getNewShop() {
        return newShop;
    }

    public void setNewShop(ShopVO newShop) {
        this.newShop = newShop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ShopChangeMsg that = (ShopChangeMsg) o;
        return Objects.equals(originalShop, that.originalShop) &&
                Objects.equals(newShop, that.newShop) &&
                Objects.equals(messageType, that.messageType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalShop, newShop, messageType);
    }

    @Override
    public String toString() {
        return "ShopStatusChangeMsg{" +
                "originalShop=" + originalShop +
                ", newShop=" + newShop +
                '}';
    }

}

package com.enation.app.javashop.model.member.vo;

import com.enation.app.javashop.model.member.dos.AskReplyDO;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员问题咨询回复对象vo
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-28
 */
public class AskReplyVO extends AskReplyDO {
    /**
     * 咨询内容
     */
    @ApiModelProperty(name = "ask_content", value = "咨询内容")
    private String askContent;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id")
    private Long goodsId;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称")
    private String goodsName;
    /**
     * 商品图片
     */
    @ApiModelProperty(name = "goods_img", value = "商品图片")
    private String goodsImg;

    public String getAskContent() {
        return askContent;
    }

    public void setAskContent(String askContent) {
        this.askContent = askContent;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    @Override
    public String toString() {
        return "AskReplyVO{" +
                "askContent='" + askContent + '\'' +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                '}';
    }
}

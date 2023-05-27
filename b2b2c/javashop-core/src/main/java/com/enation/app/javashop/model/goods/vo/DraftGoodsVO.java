package com.enation.app.javashop.model.goods.vo;

import com.enation.app.javashop.model.goods.dos.DraftGoodsDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.List;

/**
 * 草稿箱商品vo
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/7/6 上午3:00
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DraftGoodsVO extends DraftGoodsDO {

    /**
     * 商品分类名称
     */
    @ApiModelProperty(name = "category_name", value = "商品分类名称", required = false)
    private String categoryName;

    @ApiModelProperty(name = "category_ids", value = "分类id数组")
    private Long[] categoryIds;

    @ApiModelProperty(name = "intro_list", value = "商品移动端详情数据集合")
    private List<GoodsMobileIntroVO> introList;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<GoodsMobileIntroVO> getIntroList() {
        return introList;
    }

    public void setIntroList(List<GoodsMobileIntroVO> introList) {
        this.introList = introList;
    }

    public Long[] getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Long[] categoryIds) {
        this.categoryIds = categoryIds;
    }

    @Override
    public String toString() {
        return "DraftGoodsVO{" +
                "categoryName='" + categoryName + '\'' +
                ", categoryIds=" + Arrays.toString(categoryIds) +
                ", introList=" + introList +
                '}';
    }
}
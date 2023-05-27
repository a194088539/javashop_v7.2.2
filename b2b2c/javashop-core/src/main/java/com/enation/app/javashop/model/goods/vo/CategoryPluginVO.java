package com.enation.app.javashop.model.goods.vo;

import java.io.Serializable;

import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分类获取的插件返回的vo
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月16日 下午4:53:39
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoryPluginVO extends CategoryDO implements Serializable {

	/**
	 * 分类获取的插件返回的vo
	 */
	private static final long serialVersionUID = -8428052730649034814L;

	@ApiModelProperty("分类id")
	private Long id;
	@ApiModelProperty("分类名称")
	private String text;

	public CategoryPluginVO() {
	}

	public CategoryPluginVO(CategoryDO cat) {
		this.setCategoryId(cat.getCategoryId());
		this.setCategoryPath(cat.getCategoryPath());
		this.setName(cat.getName());
		this.setParentId(cat.getParentId());
		this.setImage(cat.getImage());
		this.setCategoryOrder(cat.getCategoryOrder());
		this.setAdvImage(cat.getAdvImage());
		this.setAdvImageLink(cat.getAdvImageLink());
	}

	public Long getId() {
		id = this.getCategoryId();
		return id;
	}

	public String getText() {
		text = this.getName();
		return text;
	}

	@Override
	public String toString() {
		return "CategoryPluginVO [id=" + id + ", text=" + text + "]";
	}

}

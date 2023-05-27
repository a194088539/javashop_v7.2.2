package com.enation.app.javashop.api.seller.promotion;

import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyCatDO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyCatManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 团购分类控制器
 *
 * @author Snow create in 2018/7/12
 * @version v2.0
 * @since v7.0.0
 */
@RestController
@RequestMapping("/seller/promotion/group-buy-cats")
@Api(description = "团购分类相关API")
@Validated
public class GroupbuyCatSellerController {

    @Autowired
    private GroupbuyCatManager groupbuyCatManager;

    @ApiOperation(value	= "查询团购分类列表", response = GroupbuyGoodsDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name	= "parent_id",	value =	"父分类id", dataType = "int",	paramType =	"query"),
    })
    @GetMapping
    public List<GroupbuyCatDO> list(@ApiIgnore Long parentId)	{
        if(parentId == null){
            parentId = 0L;
        }
        List<GroupbuyCatDO> list = this.groupbuyCatManager.getList(parentId);
        return list;
    }

}

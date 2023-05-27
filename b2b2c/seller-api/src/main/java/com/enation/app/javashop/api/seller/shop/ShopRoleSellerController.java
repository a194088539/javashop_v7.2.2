package com.enation.app.javashop.api.seller.shop;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.shop.dos.ShopRole;
import com.enation.app.javashop.model.shop.vo.ShopRoleVO;
import com.enation.app.javashop.service.shop.ShopRoleManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * 角色控制器
 *
 * @author admin
 * @version v1.0.0
 * @since v7.0.0
 * 2018-04-17 16:48:27
 */
@RestController
@RequestMapping("/seller/shops/roles")
@Api(description = "角色相关API")
public class ShopRoleSellerController {

    @Autowired
    private ShopRoleManager shopRoleManager;


    @ApiOperation(value = "查询角色列表", response = ShopRole.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore @NotEmpty(message = "页码不能为空") Long pageNo, @ApiIgnore @NotEmpty(message = "每页数量不能为空") Long pageSize) {
        return this.shopRoleManager.list(pageNo, pageSize);
    }


    @ApiOperation(value = "添加角色", response = ShopRoleVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopRoleVO", value = "角色", required = true, dataType = "ShopRoleVO", paramType = "body")
    })
    @PostMapping
    public ShopRoleVO add(@RequestBody @ApiIgnore ShopRoleVO shopRoleVO) {
        return this.shopRoleManager.add(shopRoleVO);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改角色表", response = ShopRoleVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "shopRoleVO", value = "角色", required = true, dataType = "ShopRoleVO", paramType = "body")
    })
    public ShopRoleVO edit(@RequestBody @ApiIgnore ShopRoleVO shopRoleVO, @PathVariable Long id) {
        return this.shopRoleManager.edit(shopRoleVO, id);
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的角色表主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {
        this.shopRoleManager.delete(id);
        return "";
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个角色表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的角色表主键", required = true, dataType = "int", paramType = "path")
    })
    public ShopRoleVO get(@PathVariable Long id) {

        return shopRoleManager.getRole(id);
    }

    @GetMapping(value = "/{id}/checked")
    @ApiOperation(value = "根据角色id查询所拥有的菜单权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的角色表主键", required = true, dataType = "int", paramType = "path")
    })
    public List<String> getCheckedMenu(@PathVariable Long id) {
        return this.shopRoleManager.getRoleMenu(id);
    }

}

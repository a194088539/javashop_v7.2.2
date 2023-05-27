package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.model.member.dos.MemberCollectionGoods;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

import com.enation.app.javashop.service.member.MemberCollectionGoodsManager;

/**
 * 会员商品收藏表控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 10:13:41
 */
@RestController
@RequestMapping("/members")
@Api(description = "会员商品收藏表相关API")
@Validated
public class MemberCollectionGoodsBuyerController {

    @Autowired
    private MemberCollectionGoodsManager memberCollectionGoodsManager;


    @ApiOperation(value = "查询会员商品收藏列表", response = MemberCollectionGoods.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/collection/goods")
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {

        return this.memberCollectionGoodsManager.list(pageNo, pageSize);
    }


    @ApiOperation(value = "添加会员商品收藏", response = MemberCollectionGoods.class)
    @PostMapping("/collection/goods")
    @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "int", paramType = "query")
    public MemberCollectionGoods add(@NotNull(message = "商品id不能为空") @ApiIgnore Long goodsId) {
        MemberCollectionGoods memberCollectionGoods = new MemberCollectionGoods();
        memberCollectionGoods.setGoodsId(goodsId);
        return this.memberCollectionGoodsManager.add(memberCollectionGoods);
    }

    @DeleteMapping(value = "/collection/goods/{goods_id}")
    @ApiOperation(value = "删除会员商品收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品id", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable("goods_id") Long goodsId) {
        this.memberCollectionGoodsManager.delete(goodsId);
        return "";
    }

    @GetMapping(value = "/collection/goods/{id}")
    @ApiOperation(value = "查询会员是否收藏商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true, dataType = "int", paramType = "path")
    })
    public SuccessMessage isCollection(@PathVariable Long id) {
        return new SuccessMessage(this.memberCollectionGoodsManager.isCollection(id));
    }


}

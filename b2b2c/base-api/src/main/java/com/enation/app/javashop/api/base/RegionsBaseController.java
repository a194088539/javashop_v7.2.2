package com.enation.app.javashop.api.base;

import com.enation.app.javashop.model.member.vo.RegionVO;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.service.system.RegionsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地区api
 *
 * @author zh
 * @version v7.0
 * @date 18/5/28 下午7:49
 * @since v7.0
 */
@RestController
@RequestMapping("/regions")
@Api(description = "地区API")
public class RegionsBaseController {

    @Autowired
    private RegionsManager regionsManager;


    @GetMapping(value = "/{id}/children")
    @ApiOperation(value = "获取某地区的子地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "地区id", required = true, dataType = "int", paramType = "path")
    })
    public List<Regions> getChildrenById(@PathVariable Long id) {

        return regionsManager.getRegionsChildren(id);
    }


    @GetMapping(value = "/depth/{depth}")
    @ApiOperation(value = "根据地区深度查询组织好地区数据结构的地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "depth", value = "深度", required = true, dataType = "int", paramType = "path")
    })
    public List<RegionVO> getRegionByDepth(@PathVariable Integer depth) {
        return regionsManager.getRegionByDepth(depth);
    }


}

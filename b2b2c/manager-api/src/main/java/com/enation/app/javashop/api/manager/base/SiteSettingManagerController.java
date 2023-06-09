package com.enation.app.javashop.api.manager.base;

import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.service.base.service.SettingManager;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.framework.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 站点设置api
 *
 * @author zh
 * @version v7.0
 * @date 18/5/18 下午6:55
 * @since v7.0
 */
@RestController
@RequestMapping("/admin/settings")
@Api(description = "站点设置")
@Validated
public class SiteSettingManagerController {
    @Autowired
    private SettingManager settingManager;


    @GetMapping(value = "/site")
    @ApiOperation(value = "获取站点设置", response = SiteSetting.class)
    public SiteSetting getSiteSetting() {
        String siteSettingJson = settingManager.get(SettingGroup.SITE);

        SiteSetting siteSetting = JsonUtil.jsonToObject(siteSettingJson,SiteSetting.class);
        if (siteSetting == null) {
            return new SiteSetting();
        }
        return siteSetting;
    }

    @PutMapping(value = "/site")
    @ApiOperation(value = "修改站点设置", response = SiteSetting.class)
    public SiteSetting editSiteSetting(@Valid SiteSetting siteSetting) {
        settingManager.save(SettingGroup.SITE, siteSetting);
        return siteSetting;
    }

}

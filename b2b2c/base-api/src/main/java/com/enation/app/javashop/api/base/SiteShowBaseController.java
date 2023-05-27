package com.enation.app.javashop.api.base;

import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.service.base.service.SettingManager;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.framework.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站点信息首页展示控制器
 *
 * @author zh
 * @version v7.0
 * @date 18/7/13 上午11:21
 * @since v7.0
 */
@RestController
@RequestMapping("/site-show")
@Api(description = "站点展示")
public class SiteShowBaseController {

    @Autowired
    private SettingManager settingManager;

    @GetMapping
    @ApiOperation(value = "获取站点设置")
    public SiteSetting getSiteSetting() {

        String siteJson = settingManager.get(SettingGroup.SITE);

        return JsonUtil.jsonToObject(siteJson,SiteSetting.class);
    }
}

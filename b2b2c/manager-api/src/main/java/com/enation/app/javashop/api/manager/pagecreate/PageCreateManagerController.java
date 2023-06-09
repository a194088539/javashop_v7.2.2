package com.enation.app.javashop.api.manager.pagecreate;

import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.vo.SuccessMessage;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.errorcode.PageCreateErrorCode;
import com.enation.app.javashop.service.pagecreate.exception.SystemException;
import com.enation.app.javashop.model.pagecreate.PageCreateEnum;
import com.enation.app.javashop.service.pagecreate.PageCreateManager;
import com.enation.app.javashop.model.system.vo.PageSetting;
import com.enation.app.javashop.framework.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;


/**
 * 静态也生成控制器
 *
 * @author zh
 * @version v1.0
 * @since v1.0
 * 2017年9月4日 上午11:37:16
 */
@RestController
@Api(description = "后台-》静态页生成-》")
@RequestMapping("/admin/page-create")
public class PageCreateManagerController {

    @Autowired
    private PageCreateManager pageCreateManager;

    @Autowired
    private SettingClient settingClient;


    /**
     * 获取当前静态页面设置参数
     */
    @ApiOperation("参数获取")
    @GetMapping(value = "/input")
    public PageSetting input() {
        String pageSettingJson = settingClient.get( SettingGroup.PAGE);
        PageSetting settings = JsonUtil.jsonToObject(pageSettingJson,PageSetting.class);
        return settings;
    }


    @ApiOperation("页面生成")
    @ApiImplicitParam(name = "choose_pages", value = "静态页：INDEX/GOODS/HELP 代表首页/商品页/帮助页", dataType = "String", paramType = "query", allowMultiple = true)
    @PostMapping(value = "/create")
    public SuccessMessage create( @ApiIgnore @RequestParam("choose_pages") String[] choosePages) {

        this.checkPage(choosePages);

        try {
            boolean result = pageCreateManager.startCreate(choosePages);
            if (result) {
                return new SuccessMessage("任务开始");
            } else {
                throw new SystemException(PageCreateErrorCode.E905.code(), PageCreateErrorCode.E905.des());
            }
        } catch (SystemException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new SystemException(PageCreateErrorCode.E901.code(), PageCreateErrorCode.E901.des());
        }

    }

    @ApiOperation("参数保存")
    @PostMapping(value = "/save")
    public PageSetting save(@Valid PageSetting pageSetting) {
        String pageSettingJson = settingClient.get( SettingGroup.PAGE);
        PageSetting settings = JsonUtil.jsonToObject(pageSettingJson,PageSetting.class);
        if (settings == null) {
            settings = new PageSetting();
        }
        settings.setStaticPageAddress(pageSetting.getStaticPageAddress());
        settings.setStaticPageWapAddress(pageSetting.getStaticPageWapAddress());
        try {
            settingClient.save(SettingGroup.PAGE, settings);
        } catch (Exception e) {
            throw new SystemException(PageCreateErrorCode.E901.code(), PageCreateErrorCode.E901.des());

        }
        return settings;
    }


    /**
     * 校验参数有效
     * @param page
     */
    private void checkPage(String[] page) {
        if (page == null) {
            throw new SystemException(PageCreateErrorCode.E904.code(), PageCreateErrorCode.E904.des());
        }
        for (String p : page) {
            if (!p.equals(PageCreateEnum.GOODS.name()) && !p.equals(PageCreateEnum.HELP.name()) && !p.equals(PageCreateEnum.INDEX.name())&&!p.equals(PageCreateEnum.SHOP.name())) {
                throw new SystemException(PageCreateErrorCode.E904.code(), PageCreateErrorCode.E904.des());
            }
        }


    }

}

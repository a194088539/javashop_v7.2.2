package com.enation.app.javashop.api.base;

import com.enation.app.javashop.model.system.dto.ValidatorPlatformDTO;
import com.enation.app.javashop.service.system.ValidatorPlatformManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证方式API
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@RestController
@RequestMapping("/validator")
@Api(description = "验证方式API")
public class ValidatorBaseController {

    @Autowired
    private ValidatorPlatformManager validatorPlatformManager;

    @ApiOperation(value = "获取当前系统开启的验证平台信息")
    @GetMapping()
    public ValidatorPlatformDTO get() {

        return this.validatorPlatformManager.getCurrentOpen();
    }

}

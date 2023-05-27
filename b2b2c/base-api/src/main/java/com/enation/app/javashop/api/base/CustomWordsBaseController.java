package com.enation.app.javashop.api.base;

import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goodssearch.EsSecretSetting;
import com.enation.app.javashop.service.goodssearch.CustomWordsManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 自定义分词控制器
 *
 * @author liuyulei
 * @version v1.0
 * @since v7.0.0
 * 2019-05-26
 */
@RestController
@RequestMapping("/load-customwords")
@Api(description = "加载分词库")
public class CustomWordsBaseController {

    @Autowired
    private CustomWordsManager customWordsManager;
    @Autowired
    private SettingClient settingClient;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "secret_key", value = "秘钥", required = true, dataType = "String", paramType = "query")

    })
    public String getCustomWords(@ApiIgnore  String secretKey){

        if(StringUtil.isEmpty(secretKey)){
            return "";
        }
        String value = settingClient.get(SettingGroup.ES_SIGN);
        if(StringUtil.isEmpty(value)){
            return "";
        }
        EsSecretSetting secretSetting = JsonUtil.jsonToObject(value,EsSecretSetting.class);

        if(!secretKey.equals(secretSetting.getSecretKey())){
            throw new ServiceException(GoodsErrorCode.E310.code(),"秘钥验证失败！");
        }
        String res = this.customWordsManager.deploy();
        try {
            return new String(res.getBytes(),"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";

    }


}

package com.enation.app.javashop.service.system.factory;

import com.enation.app.javashop.service.base.plugin.express.ExpressPlatform;
import com.enation.app.javashop.model.system.vo.ExpressPlatformVO;
import com.enation.app.javashop.service.system.ExpressPlatformManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 快递平台插件工厂类
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-30
 */
@Component
public class ExpressFactory {

    @Autowired
    private List<ExpressPlatform> expressPlatforms;

    @Autowired
    private ExpressPlatformManager expressPlatformManager;

    public ExpressPlatform getExpressPlatform() {

        ExpressPlatformVO expressPlatformVO = this.expressPlatformManager.getOpen();
        return this.findByPluginid(expressPlatformVO.getBean());
    }

    /**
     * 根据插件id获取快递平台插件
     *
     * @param pluginId
     * @return
     */
    private ExpressPlatform findByPluginid(String pluginId) {
        for (ExpressPlatform expressPlatform : expressPlatforms) {
            if (expressPlatform.getPluginId().equals(pluginId)) {
                return expressPlatform;
            }
        }
        //如果走到这里，说明找不到可用的快递平台插件
        throw new ResourceNotFoundException("未找到可用的快递平台插件");
    }
}

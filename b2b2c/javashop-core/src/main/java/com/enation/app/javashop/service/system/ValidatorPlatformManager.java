package com.enation.app.javashop.service.system;

import com.enation.app.javashop.model.system.dos.ValidatorPlatformDO;
import com.enation.app.javashop.model.system.dto.ValidatorPlatformDTO;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 验证平台相关接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
public interface ValidatorPlatformManager {

    /**
     * 获取验证平台列表数据
     * @param pageNo 页数
     * @param pageSize 每页记录数
     * @return
     */
    WebPage list(Long pageNo, Long pageSize);

    /**
     * 添加验证平台信息
     *
     * @param validatorPlatformVO 验证平台信息
     * @return validatorPlatformVO 滑块验证信息
     */
    ValidatorPlatformVO add(ValidatorPlatformVO validatorPlatformVO);

    /**
     * 根据插件ID获取验证平台信息
     *
     * @param pluginId 插件ID
     * @return
     */
    ValidatorPlatformDO getPlatform(String pluginId);

    /**
     * 修改验证平台信息
     *
     * @param validatorPlatformVO 验证平台信息
     * @return validatorPlatformVO 验证平台信息
     */
    ValidatorPlatformVO edit(ValidatorPlatformVO validatorPlatformVO);

    /**
     * 根据验证平台的插件ID获取验证平台的配置项
     *
     * @param pluginId 验证平台插件ID
     * @return 验证平台信息
     */
    ValidatorPlatformVO getConfig(String pluginId);

    /**
     * 开启某个验证平台
     *
     * @param pluginId 验证平台插件ID
     */
    void open(String pluginId);

    /**
     * 获取开启的验证平台信息
     * @return
     */
    ValidatorPlatformVO getOpen();

    /**
     * 获取当前系统开启的验证平台信息
     * @return
     */
    ValidatorPlatformDTO getCurrentOpen();

}

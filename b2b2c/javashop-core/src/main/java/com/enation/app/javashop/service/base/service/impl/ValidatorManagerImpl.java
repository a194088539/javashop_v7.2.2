package com.enation.app.javashop.service.base.service.impl;

import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.service.base.plugin.validator.ValidatorPlugin;
import com.enation.app.javashop.service.base.service.ValidatorManager;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.service.system.factory.ValidatorFactory;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证相关接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@Service
public class ValidatorManagerImpl implements ValidatorManager {

    @Autowired
    private ValidatorFactory validatorFactory;

    @Autowired
    private Cache cache;

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void validate() {
        HttpServletRequest req = ThreadContextHolder.getHttpRequest();
        String client = req.getParameter("mini_client");
        if (ClientType.MINI.getClient().equals(client)) {
            ValidatorPlugin plugin = validatorFactory.getCaptchaValidatorPlugin();
            plugin.onValidate(new HashMap<String, Object>());
        } else {
            ValidatorPlugin platform = validatorFactory.getValidatorPlugin();
            platform.onValidate(this.getConfig());
        }
    }

    /**
     * 将json参数转换为map格式
     *
     * @return 验证平台参数
     */
    private Map getConfig() {
        ValidatorPlatformVO validatorPlatformVO = (ValidatorPlatformVO) this.cache.get(CachePrefix.VALIDATOR_PLATFORM.getPrefix());
        List<ConfigItem> list = validatorPlatformVO.getConfigItems();

        Map<String, String> result = new HashMap<>(16);

        if (list != null && list.size() != 0) {
            for (ConfigItem item : list) {
                result.put(item.getName(), StringUtil.toString(item.getValue()));
            }
        }
        return result;

    }
}

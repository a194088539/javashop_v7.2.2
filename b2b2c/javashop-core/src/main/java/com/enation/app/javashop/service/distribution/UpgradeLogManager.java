package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.UpgradeLogDO;
import com.enation.app.javashop.model.distribution.enums.UpgradeTypeEnum;


/**
 * 升级日志管理类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 下午12:56
 */

 public interface UpgradeLogManager {

    /**
     * 搜索
     *
     * @param page       分页
     * @param pageSize   分页每页数量
     * @param memberName 会员名
     * @return WebPage
     */
     WebPage<UpgradeLogDO> page(long page, long pageSize, String memberName);

    /**
     * 新增一个模板升级日志
     * @param upgradeLog
     * @return do
     */
     UpgradeLogDO add(UpgradeLogDO upgradeLog);

    /**
     * 新增日志,一定要再修改之前【因为旧的模板id是根据用户id现查的】
     *
     * @param memberId    会员id
     * @param newTplId    新的模板id
     * @param upgradeTypeEnum 模版操作类型
     */
     void addUpgradeLog(Long memberId, int newTplId, UpgradeTypeEnum upgradeTypeEnum);
}

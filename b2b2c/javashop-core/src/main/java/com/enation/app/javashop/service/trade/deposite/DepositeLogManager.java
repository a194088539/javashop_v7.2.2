package com.enation.app.javashop.service.trade.deposite;

import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import com.enation.app.javashop.framework.database.WebPage;

/**
*
* @description: 预存款日志业务类
* @author: liuyulei
* @create: 2019/12/30 17:41
* @version:1.0
* @since:7.1.5
**/
public interface DepositeLogManager {


    /**
     * 添加日志
     * @param logDO  日志实体
     */
    void add(DepositeLogDO logDO);


    /**
     * 获取日志列表  分页
     * @param paramDTO
     * @return
     */
    WebPage list(DepositeParamDTO paramDTO);
}

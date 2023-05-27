package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;

/**
 *
 * @description: 预存款记录客户端
 * @author: liuyulei
 * @create: 2019/12/30 18:40
 * @version:1.0
 * @since:7.1.5
 **/
public interface DepositeLogClient {

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

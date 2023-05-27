package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.framework.database.WebPage;

/**
*
* @description: 预存款充值客户端
* @author: liuyulei
* @create: 2020/1/2 16:41
* @version:1.0
* @since:7.1.5
**/
public interface RechargeClient {


    /**
     * 查询充值记录列表
     * @param paramDTO 搜索参数
     * @return WebPage
     */
    WebPage list(DepositeParamDTO paramDTO);
}

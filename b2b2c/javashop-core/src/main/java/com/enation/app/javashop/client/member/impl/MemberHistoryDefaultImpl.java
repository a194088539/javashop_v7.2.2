package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberHistoryClient;
import com.enation.app.javashop.model.member.dto.HistoryDTO;
import com.enation.app.javashop.service.member.HistoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员历史
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午2:57
 * @since v7.0
 */

@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberHistoryDefaultImpl implements MemberHistoryClient {

    @Autowired
    private HistoryManager historyManager;


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addMemberHistory(HistoryDTO historyDTO) {
        historyManager.addMemberHistory(historyDTO);
    }


}

package com.enation.app.javashop.service.statistics.impl;

import com.enation.app.javashop.mapper.statistics.MemberRegisterMapper;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.statistics.dto.MemberRegisterData;
import com.enation.app.javashop.service.statistics.MemberDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员数据注入实现
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/22 下午10:12
 */
@Service
public class MemberDataManagerImpl implements MemberDataManager {

    @Autowired
    private MemberRegisterMapper memberRegisterMapper;

    @Override
    public void register(Member member) {
        memberRegisterMapper.insert(new MemberRegisterData(member));
    }

}

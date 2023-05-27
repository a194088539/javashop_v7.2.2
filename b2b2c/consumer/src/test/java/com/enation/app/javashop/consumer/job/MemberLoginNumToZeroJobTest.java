package com.enation.app.javashop.consumer.job;

import com.enation.app.javashop.consumer.job.execute.impl.MemberLoginNumToZeroJob;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 会员登录数量归零单元测试
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-19 下午2:40
 */
public class MemberLoginNumToZeroJobTest extends BaseTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberLoginNumToZeroJob memberLoginNumToZeroJob;


    @Autowired
    private MemberManager memberManager;

    @Autowired
    @Qualifier("memberDaoSupport")
    private DaoSupport memberDaoSupport;

    @Before
    public void befor(){
        this.memberDaoSupport.execute("delete from es_member where uname = 'hahahaha'");
    }

    @Test
    public void everyMonthTest() throws Exception{
        Member member = new Member();
        member.setUname("hahahaha");
        member.setPassword("123123");
        member.setMobile("18234121332");
        member.setHaveShop(0);
        member.setEmail("31048769912@qq.com");
        memberManager.register(member);
        memberManager.login("hahahaha", "123123",1).getAccessToken();

        Thread.sleep(3000L);
        Member cMember = this.memberManager.getMemberByName(member.getUname());
        Assert.assertEquals(cMember.getLoginCount(), 1, 0);


        memberLoginNumToZeroJob.everyMonth();
        cMember = this.memberManager.getMemberByName(member.getUname());
        Assert.assertEquals(cMember.getLoginCount(), 0, 0);



    }
}

package com.enation.app.javashop.consumer.shop.distribution;

import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.MemberRegisterMsg;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.distribution.DistributionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;

/**
 * 注册后添加分销商
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/13 下午11:33
 */
@Rollback
public class DistributionRegisterConsumerTest extends BaseTest {


    @Autowired
    private Cache cache;


    @Autowired
    private DistributionManager distributionManager;

    @Autowired
    @Qualifier("distributionDaoSupport")
    private DaoSupport daoSupport;


    @Autowired
    private DistributionRegisterConsumer distributionRegisterConsumer;


    @Before
    public void beforeDistribution() {
       // DistributionBeforeTest.before(daoSupport);
    }




    @Test
    public void memberRegister() throws Exception {
        Member member = new Member();
        member.setUname("test123");
        member.setMemberId(123L);
        MemberRegisterMsg memberRegisterMsg = new MemberRegisterMsg();
        memberRegisterMsg.setMember(member);
        String uuid="uuid_uuid";
        memberRegisterMsg.setUuid(uuid);
        cache.put(CachePrefix.DISTRIBUTION_UP+uuid, "1");

        distributionRegisterConsumer.memberRegister(memberRegisterMsg);
        DistributionDO ddo = distributionManager.getDistributorByMemberId(123l);

        DistributionDO distributionDO = new DistributionDO();
        distributionDO.setMemberName("test123");
        distributionDO.setMemberId(123L);
        distributionDO.setPath("|0|1|123|");
        distributionDO.setMemberIdLv1(1l);
        distributionDO.setCurrentTplId(1l);
        distributionDO.setCurrentTplName("模版1");

        Assert.assertEquals(ddo.toString(), distributionDO.toString());

    }
    @Test
    public void memberRegister1() throws Exception {
        Member member = new Member();
        member.setUname("test123");
        member.setMemberId(123L);
        MemberRegisterMsg memberRegisterMsg = new MemberRegisterMsg();
        memberRegisterMsg.setMember(member);
        String uuid="uuid_uuid";
        memberRegisterMsg.setUuid(uuid);
        cache.put(CachePrefix.DISTRIBUTION_UP+uuid, "3");

        distributionRegisterConsumer.memberRegister(memberRegisterMsg);
        DistributionDO ddo = distributionManager.getDistributorByMemberId(123l);

        DistributionDO distributionDO = new DistributionDO();
        distributionDO.setMemberName("test123");
        distributionDO.setMemberId(123L);
        distributionDO.setPath("|0|1|2|3|123|");
        distributionDO.setMemberIdLv1(3l);
        distributionDO.setMemberIdLv2(2l);
        distributionDO.setCurrentTplId(1l);
        distributionDO.setCurrentTplName("模版1");

        Assert.assertEquals(ddo.toString(), distributionDO.toString());

    }
}

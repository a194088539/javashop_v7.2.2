package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.auth.*;
import com.enation.app.javashop.framework.auth.impl.JwtTokenCreater;
import com.enation.app.javashop.framework.auth.impl.JwtTokenParser;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * token 单元测试
 *
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-06-24
 */

public class TokenTest {

    @Test
    public void testBeanUtil() {
        Long uid =1L;
        Map map = new HashMap();
        map.put("username", "wangfeng");
        map.put("uid",uid);
        MyUser user = BeanUtil.mapToBean(MyUser.class, map);

        MyUser expectedUser = new MyUser();
        expectedUser.setUsername("wangfeng");

        Assert.assertEquals(expectedUser, user);

    }

    @Test
    public void testParse() {
        String secret = "abc";

        Admin admin = new Admin();
        admin.setFounder(1);
        admin.add("ADMIN");
        admin.setUid(1L);
        admin.setUsername("kingapex");

        TokenCreater tokenCreater = new JwtTokenCreater(secret);
        Token token = tokenCreater.create(admin);

        TokenParser tokenParser = new JwtTokenParser(secret);
        AuthUser user = tokenParser.parse(User.class, token.getAccessToken());
        System.out.println(user);

    }

    @Test
    public void testToken() {

        //正确性测试
        String secret = "abc";
        MyUser user = new MyUser();
        user.setUsername("wangfeng");

        TokenCreater tokenCreater = new JwtTokenCreater(secret);
        Token token = tokenCreater.create(user);

        TokenParser tokenParser = new JwtTokenParser(secret);

        MyUser user1 = tokenParser.parse(MyUser.class, token.getAccessToken());

        Assert.assertEquals(user, user1);


        try {
            //失败性测试
            tokenParser = new JwtTokenParser("the wrong key");
            user1 = tokenParser.parse(MyUser.class, token.getAccessToken());
            Assert.fail("key 错误不能走到这里");
        } catch (TokenParseException e) {

        }


    }

    @Test
    public void test1() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjMsInN1YiI6InVzZXIiLCJyb2xlcyI6WyJidXllciJdLCJleHAiOjE1NjE5NzAxOTYsInV1aWQiOm51bGwsInVzZXJuYW1lIjoia2luZ2FwZXgyIn0.eduw_PUlHLy2PI1wOprhka0vqm2pAF2JpD5xLPmtlkOn8Jvgu3tSOevUDaZWwfW0xT9knaussLuOyahkBqpogw";
        TokenParser tokenParser = new JwtTokenParser("cc282eed51464bd0a10bfe542493a560");
        MyUser user1 = tokenParser.parse(MyUser.class, token);

    }


    /**
     * 有效期测试
     */
    @Test
    public void testExp() throws InterruptedException {
        String secret = "abc";
        MyUser user = new MyUser();
        user.setUsername("wangfeng");

        //设定为五秒过期
        TokenCreater tokenCreater = new JwtTokenCreater(secret).setAccessTokenExp(5);
        Token token = tokenCreater.create(user);

        String accessToken  = token.getAccessToken();
        System.out.println(accessToken);
        Thread.sleep(10);
        TokenParser tokenParser = new JwtTokenParser(secret);

        MyUser user1 = tokenParser.parse(MyUser.class, accessToken);
        System.out.println(user1);
    }

}

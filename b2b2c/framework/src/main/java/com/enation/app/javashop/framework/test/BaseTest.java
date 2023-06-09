package com.enation.app.javashop.framework.test;


import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Clerk;
import com.enation.app.javashop.framework.security.model.Role;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by kingapex on 2018/3/28.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/28
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest()
@Transactional(rollbackFor = Exception.class)
//@Rollback
@ContextConfiguration(classes = {TestConfig.class})
public abstract class BaseTest {


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private Cache cache;

    @Autowired
    JavashopConfig javashopConfig;

    protected final String uuid = "8ac6bc40-a5f9-11e8-afe8-657ead33359b";

    /**
     * 10 days
     */
    static final long EXPIRATIONTIME = 864_000_000;
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    /**
     * 卖家1 sellerId = 3
     */
    protected String seller1 = "eyJhbGciOiJIUzUxMiJ9.eyJzZWxmT3BlcmF0ZWQiOjAsInVpZCI6MSwic3ViIjoiQ0xFUksiLCJzZWxsZXJJZCI6Mywicm9sZSI6IlNVUEVSX1NFTExFUiIsImZvdW5kZXIiOm51bGwsInJvbGVzIjpbIkJVWUVSIiwiU0VMTEVSIiwiQ0xFUksiXSwic2VsbGVyTmFtZSI6IuWwj-W6lyIsImNsZXJrSWQiOjEsImNsZXJrTmFtZSI6Iua1i-ivleW6l-WRmCIsInVzZXJuYW1lIjoi5rWL6K-V6LSm5oi3In0.CEYCkpz0TW4VNFqVIH5nGlbwbDXme9-aUo3k05P0VrKKvlibof1IUqfPDN_jvt1o8PtLRZchkzkup2TSvZOivw";
    /**
     * 卖家2 sellerId = 4
     */
    protected String seller2 = "eyJhbGciOiJIUzUxMiJ9.eyJzZWxmT3BlcmF0ZWQiOjAsInVpZCI6MSwic3ViIjoiQ0xFUksiLCJzZWxsZXJJZCI6NCwicm9sZSI6IlNVUEVSX1NFTExFUiIsImZvdW5kZXIiOm51bGwsInJvbGVzIjpbIkJVWUVSIiwiU0VMTEVSIiwiQ0xFUksiXSwic2VsbGVyTmFtZSI6IuWwj-W6lyIsImNsZXJrSWQiOjEsImNsZXJrTmFtZSI6Iua1i-ivleW6l-WRmCIsInVzZXJuYW1lIjoi5rWL6K-V6LSm5oi3In0.a97FcMUqhzzR3dD1OUsYc4YBMsCSaM30_uKM952F3hTiw1VIuzaYibT9dUvnMYSE6rrsRR2HF4V6XGrCL0Fgww";

    /**
     * 买家1 uid = 1
     */
    protected String buyer1 = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInN1YiI6IkJVWUVSIiwicm9sZXMiOlsiQlVZRVIiXSwiZXhwIjoxNTU2NTA2NDU0LCJ1c2VybmFtZSI6IndhbmdmZW5nIn0.wdJxhrq0coEp4KpoXTdWZ0V59hmOpnFlyTUCIVkwyDVsq2ZjTIDf8l0EYmwRDRi-dgDUhC6Nb9C-_WOl51UNlw";

    /**
     * 买家2 uid = 2
     */
    protected String buyer2 = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjIsInN1YiI6IkJVWUVSIiwicm9sZXMiOlsiQlVZRVIiXSwiZXhwIjoxNTU2NTA2OTAxLCJ1c2VybmFtZSI6IndhbmdmZW5nMSJ9.zUIknjpdLPOfr5N2_c4OTZw_a5aqsipGTUoz2AmH_CsvjnMGnZwrS4FfPo6IAdtJgXcz3HVYDVXbiNszffxwLQ";

    /**
     * 超级管理员的token uid =1
     */
    protected String superAdmin = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInN1YiI6IkFETUlOIiwicm9sZSI6IlNVUEVSX0FETUlOIiwiZm91bmRlciI6bnVsbCwidXVpZCI6bnVsbCwidXNlcm5hbWUiOiLmtYvor5XotKbmiLcifQ.Ozpotzn7rK21q1JupTob-qDAP_qda6iNT2Sqk9sCrHjmDL3d8EM2C-yg4AvaRKLmWsC-guSdMZfikxs4SHUhFQ";

    /**
     * 封装传递参数
     *
     * @param names  参数名
     * @param values 参数值数组
     * @return 组装好的参数map 列表
     */
    protected static List<MultiValueMap<String, String>> toMultiValueMaps(String[] names, String[]... values) {

        List<MultiValueMap<String, String>> stringMultiValueMapList = new ArrayList<>();
        //values.length组数组

        for (String[] value : values) {

            MultiValueMap<String, String> stringMultiValueMap = new LinkedMultiValueMap<>();

            for (int i = 0; i < value.length; i++) {

                stringMultiValueMap.add(names[i], value[i]);


            }
            stringMultiValueMapList.add(stringMultiValueMap);

        }

        return stringMultiValueMapList;
    }


    /**
     * 对象比较matcher
     *
     * @param expect 要比较的对象
     * @return 象比较 ResultMatcher
     */
    protected ResultMatcher objectEquals(Object expect) {
        ObjectResultMatchers matchers = new ObjectResultMatchers();
        return matchers.objectEquals(expect);
    }

    /**
     * 将对象转换为可当做参数传递的map
     *
     * @param obj 对象
     * @return
     * @throws IllegalAccessException
     */
    public static MultiValueMap<String, String> objectToMap(Object obj) throws IllegalAccessException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            String value = StringUtil.toString(field.get(obj));
            map.add(fieldName, value);
        }
        return map;

    }

    /**
     * 对象比较matcher
     *
     * @param expect 要比较的对象
     * @return 象比较 ResultMatcher
     */
    protected ResultMatcher stringEquals(String expect) {
        ObjectResultMatchers matchers = new ObjectResultMatchers();
        return matchers.stringEquals(expect);
    }

    /**
     * 生成买家token
     *
     * @param uid      会员id
     * @param username 会员名称
     * @return
     */

    protected String createBuyerToken(Long uid, String username) {
        Buyer buyer = new Buyer();
        buyer.setUid(uid);
        buyer.setUsername(username);
        ObjectMapper oMapper = new ObjectMapper();
        Map buyerMap = oMapper.convertValue(buyer, HashMap.class);
        String jsonWebToken = Jwts.builder()

                .setClaims(buyerMap)
                .setSubject(Role.BUYER.name())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return jsonWebToken;

    }

    /**
     * 生成卖家token
     *
     * @param uid        会员名称
     * @param sellerId   店铺ID
     * @param userName   会员名称
     * @param sellerName 店铺名称
     * @return
     */
    protected String createSellerToken(Long uid, Long sellerId, String userName, String sellerName) {
        Clerk clerk = new Clerk();
        clerk.setUid(uid);
        clerk.setSellerId(sellerId);
        clerk.setSellerName(sellerName);
        clerk.setClerkId(1);
        clerk.setClerkName(userName);
        clerk.setUsername(userName);
        ObjectMapper oMapper = new ObjectMapper();
        Map clerkMap = oMapper.convertValue(clerk, HashMap.class);
        String jsonWebToken = Jwts.builder()
                .setClaims(clerkMap)
                .setSubject("CLERK")
                .signWith(SignatureAlgorithm.HS512, javashopConfig.getTokenSecret())
                .compact();
        return jsonWebToken;
    }

    /**
     * 将MultiValueMap 转换成Map
     *
     * @return
     */
    protected Map formatMap(MultiValueMap<String, String> map) {
        Map stringMap = new HashMap<>(16);
        Set<String> strings = map.keySet();
        for (String str : strings) {
            stringMap.put(str, map.get(str).get(0));
        }
        return stringMap;
    }

    /**
     * 将Map转换成Map<String,String>
     *
     * @param map
     * @return
     */
    protected Map formatMap(Map<String, Object> map) {
        Map actual = new HashMap(16);
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            actual.put(entry.getKey(), StringUtil.toString(entry.getValue()));
        }
        return actual;
    }

    /**
     * 删除文件夹下文件
     *
     * @param path 文件路径
     * @return
     */
    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                //先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                //再删除空文件夹
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹路径
     */
    public void delFolder(String folderPath) {
        try {
            //删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    protected Map login(String username, String password) throws Exception {
        cache.put("{CAPTCHA}_" + "123456789" + "_" + "LOGIN", "1111");
        String member = mockMvc.perform(get("/passport/login")
                .param("username", username)
                .param("password", password)
                .param("captcha", "1111")
                .param("uuid", "123456789").header("uuid", "123456789"))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();
        Map map = JsonUtil.toMap(member);
        return map;
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param passpord 密码
     * @return
     * @throws Exception
     */
    protected Map loginForMap(String username, String passpord) throws Exception {
        cache.put("CAPTCHA_" + uuid + "_LOGIN", "1111", 1000);
        String json = mockMvc.perform(get("/passport/login")
                .param("username", username)
                .param("password", passpord)
                .param("captcha", "1111")
                .param("uuid", uuid))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
        Map map = JsonUtil.toMap(json);
        return map;
    }

    protected Map register(String name, String password, String mobile) throws Exception{
        cache.put("{SMS_CODE}_" + "REGISTER" + "_"+mobile, "111111");
        String json = mockMvc.perform(post("/passport/register/pc")
                .param("username", name)
                .param("password", password)
                .param("mobile",mobile)
                .param("sms_code","111111")
                .param("uuid", uuid).header("uuid",uuid))
                .andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
        Map map = JsonUtil.toMap(json);
        return map;
    }

}

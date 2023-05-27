package com.enation.app.javashop.api.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.system.ArticleMapper;
import com.enation.app.javashop.mapper.User;
import com.enation.app.javashop.mapper.UserMapper;
import com.enation.app.javashop.model.pagedata.vo.ArticleDetail;
import com.enation.app.javashop.service.pagedata.ArticleManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/7/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
public class SampleTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PintuanGoodsManager pintuanGoodsManager;
    @Test
    public void test() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

    @Test
    public void testAdd() {

        //构造用户模型
        User user = new User();
        user.setAge(10);
        user.setEmail("kingapex1@163.com");
        user.setName("kingapex1");

        //调用mapper的insert方法插入数据
        userMapper.insert(user);

    }


    @Test
    public void testSelect() {

        //构造查询条件
        QueryWrapper wrapper = buildWrapper();

        //通过mapper查询
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    @Test
    public void testDelete() {
        userMapper.deleteById(1L);

        //构造查询条件，按条件删除
        QueryWrapper wrapper = buildWrapper();
        userMapper.delete(wrapper);

    }

    @Test
    public void testUpdate() {
        //构造要更新的用户模型
        User user = new User();
        user.setId(2L);
        user.setEmail("kingapex@javashop.cn");
        user.setName("111");
        userMapper.updateById(user);
    }


    @Test
    public void testPageForMap() {

        //构造查询条件
        QueryWrapper wrapper = buildWrapper();

        //构造分页条件：第一页，页大小为2
        Page page = new Page<>(1, 2);

        //调用mapper进行分页的Map式查询
        IPage<Map<String, Object>> userIPage = userMapper.selectMapsPage(page, wrapper);


        Long total = userIPage.getTotal();
        System.out.println(total);
        List userList = userIPage.getRecords();
        System.out.println(userList);
    }


    @Test
    public void testPageForModel() {

        //构造查询条件
        QueryWrapper wrapper = buildWrapper();

        //构造分页条件：第一页，页大小为2
        Page page = new Page<>(1, 2);

        //调用mapper进行分页的Map式查询
        IPage<User> userIPage = userMapper.selectPage(page, wrapper);

        Long total = userIPage.getTotal();
        System.out.println(total);
        List userList = userIPage.getRecords();
        System.out.println(userList);

    }


    /**
     * 构造查询条件
     *
     * @return
     */
    QueryWrapper buildWrapper() {
    //构造查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");


        //name like %wf%
        wrapper.like("name", "kingapex");
        int i=2;
        wrapper.and(wq -> {
            wq.eq("age", 19);
            if (i == 1) {
                wq.or().eq("age", 18);
            }

        });

        return wrapper;
    }


    @Autowired
    ArticleManager articleManager;

    @Autowired
    ArticleMapper articleMapper;

    @Test
    public void articleTest() {
        List list = new ArrayList();
        list.add(1);
        list.add(492);

        Map params = new HashMap();
        params.put("name","%用户注册协议%");
        params.put("list",list);

        List<ArticleDetail> articleDetailList = articleMapper.listDetail(params);
        articleDetailList.forEach(System.out::print);
    }

    @Test
    public void testCustomSqlSegment() {
        QueryWrapper<User> ew = new QueryWrapper<>();
        ew.like("u.name", "Tom");
        List<User> list = userMapper.customerSqlSegment(ew);
        list.forEach(System.out::print);
     }

}

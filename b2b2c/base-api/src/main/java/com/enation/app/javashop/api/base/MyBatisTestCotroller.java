//package com.enation.app.javashop.api.base;
//
//import com.enation.app.javashop.api.mybatis.User;
//import com.enation.app.javashop.api.mybatis.mapper.UserMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author kingapex
// * @version 1.0
// * @since 7.1.0
// * 2020/7/3
// */
//@RestController
//@RequestMapping("/test/mybatis")
//public class MyBatisTestCotroller {
//
//    @Autowired
//    UserMapper userMapper;
//
//    @GetMapping("/id")
//    public String id(String position) {
//        User user = new User();
//        user.setAge(10);
//        user.setEmail("kingapex@163.com");
//        user.setName("kingapex");
//        userMapper.insert(user);
//
////        IPage page = userMapper.selectPage();
//
//        return ""+ user.getId();
//    }
//
//}

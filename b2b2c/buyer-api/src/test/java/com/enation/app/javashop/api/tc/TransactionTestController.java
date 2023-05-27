package com.enation.app.javashop.api.tc;

import com.enation.app.javashop.framework.database.DaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/13
 */
@RestController
@RequestMapping("/goods/tx")
public class TransactionTestController {

    @Autowired
    private DaoSupport daoSupport;

    @Autowired
    TransactionTestService transactionTestService;

    @GetMapping("/test")
    public String test() {
        transactionTestService.orderAdd();
        return "ok";
    }


    @GetMapping("/get")
    public String get() {

        return ""+daoSupport.queryForLong("select count(0) from es_order");
    }


}

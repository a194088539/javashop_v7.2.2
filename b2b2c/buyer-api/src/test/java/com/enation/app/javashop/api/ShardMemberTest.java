package com.enation.app.javashop.api;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.security.model.Clerk;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberDTO;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.service.statistics.util.DateUtil;
import org.elasticsearch.search.DocValueFormat;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Rollback(false)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ShardMemberTest extends BaseTest {

    @Autowired
    private DaoSupport daoSupport;

    @Autowired
    SnCreator snCreator;

    @Test
    public void query3() {

        String ptSql = "select order_sn from  es_pintuan_child_order WHERE order_id = ?";
        List<Map<String, Object>> ptList = this.daoSupport.queryForList(ptSql, 32989816392134674l);
        String orderSn = ptList.stream().map(map -> map.get("order_sn").toString()).collect(joining(",", "(", ")"));
        orderSn = orderSn.equals("()") ? "(0)" : orderSn;
        String sql = "select o.order_data,o.sn from  es_order o  where o.sn in  " + orderSn;
        List<Map<String, Object>> list = this.daoSupport.queryForList(sql);
        System.out.println(list);
    }
    @Test
    public void update3(){

        String words ="ddddd";
        daoSupport.queryForList("select * from es_goods_words when words=? ",words);
       this.daoSupport.execute("update es_goods_words set goods_num  = (case goods_num-1<0 when true  then 0 else goods_num-1 end ) where words=?", words);

    }

    @Test
    public void query4() {
        List<Object> term = new ArrayList<>();
        Long skuId = 25807468286324737L;
        String sql = "select * from es_goods where goods_id in (  ? ) ";
        term.add(skuId);
        List<GoodsDO> goodsDOS = this.daoSupport.queryForList(sql, GoodsDO.class, term.toArray());

        System.out.println(goodsDOS);
    }

    @Test
    public void query2() {
        List list = daoSupport.queryForList("select * from es_clerk c left join es_member m on c.member_id = m.member_id");
        System.out.println(list);
        list.forEach(System.out::println);

    }

    @Test
    public void snTest() {
        for (int i = 0; i < 10; i++) {
            Long sn = snCreator.create(1);
            if (sn % 2 == 0) {
                // System.out.println("打印原本" + sn);
                long l = sn >> 2;
                if (l % 2 == 0) {
                    System.out.println("打印偶数" + l);
                } else {
                    System.out.println("打印奇数" + l);
                }

            }
        }


    }

    @Test
    public void query() {
        Long a = 22159642901753941L;
        List list = daoSupport.queryForList("SELECT i.* FROM  es_member i  WHERE i.member_id=?", a);
        System.out.println(list);

    }

    @Test
    public void addClerk() {
        Long memberId = snCreator.create(1);
        ClerkDO clerkDO = new ClerkDO();
        clerkDO.setClerkName("名称");
        clerkDO.setCreateTime(DateUtil.getDateline());
        clerkDO.setMemberId(memberId);
        daoSupport.insert(clerkDO);
    }


    @Test
    public void goodsTest() {
        for (int i = 1; i <= 100; i++) {
            GoodsDO goods = new GoodsDO();
            goods.setGoodsId(Long.valueOf(i));
            goods.setSellerId(Long.valueOf(random()));
            goods.setGoodsName("test" + i);
            goods.setQuantity(1);
            goods.setEnableQuantity(1);

            daoSupport.insert(goods);

            GoodsSkuDO goodsSku = new GoodsSkuDO();
            BeanUtils.copyProperties(goods, goodsSku);
            goodsSku.setEnableQuantity(goodsSku.getQuantity());
            goodsSku.setHashCode(-1);
            this.daoSupport.insert("es_goods_sku", goodsSku);

        }
    }


    Random rand = new Random();

    private long random() {
        int index = rand.nextInt(10);
        index++;
        return Long.valueOf(index);
    }


    @Test
    public void addTest() {

        for (int i = 0; i < 100; i++) {

            Long sn = snCreator.create(1);


            //         String orderSn = "" + sn;

            Member member = new Member();

            member.setAddress("设施");
            member.setCity("燕郊");
            member.setNickname("昵称");
            member.setMobile("15711395365");
            member.setEmail("www@126.com");
            member.setCreateTime(DateUtil.getDateline());
            daoSupport.insert(member);
            long id = daoSupport.getLastId("");
            //    this.addClerk(id);

            //    addItem(orderSn);

//            if (i == 70) {
//                throw new RuntimeException("test");
//            }
        }

    }

    private void addItem(String orderSn) {

        for (int i = 1; i < 4; i++) {
            OrderItemsDO orderItemsDO = new OrderItemsDO();
            orderItemsDO.setCatId(3333L);
            orderItemsDO.setGoodsId(Long.valueOf(i));
            orderItemsDO.setName("orderitem name " + i);
            orderItemsDO.setNum(3);
            orderItemsDO.setPrice(99.99);
            orderItemsDO.setOrderSn(orderSn);
            daoSupport.insert(orderItemsDO);

        }

    }

    @Test
    public void te() {


        String sql = "SELECT  o.order_price FROM es_sss_order_goods_data oi LEFT JOIN es_sss_order_data o ON oi.order_sn = o.sn  WHERE o.create_time >=  1577808000 AND o.create_time <=1609430399 ";

        List<Map<String, BigDecimal>> list = daoSupport.queryForList(sql);

        Map<Integer, Long> collect = list.stream()
                .map(map -> {
                    double price = map.get("order_price").doubleValue();
                    if (price >= 0 && price <= 100) {
                        return 1;
                    } else if (price >= 100 && price <= 1000) {
                        return 2;
                    } else if (price >= 1000 && price <= 10000) {
                        return 3;
                    } else if (price >= 10000 && price <= 50000) {
                        return 4;
                    } else {
                        return 0;
                    }
                }).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Map> collect1 = collect.keySet().stream().map(key -> {
            Map map = new HashMap();
            map.put("price_interval", key);
            map.put("t_num", collect.get(key));
            return map;
        }).collect(Collectors.toList());

        System.out.println(collect);
        System.out.println(collect1);
    }

}

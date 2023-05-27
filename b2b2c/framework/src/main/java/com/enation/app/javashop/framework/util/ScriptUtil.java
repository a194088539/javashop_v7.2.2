package com.enation.app.javashop.framework.util;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * 脚本生成工具类
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * @date 2020-01-06
 */
public class ScriptUtil {

    private static final Logger log = LoggerFactory.getLogger(ScriptUtil.class);

    /**
     * 渲染并读取脚本内容
     * @param name 脚本模板名称（例：test.js，test.html，test.ftl等）
     * @param model 渲染脚本需要的数据内容
     * @return
     */
    public static String renderScript(String name, Map<String, Object> model) {
        StringWriter stringWriter = new StringWriter();

        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

            cfg.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(),"/script_tpl");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setNumberFormat("#.##");

            Template temp = cfg.getTemplate(name);

            temp.process(model, stringWriter);

            stringWriter.flush();

            return stringWriter.toString();

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                stringWriter.close();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }

        return null;
    }

    /**
     * @Description:执行script脚本
     * @param method script方法名
     * @param params 参数
     * @param script 脚本
     * @return: 返回执行结果
     * @Author: liuyulei
     * @Date: 2020/1/7
     */
    public static Object executeScript(String method,Map<String,Object> params,String script)  {
        if (StringUtil.isEmpty(script)){
            log.debug("script is " + script);
            return new Object();
        }

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");


            log.debug("脚本参数：");
            for (String key:params.keySet()) {
                log.debug(key + "=" + params.get(key));
                engine.put(key, params.get(key));
            }

            engine.eval(script);
            log.debug("script 脚本 :");
            log.debug(script);

            Invocable invocable = (Invocable) engine;

            return invocable.invokeFunction(method);
        } catch (ScriptException e) {
            log.error(e.getMessage(),e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(),e);
        }
        return new Object();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> model = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", "1578283225");
        params.put("endTime", "1578293225");
        params.put("fullMoney", 100);
        params.put("isFullMinus", 1);
        params.put("minusValue", 5.00);
        params.put("isDiscount", 0);
        params.put("discountValue", 0.8);

        List<Map<String, Object>> giftList = new ArrayList<>();

        Map<String, Object> free = new HashMap<>();
        free.put("type", "freeShip");
        free.put("value", true);
        giftList.add(free);

        Map<String, Object> point = new HashMap<>();
        point.put("type", "point");
        point.put("value", 100);
        giftList.add(point);

        Map<String, Object> gift = new HashMap<>();
        gift.put("type", "gift");
        gift.put("value", 12);
        giftList.add(gift);

        Map<String, Object> coupon = new HashMap<>();
        coupon.put("type", "coupon");
        coupon.put("value", 5);
        giftList.add(coupon);

        params.put("gift", JsonUtil.objectToJson(giftList));

        model.put("promotionActive", params);

        String path = "full_discount.ftl";


        String script = renderScript(path, model);
        log.error(script);

//        Map<String,Object> param = new HashedMap();
//
//        param.put("$price", "110");
//
//        Object object = executeScript("giveGift",param,script);
//        log.error(object + "" );
//        System.out.println(JsonUtil.toList(object.toString()));

//        Map<String, Object> res = JsonUtil.toMap(object.toString());
//        log.error(res + "" );

    }
}

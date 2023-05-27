package com.enation.app.javashop.framework.context.instance;

import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 实例上下文基于redis的实现
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/15
 */
@Service
public class InstanceContextImpl implements InstanceContext {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Lazy
    private Cache redisCache;

    /**
     * 实例缓存key
     */
    public static String REDIS_KEY = "{instances}";

    /**
     * app name，由spring.application.name配置获取
     */
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public Set<String> getApps() {
        List<AppInstance> instances = getInstances();
        Set<String> list = new HashSet();
        for (AppInstance instance : instances) {
            list.add(instance.getAppName());
        }
        return list;
    }


    @Override
    public List<AppInstance> getInstances() {
        List<AppInstance> instances = (List) redisCache.get(REDIS_KEY);

        if (instances == null) {
            instances = new ArrayList<>();
        }

        return instances;
    }

    @Override
    public void register() {

        List<AppInstance> instances = getInstances();

        List<Integer> workIdList = new ArrayList<>();

        logger.debug("现有实例：");
        for (AppInstance instance : instances) {
            logger.debug(instance.getWorkId());
            workIdList.add(instance.getWorkId());
        }

        //按大小排好序
        Collections.sort(workIdList);

        //用于记录上一个id
        int preId = 0;
        int newId = 0;

        for (Integer workId : workIdList) {

            //workId不连续，使workid补充上
            if (workId - 1 != preId) {
                newId = preId + 1;
                break;
            }
            preId = workId;
        }

        //如果newId为0，说明workid都是连续的
        if (newId == 0) {
            newId = workIdList.size() + 1;
        }

        //将本实例压入list
        AppInstance instance = AppInstance.getInstance();
        logger.debug("此实例名称："+newId);
        instance.setWorkId(newId);

        Long now = DateUtil.getDateline();
        Long expirationTime = now + 30;
        instance.setExpirationTime(expirationTime);

        logger.debug("实例名称："+appName);
        instance.setAppName(appName);

        instances.add(instance);

        //更新缓存
        redisCache.put(REDIS_KEY, instances);

    }


    /**
     * 服务心跳
     * 每个10秒执行
     */
    @Scheduled(fixedRate = 10000)
    public void heartbeat() {

        List<AppInstance> instances = getInstances();
        if (instances == null || instances.isEmpty()) {
            return;
        }
        //单例
        AppInstance instance = AppInstance.getInstance();

        Long now = DateUtil.getDateline();
        Long expirationTime = now + 30;

        //挂机的实例列表
        List<AppInstance> shutdownList = new ArrayList<>();

        for (AppInstance appInstance : instances) {

            //找到当前的instance,并延期30秒
            if (instance.getUuid().equals(appInstance.getUuid())) {
                appInstance.setExpirationTime(expirationTime);
            }


            //过期判定为超期，已经down掉了
            if (appInstance.getExpirationTime() < now) {
                shutdownList.add(appInstance);
            }

        }


        //移除掉down掉的instance
        for (AppInstance appInstance : shutdownList) {
            instances.remove(appInstance);
        }

        //更新缓存
        redisCache.put(REDIS_KEY, instances);


    }


}

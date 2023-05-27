package com.enation.app.javashop.framework.redis.configure;

/**
 * Created by kingapex on 26/12/2017.
 *
 * Redis类型
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 26/12/2017
 */
public enum RedisType {

    /**
     * 集群
     */
    cluster,
    /**
     * 哨兵
     */
    sentinel,
    /**
     * 独立
     */
    standalone



}

package com.enation.app.javashop.framework.logs;

import com.enation.app.javashop.framework.util.JsonUtil;

import java.io.Serializable;

/**
 * 默认的logger实现
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-04-17
 */

public class DefaultLoggerImpl implements Logger,Serializable {

    /**
     * 构造器，必须用slf4j loggger来初始化
     * @param logger
     */
    public DefaultLoggerImpl(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    private  org.slf4j.Logger logger;

    @Override
    public void info(String log) {
        if (logger.isInfoEnabled()) {
            logger.info(log);
        }
    }

    @Override
    public void info(String log, Object... obj) {
        logger.info(log,obj);
    }

    @Override
    public void debug(String log) {
        if (logger.isDebugEnabled()) {
            logger.debug(log);
        }


    }

    @Override
    public void debug(Object obj) {
        if (logger.isDebugEnabled() && obj!=null) {
            logger.debug(JsonUtil.objectToJson(obj));
        }
    }

    @Override
    public void debug(String log, Throwable throwable) {
        logger.debug(log,throwable);
    }

    @Override
    public void warn(String log, Object... obj) {
        logger.warn(log,obj);
    }

    @Override
    public void warn(String log) {
        logger.warn(log);
    }

    @Override
    public void error(String log) {
        logger.error(log);

    }

    @Override
    public void error(String log, Object... obj) {
        logger.error(log,obj);
    }

    @Override
    public void error(String log, Throwable throwable) {
        logger.error(log,throwable);
    }

    public org.slf4j.Logger getLogger() {
        return logger;
    }

    public void setLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }
}

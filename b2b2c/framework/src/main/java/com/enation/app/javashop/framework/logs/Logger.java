package com.enation.app.javashop.framework.logs;

/**
 * 日志接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-04-17
 */
public interface Logger {

    /**
     * info
     * @param log
     */
    void info(String log);

    /**
     * info
     * @param log
     * @param obj
     */
    void info(String log,Object...obj);

    /**
     * 调试日志
     * @param log
     */
    void debug(String log);

    /**
     * 调试日志
     * @param obj
     */
    void debug(Object obj);

    /**
     * 调试日志
     * @param log
     * @param throwable
     */
    void debug(String log, Throwable throwable);

    /**
     * warn
     * @param log
     */
    void warn(String log,Object...obj);

    /**
     * warn
     * @param log
     */
    void warn(String log);

    /**
     * 记录错误日志
     * @param log
     */
    void error(String log);

    /**
     * 记录错误日志
     * @param log
     * @param obj
     */
    void error(String log,Object...obj);

    /**
     * 错误日志
     * @param log
     * @param throwable
     */
    void error(String log, Throwable throwable);

}

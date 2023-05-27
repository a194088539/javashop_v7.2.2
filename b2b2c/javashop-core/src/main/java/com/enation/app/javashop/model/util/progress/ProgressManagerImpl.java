package com.enation.app.javashop.model.util.progress;

import com.enation.app.javashop.model.system.vo.ProgressEnum;
import com.enation.app.javashop.model.system.vo.TaskProgress;
import com.enation.app.javashop.model.util.progress.ProgressManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 进度管理实现
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 下午3:10
 */
@Component
public class ProgressManagerImpl implements ProgressManager {

    @Autowired
    private Cache cache;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public TaskProgress getProgress(String id) {
        id = TaskProgress.PROCESS + id;
        return (TaskProgress) cache.get(id);

    }

    @Override
    public void putProgress(String id, TaskProgress progress) {
        id = TaskProgress.PROCESS + id;
        progress.setId(id);
        cache.put(id, progress,100);


    }

    @Override
    public void remove(String id) {
        id = TaskProgress.PROCESS + id;
        cache.remove(id);

    }

    @Override
    public void taskBegin(String key, Integer count) {
        try {
            //新建任务
            TaskProgress tk = new TaskProgress(count);
            //存储任务
            this.putProgress(key, tk);
        } catch (Exception e) {
            logger.error("新建任务失败" + e);
        }
    }

    @Override
    public void taskEnd(String key, String message) {
        try {
            //获取任务
            TaskProgress tk = this.getProgress(key);
            if (tk != null) {
                tk.step(message);
                tk.success();
                //更新进度 重新放入缓存
                this.putProgress(key, tk);
            }
        } catch (Exception e) {
            logger.error("任务结束异常" + e);
        }
    }

    @Override
    public void taskError(String key, String message) {
        try {
            TaskProgress tk = this.getProgress(key);
            if (tk != null) {
                tk.setTaskStatus(ProgressEnum.EXCEPTION.name());
                tk.setMessage(message);
                this.putProgress(key, tk);
            }
        } catch (Exception e) {
            logger.error("任务异常" + e);
        }
    }

    @Override
    public void taskUpdate(String key, String message) {
        try {

            logger.debug("key is "+ key);
            TaskProgress tk = this.getProgress(key);
            if (tk != null) {
                logger.debug("tk is ");
                logger.debug(tk);
                tk.step(message);
                this.putProgress(key, tk);
            }else {
                logger.debug("tk  is null");
                logger.debug(tk);
            }
        } catch (Exception e) {
            logger.error("更新任务失败" + e);
        }
    }
}

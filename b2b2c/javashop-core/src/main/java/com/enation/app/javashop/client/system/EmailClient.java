package com.enation.app.javashop.client.system;

import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.model.base.vo.EmailVO;

/**
 * @author fk
 * @version v2.0
 * @Description: 邮件
 * @date 2018/8/13 16:25
 * @since v7.0.0
 */
public interface EmailClient {

    /**
     * 邮件发送实现，供消费者调用
     *
     * @param emailVO
     */
    void sendEmail(EmailVO emailVO);

    /**
     * 验证邮箱验证码
     *
     * @param scene  业务场景
     * @param email  电子邮箱
     * @param code   电子邮箱验证码
     * @return 是否通过校验 true通过，false不通过
     */
    boolean valid(String scene, String email, String code);

    /**
     * 发送(发送邮箱验证码)消息
     *
     * @param byName    操作，替换内容
     * @param email     电子邮箱
     * @param sceneType 操作类型
     */
    void sendEmailMessage(String byName, String email, SceneType sceneType);
}

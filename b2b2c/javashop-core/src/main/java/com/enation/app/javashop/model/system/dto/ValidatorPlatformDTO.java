package com.enation.app.javashop.model.system.dto;

import com.enation.app.javashop.model.system.vo.AliyunAfsVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;

/**
 * 验证平台DTO
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@ApiIgnore
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ValidatorPlatformDTO implements Serializable {

    private static final long serialVersionUID = -5875562388711541719L;

    @ApiModelProperty(value = "验证平台类型", name = "validator_type IMAGE：图片验证码，ALIYUN：阿里云滑动验证")
    private String validatorType;

    @ApiModelProperty(value = "阿里云滑动验证参数", name = "aliyun_afs")
    private AliyunAfsVO aliyunAfs;

    public String getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(String validatorType) {
        this.validatorType = validatorType;
    }

    public AliyunAfsVO getAliyunAfs() {
        return aliyunAfs;
    }

    public void setAliyunAfs(AliyunAfsVO aliyunAfs) {
        this.aliyunAfs = aliyunAfs;
    }

    @Override
    public String toString() {
        return "ValidatorPlatformDTO{" +
                "validatorType='" + validatorType + '\'' +
                ", aliyunAfs=" + aliyunAfs +
                '}';
    }
}

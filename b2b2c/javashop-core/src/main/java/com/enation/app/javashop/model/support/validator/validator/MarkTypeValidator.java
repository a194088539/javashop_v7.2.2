package com.enation.app.javashop.model.support.validator.validator;

import com.enation.app.javashop.model.support.validator.annotation.MarkType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * @author fk
 * @version v2.0
 * @Description: SortType 验证
 * @date 2018/4/3 11:44
 * @since v7.0.0
 */
public class MarkTypeValidator implements ConstraintValidator<MarkType, String> {

    private final String[] ALL_STATUS = {"hot", "new","recommend"};

    @Override
    public void initialize(MarkType status) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.asList(ALL_STATUS).contains(value);
    }
}

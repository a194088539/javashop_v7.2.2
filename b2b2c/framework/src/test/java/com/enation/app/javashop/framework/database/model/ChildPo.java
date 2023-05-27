package com.enation.app.javashop.framework.database.model;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.enation.app.javashop.framework.util.StringUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author liuyulei
 * @version 1.0
 * @Description: TODO
 * @date 2019/2/14 15:56
 * @since v7.0
 */
@Table(name="es_jdbc_test")
public class ChildPo extends JdbcTestPo implements Serializable {

    private String childName;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChildPo childPo = (ChildPo) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(childName, childPo.childName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(childName)
                .toHashCode();
    }


    @Override
    public String toString() {
        return "ChildPo{" +
                "childName='" + childName + '\'' +
                '}';
    }


}

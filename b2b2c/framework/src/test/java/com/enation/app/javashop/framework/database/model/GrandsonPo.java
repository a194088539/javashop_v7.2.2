package com.enation.app.javashop.framework.database.model;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Table;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author liuyulei
 * @version 1.0
 * @Description: TODO
 * @date 2019/2/14 17:19
 * @since v7.0
 */
@Table(name="es_jdbc_test")
public class GrandsonPo extends  ChildPo implements Serializable {

    private String grandSonName;


    public String getGrandSonName() {
        return grandSonName;
    }

    public void setGrandSonName(String grandSonName) {
        this.grandSonName = grandSonName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GrandsonPo that = (GrandsonPo) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(grandSonName, that.grandSonName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(grandSonName)
                .toHashCode();
    }


    @Override
    public String toString() {
        return "GrandsonPo{" +
                "grandSonName='" + grandSonName + '\'' +
                '}';
    }
}

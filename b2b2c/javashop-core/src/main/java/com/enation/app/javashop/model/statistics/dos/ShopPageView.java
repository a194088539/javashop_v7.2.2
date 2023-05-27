package com.enation.app.javashop.model.statistics.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;

import java.io.Serializable;

/**
 * ShopPV
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-07 上午11:19
 */
@TableName("es_sss_shop_pv")
public class ShopPageView  implements Serializable {

    @TableId(type= IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "seller_id")
    private Long sellerId;

    @TableField("vs_year")
    private Integer year;

    @TableField("vs_month")
    private Integer month;

    @TableField("vs_day")
    private Integer day;

    @TableField("vs_num")
    private Integer num;

    @Column(name="create_time")
    private Long createTime;

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (num != null ? num.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}

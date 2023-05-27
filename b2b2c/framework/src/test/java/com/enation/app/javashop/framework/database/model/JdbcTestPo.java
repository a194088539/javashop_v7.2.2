package com.enation.app.javashop.framework.database.model;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author mengyuanming
 * @Version 1.0
 * @Since 7.0
 * 2018年3月30日下午5:07:25
 */
@Table(name="es_jdbc_test")
public class JdbcTestPo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -70941111604460441L;

	@Id(name = "test_id")
	private Long testId;

	@Column()
	private String name;

	@Column
	private Integer num;

	@Column()
	private Long time;

	@Column(name="total_price")
	private Double totalPrice;

	@Column(name="average_price")
	private Float averagePrice;

	@Column(name="my_value",allowNullUpdate = true)
	private String myValue;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public Float getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(Float averagePrice) {
		this.averagePrice = averagePrice;
	}

	@Override
	public String toString() {
		return "JdbcTestPo ["
				+ "testId=" + testId +
				", name=" + name +
				", num=" + num +
				", time=" + time +
				", totalPrice=" + totalPrice +
				", averagePrice=" + averagePrice +
				",myValue="+ myValue+
				"]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JdbcTestPo that = (JdbcTestPo) o;

		return new EqualsBuilder()
				.append(testId, that.testId)
				.append(name, that.name)
				.append(num, that.num)
				.append(time, that.time)
				.append(totalPrice, that.totalPrice)
				.append(averagePrice, that.averagePrice)
				.append(myValue,that.myValue)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(testId)
				.append(name)
				.append(num)
				.append(time)
				.append(totalPrice)
				.append(averagePrice)
				.append(myValue)
				.toHashCode();
	}

	public String getMyValue() {
		return myValue;
	}

	public void setMyValue(String myValue) {
		this.myValue = myValue;
	}
}

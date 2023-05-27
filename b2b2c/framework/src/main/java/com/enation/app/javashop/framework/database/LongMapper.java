package com.enation.app.javashop.framework.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * Long mapper
 * @author kingapex
 * @version v1.0
 * @since v7.0.0
 * 2018年3月23日 上午10:26:41
 */
public class LongMapper implements RowMapper<Long> {

	@Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
		Long  v = rs.getLong(1);
		return v;
	}

}

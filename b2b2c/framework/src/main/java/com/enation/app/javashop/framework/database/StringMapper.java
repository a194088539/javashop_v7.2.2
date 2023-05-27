package com.enation.app.javashop.framework.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 字符串型mapper
 * @author kingapex
 * @version v1.0
 * @since v7.0.0
 * 2018年3月23日 上午10:26:41
 */
public class StringMapper implements  RowMapper<String> {

	
	@Override
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		String str = rs.getString(1);
		return str;
	}

}

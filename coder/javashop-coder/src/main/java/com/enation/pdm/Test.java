package com.enation.pdm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.enation.pdm.model.Column;
import com.enation.pdm.model.Project;
import com.enation.pdm.model.Table;

public class Test {

	public static void main(String[] args) {
		try {
			
			Project project = PDMParser.parse(new File("/Users/xulipeng/WorkFile/eclipse/workspace/trunk-bug/docs/DB/订单服务.pdm"));
			
			//所有表的集合
			List<Table> tableList = project.getTableList();
			for (Table table : tableList) {
				String tableName = table.getName();
				String tableCode = table.getCode();

				System.out.println("表名:"+tableName+"  表代码:"+tableCode);
				
				List<Column> columnList = table.getColumns();
				for (Column column : columnList) {
					String columnName = column.getName();
					String columnCode = column.getCode();
					String columnType = column.getType();
					Integer length = column.getLength();
					boolean pkFlag = column.isPkFlag(); //是否为主键
					String comment = column.getComment();
					System.out.print(" 字段名："+columnName);
					System.out.print(" 字段代码："+columnCode);
					System.out.print(" 字段类型："+columnType);
					System.out.print(" 字段长度："+length);
					System.out.print(" 是否主键："+pkFlag);
					System.out.print(" 注释："+comment);
					System.out.println("");
				}
				System.out.println("\n\r");
			}
			
			//System.out.println(PDMParser.parse(new File("/Users/xulipeng/WorkFile/eclipse/workspace/trunk-bug/docs/DB/订单服务.pdm")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

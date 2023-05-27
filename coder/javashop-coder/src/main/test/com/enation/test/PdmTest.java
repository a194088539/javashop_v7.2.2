package com.enation.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.framework.database.IDaoSupport;
import com.enation.pdm.PDMParser;
import com.enation.pdm.model.Column;
import com.enation.pdm.model.Project;
import com.enation.pdm.model.Table;

public class PdmTest {

	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Test
	public void test() {
		try {
			
			Project project = PDMParser.parse(new File("/Users/xulipeng/WorkFile/eclipse/workspace/trunk-bug/docs/DB/订单服务.pdm"));
			
			//所有表的集合
			List<Table> tableList = project.getTableList();
			for (Table table : tableList) {
				String tableName = table.getName().split("\\(")[0];
				String tableCode = table.getCode();
				
				String[] name = tableCode.split("_");
				
				String e_name = "";
				for (int i = 1; i < name.length; i++) {
					String str = name[i];
					String caseName = str.substring(0,1).toUpperCase() + str.substring(1);
					e_name += caseName; 
				}
				
				System.out.println("表名:"+tableName+"  表代码:"+tableCode +"  英文名:"+e_name);
				
				
				List<Column> columnList = table.getColumns();
				for (Column column : columnList) {
					String columnName = column.getName();
					String columnCode = column.getCode();
					String columnType = column.getType();
					boolean pkFlag = column.isPkFlag(); //是否为主键
					String comment = column.getComment();
					
					String type = columnType.split("\\(")[0];
					String length = columnType.substring(0, columnType.length()-1).split("\\(")[1];
					
					System.out.println(" 字段名:"+columnCode+" 提示文字:"+columnName+" 类型:"+type+" 长度:"+length+" 是否主键:"+pkFlag);

					//					System.out.print(" 字段名："+columnName);
//					System.out.print(" 字段代码："+columnCode);
//					System.out.print(" 字段类型："+columnType);
//					System.out.print(" 字段长度："+length);
//					System.out.print(" 是否主键："+pkFlag);
//					System.out.print(" 注释："+comment);
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

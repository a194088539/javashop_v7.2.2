package com.enation.app.javashop.framework.util;

import com.enation.app.javashop.framework.database.ColumnMeta;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 反射机制工具类
 * @author Snow create in 2018/3/21
 * @version v2.0
 * @since v7.0.0
 */
public class ReflectionUtil {


	/**
	 * 将po对象中有属性和值转换成map
	 * @param po
	 * @return
	 */
	@SuppressWarnings("AlibabaCollectionInitShouldAssignCapacity")
	public static Map po2Map(Object po) {
		Map poMap = new HashedMap();
		ColumnMeta columnMeta = getColumnMeta(po);

		Object[] columnName = columnMeta.getNames();
		Object[] columnValue = columnMeta.getValues();

		for (int i = 0; i < columnValue.length; i++) {
			poMap.put(columnName[i],columnValue[i]);
		}

		return poMap;
	}


	/**
	 * 遍历实体类的属性和数据类型以及属性值
	 * @param model
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static ColumnMeta getColumnMeta(Object model)  {

		ColumnMeta columnMeta = new ColumnMeta();

		try {
			// 获取实体类的所有属性，返回Field数组
			/*** 修改获取不到父类属性的问题    update by liuyulei 2019-2-14 **********begin*/
			List<Field> fields = new ArrayList<>();
			//获取所有属性(包含父类)
			fields = getParentField(model.getClass(),fields);
			/*** 修改获取不到父类属性的问题 **********end*/


			//属性名
			List columnName = new ArrayList();
			//属性值
			List columnValue = new ArrayList();

			/**
			 * 遍历所有的属性，过滤掉满足以下条件的属性。
			 * 1、属性值为null的数据
			 * 2、没有添加自定义标签 @id 和 @Column 的属性
			 */
			for (Field field:fields ) {

				if(field.isAnnotationPresent(Id.class) ){
					Id id = field.getAnnotation(Id.class);
					if(!StringUtil.isEmpty(id.name())) {
						columnMeta.setPrimaryKeyName(id.name());
					}else {
						columnMeta.setPrimaryKeyName(field.getName());
					}
				}

				if(field.isAnnotationPresent(Column.class) ){

					String dbName = field.getName();
					Column column = field.getAnnotation(Column.class);
					if(!StringUtil.isEmpty(column.name())){
						dbName = column.name();
					}
					ReflectionUtils.makeAccessible(field);
					Object value = ReflectionUtils.getField(field,model);
					if ( value == null && !column.allowNullUpdate()){
						continue;
					}

					columnName.add(dbName);
					columnValue.add(value);
				}
			}

			columnMeta.setNames(columnName.toArray());
			columnMeta.setValues(columnValue.toArray());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnMeta;
	}

	/**
	 * 根据Class读取主键的字段名
	 * @param clazz
	 * @return
	 */
	public static String  getPrimaryKey(Class clazz){
		String  columnId = "";
		/*** 修改获取不到父类属性的问题    update by liuyulei 2019-09-16 **********begin*/
		List<Field> fields = new ArrayList<>();
		//获取所有属性(包含父类)
		fields = getParentField(clazz,fields);
		/*** 修改获取不到父类属性的问题 **********end*/
		for (Field field : fields) {
			//主键字段名
			if(field.isAnnotationPresent(Id.class)){
				Id id =  field.getAnnotation(Id.class);
				String columnIdName = id.name();

				//是否自定义的属性名
				if(StringUtil.isEmpty(columnIdName)){
					columnId = field.getName();
				}else{
					columnId = columnIdName;
				}
				break;
			}
		}
		return columnId;
	}


	/**
	 * 获取父类中注解表名
	 * @param calzz
	 * @return
	 * add by liuyulei 2019-12-12
	 */
	public static String getTableName(Class<?> calzz){
		//获取当前类注解表名
		Table table = calzz.getAnnotation(Table.class);

		//判断当前类注解表名
		if(table == null || StringUtil.isEmpty(table.name())){
			//如果为空，且其父类不为Object
			if(calzz.getSuperclass() != Object.class){
				//获取其父类注解表名
				return getTableName(calzz.getSuperclass());
			}
		}

		return table.name();
	}

	private static String getFieldName(String methodName){

		methodName = methodName.substring(3);
		methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
		return methodName;
	}


	/**
	 * 递归获取所有父类的属性
	 * @param calzz
	 * @param list
	 * @return
	 *
	 * add by liuyulei 2019-02-14
	 */
	protected static List<Field> getParentField(Class<?> calzz,List<Field> list){

		if(calzz.getSuperclass() !=  Object.class){
			getParentField(calzz.getSuperclass(),list);
		}

		Field[] fields = calzz.getDeclaredFields();
		list.addAll(arrayConvertList(fields));

		return list ;
	}

	/**
	 * 将数组转换成List
	 * @param fields
	 * @return
	 * add by liuyulei 2019-02-14
	 *
	 */
	protected static List<Field> arrayConvertList(Field[] fields){
		List<Field> resultList = new ArrayList<>(fields.length);
		Collections.addAll(resultList,fields);
		return  resultList;

	}
}

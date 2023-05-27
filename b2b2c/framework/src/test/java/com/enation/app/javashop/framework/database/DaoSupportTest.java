package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.FrameworkApplication;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.model.ChildPo;
import com.enation.app.javashop.framework.database.model.GrandsonPo;
import com.enation.app.javashop.framework.database.model.JdbcTestPo;
import com.enation.app.javashop.framework.util.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 单元测试类，基于Mysql的基本增删改查操作
 *
 * @author Snow create in 2018/3/21
 * @version v2.0
 * @since v7.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@ComponentScan("com.enation.app.javashop")
public class DaoSupportTest {

	@Autowired
	private DaoSupport daoSupport;

	/**
	 * 每次单元测试时，先创建一个测试用的表
	 */
	@Before
	public void createTable() {
		String sql = "create table es_jdbc_test (test_id int primary key not null auto_increment,"
				+ "name varchar(50),num int,time BIGINT(11),total_price decimal(10,2)," + "average_price float(8,4) ,my_value varchar(255),child_name varchar(255),grand_son_name varchar(255))";
		this.daoSupport.execute(sql);
	}

	/**
	 * 测试结束后，删除测试用表
	 */
	@After
	public void dropTable() {
		String sql = "drop table es_jdbc_test";
		this.daoSupport.execute(sql);
	}

	/**
	 * 根据id获取一个 tet po对象
	 *
	 * @param id id值
	 * @return po对象
	 */
	private JdbcTestPo get(Long id) {
		return daoSupport.queryForObject(JdbcTestPo.class, id);
	}

	/**
	 * 直接使用sql语句与参数操作数据库
	 */
	@Test
	public void executeUpdate() {

		this.insertByT();

		String sql = " update es_jdbc_test set name = ?,num = ?,time = ?,total_price = ?,average_price = ? ";

		this.daoSupport.execute(sql, "mym", 38, 1522229909, 53.8, 14.2f);

		JdbcTestPo testPo = new JdbcTestPo();

		testPo.setTestId(1L);
		testPo.setName("mym");
		testPo.setNum(38);
		testPo.setTime((long) 1522229909);
		testPo.setTotalPrice(53.8);
		testPo.setAveragePrice(14.2f);

		// 验证新数据
		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 获取最后添加的id
	 */
	@Test
	public void getLastId() {

		this.insertByT();

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setTestId(1L);
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);

		JdbcTestPo dbPo = this.get(this.daoSupport.getLastId("es_jdbc_test"));
		Assert.assertEquals(testPo, dbPo);
	}

	/**
	 * 表名与Map插入数据
	 */
	@Test
	public void insertByMap() {

		Map map = new HashMap(5);

		map.put("name", "wf");
		map.put("num", 18);
		map.put("time", 12345678901L);
		map.put("total_price", 22.2);
		map.put("average_price", 11.11f);

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setTestId(1L);
		testPo.setName((String) map.get("name"));
		testPo.setNum((Integer) map.get("num"));
		testPo.setTime((Long) map.get("time"));
		testPo.setTotalPrice((Double) map.get("total_price"));
		testPo.setAveragePrice((Float) map.get("average_price"));

		this.daoSupport.insert("es_jdbc_test", map);

		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 表名与Po插入数据
	 */
	@Test
	public void insertByPo() {
		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setTestId(1L);
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);

		this.daoSupport.insert("es_jdbc_test", testPo);

		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);
	}

	/**
	 * 查询int值
	 */
	@Test
	public void queryForInt() {
		this.insertByT();

		String sql = " select num from es_jdbc_test where test_id = ? ";

		int num = this.daoSupport.queryForInt(sql, 1);

		JdbcTestPo dbPo = this.get(1L);
		int dbNum = dbPo.getNum();

		Assert.assertEquals(num, dbNum);

	}

	/**
	 * 查询float值
	 */
	@Test
	public void queryForFloat() {
		this.insertByT();

		String sql = " select average_price from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ? ";

		Float averagePrice = this.daoSupport.queryForFloat(sql, "wf", 18, 12345678901L, 22.2, 11.11f);

		JdbcTestPo dbPo = this.get(1L);
		Float dbAvPrice = dbPo.getAveragePrice();

		Assert.assertEquals(averagePrice, dbAvPrice);

	}

	/**
	 * 查询long值
	 */
	@Test
	public void queryForLong() {
		this.insertByT();

		String sql = " select time from es_jdbc_test where name = ? and num = ? and total_price = ? and average_price = ? ";

		Long time = this.daoSupport.queryForLong(sql, "wf", 18, 22.2, 11.11f);

		JdbcTestPo dbPo = this.get(1L);
		Long dbTime = dbPo.getTime();

		Assert.assertEquals(time, dbTime);

	}

	/**
	 * 查询double值
	 */
	@Test
	public void queryForDouble() {

		this.insertByT();

		String sql = " select total_price from es_jdbc_test where name = ? and num = ? and time = ? and average_price = ? ";

		Double totalPrice = this.daoSupport.queryForDouble(sql, "wf", 18, 12345678901L, 11.11f);

		JdbcTestPo dbPo = this.get(1L);
		Double dbTotalPrice = dbPo.getTotalPrice();

		Assert.assertEquals(totalPrice, dbTotalPrice);

	}

	/**
	 * 查询String值
	 */
	@Test
	public void queryForString() {
		this.insertByT();

		String sql = " select name from es_jdbc_test where  num = ? and time = ? and total_price = ? and average_price = ? ";

		String name = this.daoSupport.queryForString(sql, 18, 12345678901L, 22.2, 11.11f);
		JdbcTestPo dbPo = this.get(1L);
		String dbName = dbPo.getName();

		Assert.assertEquals(name, dbName);

	}

	/**
	 * sql和参数查询List
	 */
	@Test
	public void queryListByArgs() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 5; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);

			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);
		}

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		List<Map> dbList = this.daoSupport.queryForList(sql, "wf", 18, 12345678901L, 22.2, 11.11f);

		for (int i = 1; i <= 5; i++) {

			JdbcTestPo testPo = list.get(i - 1);

			Map map = dbList.get(i - 1);
			JdbcTestPo dbPo = new JdbcTestPo();
			dbPo.setTestId((Long) map.get("test_id"));
			dbPo.setName((String) map.get("name"));
			dbPo.setNum((Integer) map.get("num"));
			dbPo.setTime((Long) map.get("time"));
			dbPo.setTotalPrice(Double.parseDouble(map.get("total_price").toString()));
			dbPo.setAveragePrice((Float) map.get("average_price"));

			Assert.assertEquals(testPo, dbPo);
		}

	}

	/**
	 * sql和class查询List
	 */
	@Test
	public void queryListByClass() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 5; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);
			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);

		}

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		List<JdbcTestPo> dblist = this.daoSupport.queryForList(sql, JdbcTestPo.class, "wf", 18, 12345678901L, 22.2,
				11.11f);

		for (int i = 1; i <= 5; i++) {

			JdbcTestPo testPo = list.get(i - 1);

			JdbcTestPo dbPo = dblist.get(i - 1);

			Assert.assertEquals(testPo, dbPo);

		}

	}

	/**
	 * 根据参数查询分页list
	 */
	@Test
	public void queryListPage() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 10; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);
			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);
		}

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		List<Map> dbList = new ArrayList();

		for (int i = 1; i <= 5; i++) {
			List pageList = this.daoSupport.queryForListPage(sql, i, 2, "wf", 18, 12345678901L, 22.2, 11.11f);
			dbList.addAll(pageList);
		}

		for (int i = 1; i <= 10; i++) {

			JdbcTestPo testPo = list.get(i - 1);

			Map map = dbList.get(i - 1);
			JdbcTestPo dbPo = new JdbcTestPo();
			dbPo.setTestId((Long) map.get("test_id"));
			dbPo.setName((String) map.get("name"));
			dbPo.setNum((Integer) map.get("num"));
			dbPo.setTime((Long) map.get("time"));
			dbPo.setTotalPrice(Double.parseDouble(map.get("total_price").toString()));
			dbPo.setAveragePrice((Float) map.get("average_price"));

			Assert.assertEquals(testPo, dbPo);
		}

	}

	/**
	 * 查询map
	 */
	@Test
	public void queryMap() {

		this.insertByT();

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ? ";

		Map dbMap = this.daoSupport.queryForMap(sql, "wf", 18, 12345678901L, 22.2, 11.11f);

		JdbcTestPo testPo = new JdbcTestPo();

		testPo.setTestId(1L);
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);

		JdbcTestPo dbPo = new JdbcTestPo();

		dbPo.setTestId((Long) dbMap.get("test_id"));
		dbPo.setName((String) dbMap.get("name"));
		dbPo.setNum((Integer) dbMap.get("num"));
		dbPo.setTime((Long) dbMap.get("time"));
		dbPo.setTotalPrice(Double.parseDouble(dbMap.get("total_price").toString()));
		dbPo.setAveragePrice((Float) dbMap.get("average_price"));

		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 查询page
	 */
	@Test
	public void queryPage() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 10; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);
			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);
		}

		WebPage testPage = new WebPage(1L, Long.parseLong(Integer.valueOf(list.size()).toString()), 10L, list);

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		WebPage dbPage = this.daoSupport.queryForPage(sql, 1, 10, "wf", 18, 12345678901L, 22.2, 11.11f);

		List<Map> dbList = dbPage.getData();
		List<JdbcTestPo> dbConverList = new ArrayList();

		for (Map map : dbList) {
			JdbcTestPo dbPo = new JdbcTestPo();

			dbPo.setTestId((Long) map.get("test_id"));
			dbPo.setName((String) map.get("name"));
			dbPo.setNum((Integer) map.get("num"));
			dbPo.setTime((Long) map.get("time"));
			dbPo.setTotalPrice(Double.parseDouble(map.get("total_price").toString()));
			dbPo.setAveragePrice((Float) map.get("average_price"));
			dbConverList.add(dbPo);

		}

		dbPage.setData(dbConverList);

		Assert.assertEquals(testPage, dbPage);

	}

	/**
	 * 增加统计总数据量sql参数查询page
	 */
	@Test
	public void queryPageAddCountSql() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 10; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);
			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);
		}

		WebPage testPage = new WebPage(1L, Long.parseLong(Integer.valueOf(list.size()).toString()), 10L, list);

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		String countSql = " select count(0) from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ? ";

		WebPage dbPage = this.daoSupport.queryForPage(sql, countSql, 1, 10, "wf", 18, 12345678901L, 22.2, 11.11f);

		List<Map> dbList = dbPage.getData();
		List<JdbcTestPo> dbConverList = new ArrayList();

		for (Map map : dbList) {
			JdbcTestPo dbPo = new JdbcTestPo();

			dbPo.setTestId((Long) map.get("test_id"));
			dbPo.setName((String) map.get("name"));
			dbPo.setNum((Integer) map.get("num"));
			dbPo.setTime((Long) map.get("time"));
			dbPo.setTotalPrice(Double.parseDouble(map.get("total_price").toString()));
			dbPo.setAveragePrice((Float) map.get("average_price"));
			dbConverList.add(dbPo);

		}

		dbPage.setData(dbConverList);

		Assert.assertEquals(testPage, dbPage);

	}

	/**
	 * 根据class查询page
	 */
	@Test
	public void queryPageByClass() {

		List<JdbcTestPo> list = new ArrayList();
		for (Long i = 1L; i <= 10; i++) {

			JdbcTestPo testPo = new JdbcTestPo();
			testPo.setTestId(i);
			testPo.setName("wf");
			testPo.setNum(18);
			testPo.setTime(12345678901L);
			testPo.setTotalPrice(22.2);
			testPo.setAveragePrice(11.11f);
			list.add(testPo);
			this.daoSupport.insert("es_jdbc_test", testPo);
		}

		WebPage testPage = new WebPage(1L, Long.parseLong(Integer.valueOf(list.size()).toString()), 10L, list);

		String sql = "select * from es_jdbc_test where name = ? and num = ? and time = ? and total_price = ? and average_price = ?";

		WebPage dbPage = this.daoSupport.queryForPage(sql, 1, 10, JdbcTestPo.class, "wf", 18, 12345678901L, 22.2, 11.11f);

		Assert.assertEquals(testPage, dbPage);

	}

	/**
	 * 更新操作
	 */
	@Test
	public void update() {

		this.insertByT();

		Map fields = new HashMap(5);

		fields.put("name", "mym");
		fields.put("num", 38);
		fields.put("time", 12345678901L);
		fields.put("total_price", 22.2);
		fields.put("average_price", 11.11f);

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setTestId(1L);
		testPo.setName((String) fields.get("name"));
		testPo.setNum((Integer) fields.get("num"));
		testPo.setTime((Long) fields.get("time"));
		testPo.setTotalPrice(Double.parseDouble(fields.get("total_price").toString()));
		testPo.setAveragePrice((Float) fields.get("average_price"));

		Map<String, Integer> where = new HashMap(1);

		where.put("test_id", 1);

		this.daoSupport.update("es_jdbc_test", fields, where);

		JdbcTestPo dbPo = this.get(1L);

		Assert.assertEquals(testPo, dbPo);


	}

	/**
	 * 通过Po和Map插入更新数据
	 */
	@Test
	public void updateByPo() {
		this.insertByPo();

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(22);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(222.2);

		Map where = new HashMap<>(1);
		where.put("name", testPo.getName());

		this.daoSupport.update("es_jdbc_test", testPo, where);
	}


	@Test
	public void updateByNullPo() {
		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);
		testPo.setMyValue("1");
		this.daoSupport.insert(testPo);

		testPo.setName("wf");
		testPo.setNum(22);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(222.2);
		testPo.setAveragePrice(11.11f);
		testPo.setMyValue("");
		Map where = new HashMap<>(1);
		where.put("name", testPo.getName());

		this.daoSupport.update("es_jdbc_test", testPo, where);

		// 断言和库中的一致
		testPo.setTestId(1L);

		//验证myvalue强制被更新
		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);


	}

	/**
	 * 根据泛型类插入数据
	 */
	@Test
	public void insertByT() {

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);
		this.daoSupport.insert(testPo);

		// 断言和库中的一致
		testPo.setTestId(1L);

		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 根据泛型和id，更新数据
	 */
	@Test
	public void updateByT() {
		this.insertByT();

		// 断言和库中的一致
		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(2);
		testPo.setTotalPrice(20.09D);
		testPo.setTestId(1L);
		testPo.setAveragePrice(10.1f);

		this.daoSupport.update(testPo, testPo.getTestId());

		// time不是数据库字段，但通过map方式更新 也会生效
		testPo.setTime(12345678901L);
		JdbcTestPo dbPo = this.get(1L);
		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 根据class和id删除数据
	 */
	@Test
	public void deleteByClass() {
		this.insertByT();
		this.daoSupport.delete(JdbcTestPo.class, 1L);
	}

	/**
	 * 根据class和id，查询泛型类
	 */
	@Test
	public void queryObjectById() {
		this.insertByT();

		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setTestId(1L);
		testPo.setAveragePrice(11.11f);

		JdbcTestPo dbPo = this.daoSupport.queryForObject(JdbcTestPo.class, 1L);
		Assert.assertEquals(testPo, dbPo);
	}

	/**
	 * 查询泛型类
	 */
	@Test
	public void queryObject() {

		this.insertByT();
		JdbcTestPo testPo = new JdbcTestPo();
		testPo.setName("wf");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setTestId(1L);
		testPo.setAveragePrice(11.11f);

		JdbcTestPo dbPo = this.daoSupport.queryForObject("select * from es_jdbc_test where test_id = ?", JdbcTestPo.class, 1);

		Assert.assertEquals(testPo, dbPo);

	}

	/**
	 * 子类使用父类属性插入数据
	 */
	@Test
	public void testParentObj() {

		GrandsonPo testPo = new GrandsonPo();
		testPo.setName("testPo");
		testPo.setNum(18);
		testPo.setTime(12345678901L);
		testPo.setTotalPrice(22.2);
		testPo.setAveragePrice(11.11f);
		testPo.setGrandSonName("grandSonPo");
		testPo.setChildName("ChildPo");
		this.daoSupport.insert(testPo);

		// 断言和库中的一致
		testPo.setTestId(1L);

		GrandsonPo dbPo =this.daoSupport.queryForObject("select * from es_jdbc_test where test_id = ?", GrandsonPo.class, 1L);
		Assert.assertEquals(testPo, dbPo);

		ChildPo childPo = new ChildPo();
		childPo.setName("testPo");
		childPo.setNum(18);
		childPo.setTime(12345678901L);
		childPo.setTotalPrice(22.2);
		childPo.setAveragePrice(11.11f);
		childPo.setChildName("ChildPo");
		this.daoSupport.insert(testPo);



		// 断言和库中的一致
		childPo.setTestId(2L);

		ChildPo dbChild =this.daoSupport.queryForObject("select * from es_jdbc_test where test_id = ?", ChildPo.class, 2);
		Assert.assertEquals(childPo, dbChild);

	}


	public static List<Field> getParentField(Class<?> calzz,List<Field> list){

		if(calzz.getSuperclass() !=  Object.class){
			getParentField(calzz.getSuperclass(),list);
		}

		Field[] fields = calzz.getDeclaredFields();
		list.addAll(arrayConvertList(fields));

		return list ;
	}

	public static List<Field> arrayConvertList(Field[] fields){
		List<Field> resultList = new ArrayList<>(fields.length);
		Collections.addAll(resultList,fields);
		return  resultList;

	}



	public static void main(String[] a) {
		ChildPo childPo = new ChildPo();
		childPo.setName("Name");
		childPo.setChildName("ChildName");

		GrandsonPo grandsonPo = new GrandsonPo();
		grandsonPo.setName("Name");
		grandsonPo.setChildName("ChildName");
		grandsonPo.setGrandSonName("GrandSonName");
		Field[] fields = grandsonPo.getClass().getDeclaredFields();
		List<Field> allField = new ArrayList();

		allField = getParentField(grandsonPo.getClass(),allField);




		for (Field field:allField ) {

			if(field.isAnnotationPresent(Column.class) ){

				String dbName = field.getName();
				Column column = field.getAnnotation(Column.class);
				if(!StringUtil.isEmpty(column.name())){
					dbName = column.name();
				}
				ReflectionUtils.makeAccessible(field);
				Object value = ReflectionUtils.getField(field,grandsonPo);
				if ( value == null && !column.allowNullUpdate()){
					continue;
				}
				System.out.println(dbName + "=" + value);
			}
		}
	}



}



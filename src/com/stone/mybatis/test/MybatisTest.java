package com.stone.mybatis.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.stone.mybatis.bean.Employee;
import com.stone.mybatis.bean.EmployeeExample;
import com.stone.mybatis.bean.EmployeeExample.Criteria;
import com.stone.mybatis.dao.EmployeeMapper;

/**
 * 1、接口式编程 原生： Dao ===> DaoImpl mybatis Mapper ===> xxxMapper.xml
 * 
 * 2、SqlSession代表和数据库的一次会话，用完必须关闭。
 * 3、SqlSession和Connection一样都是非线程安全，每次使用都应该去获取新的对象
 * 4、mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象（将接口和XML进行绑定） EmployeeMapper mapper =
 * openSession.getMapper(EmployeeMapper.class); 5、两个重要的配置文件：
 * mybatis的全局配置文件：包含数据库的连接池信息，事物管理器信息等系统运行环境信息 SQL映射文件保存了每一个SQL语句的映射信息。
 * 
 * @author lei.shi445
 *
 */
public class MybatisTest {

	public SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}

	@Test
	public void test01() throws Exception{
		   List<String> warnings = new ArrayList<String>();
		   boolean overwrite = true;
		   File configFile = new File("generatorConfig.xml");
		   ConfigurationParser cp = new ConfigurationParser(warnings);
		   Configuration config = cp.parseConfiguration(configFile);
		   DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		   MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		   myBatisGenerator.generate(null);
	}
	
	@Test
	public void testMyBatis3Simple() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			List<Employee> selectAll = mapper.selectByExample(null);
			for (Employee employee : selectAll) {
				System.out.println(employee.getId());
			}
		} finally {
			openSession.close();
		}
	}
	
	
	@Test
	public void testMyBatis3() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			//xxxExample就是封装查询条件
			//1.查询所有
			List<Employee> emps = mapper.selectByExample(null);
			for (Employee employee : emps) {
				System.out.println(employee.getId());
			}
			//2.查询员工名字中有e字母的，和员工性别是1的,或者email中有字母e
			EmployeeExample employeeExample = new EmployeeExample();
			//创建一个Criteria，这个Criteria就是拼装查询条件的
			Criteria criteria = employeeExample.createCriteria();
			criteria.andLastNameLike("%e%");
			criteria.andGenderEqualTo("1");
			Criteria criteria2 = employeeExample.createCriteria();
			criteria2.andEmailLike("%e%");
			employeeExample.or(criteria2);
			List<Employee> selectByExample = mapper.selectByExample(employeeExample);
			for (Employee employee : selectByExample) {
				System.out.println(employee.getId());
			}
		} finally {
			openSession.close();
		}
	}
	
}

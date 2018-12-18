package com.stone.mybatis.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.stone.mybatis.bean.Employee;
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

	/**
	 * 两级缓存：
	 * 一级缓存（本地缓存）：SqlSession级别的缓存，一级缓存一直开启的。SqlSession级别的一个map
	 *   与数据库同一次会话期间查询到的数据会放在本地缓存中。
	 *   以后如果需要获取相同的数据，直接从缓存中获取，没必要再去查询数据库。
	 *   一级缓存失效情况（没有使用到当前的一级缓存情况，效果就是还需要再向数据库发出查询）
	 *   	1、SqlSession不同。
	 *   	2、SqlSession相同，查询条件不同。
	 *   	3、SqlSession相同，两次查询之间执行了增删改。
	 *   	4、SqlSession相同，手动清除了一级缓存
	 *   	
	 *   
	 * 二级缓存（全局缓存）：基于namespace级别的缓存，一个namespace对应一个二级缓存。
	 * 工作机制：
	 * 	1、一个会话，查询一条数据，这个数据就会被放在当前会话的一级缓存中。
	 * 	2、如果会话关闭，一级缓存中的数据会被保存到二级缓存中。新的会话查询信息就可以参照二级缓存。
	 * 	3、不同namespace查询的数据会放在自己对应的缓存中（map）
	 * 	4、数据会从二级缓存中获取
	 * 	5、查出的数据都会被默认先放在一级缓存中，只有会话提交或者关闭后，一级缓存中的数据才会转移到二级缓存中
	 * 
	 * 使用步骤：
	 * 	1、开启全局二级缓存配置：<setting name="cacheEnabled" value="true"/>
	 * 	2、去mapper.xml中配置使用二级缓存:<cache></cache>
	 * 	3、POJO需要实现序列化接口
	 * 
	 * 和缓存有关的设置/属性：
	 * 	1、cacheEnabled=true。如果为false，则关闭二级缓存，一致缓存不受影响
	 * 	2、每个select标签都有useCache="true"属性，如果为false，则不使用二级缓存，一级缓存不受影响
	 * 	3、每个增删改标签都有flushCache="true"属性，表示增删改执行完成后就会清空一级缓存和二级缓存
	 * 	4、每个select标签都有flushCache="false"，如果为true，每次查询之后都会清空缓存
	 *	5、openSession.clearCache()只是清除当前session的一级缓存，二级缓存不受影响
	 *	6、localCacheScope：本地缓存作用域，默认为session，当前会话的所有数据保存在会话缓存中。如果设置为statement，则禁用一级缓存
	 * 
	 * 第三方缓存整合：
	 * 	1、导入第三方缓存包
	 * 	2、导入第三方缓存整合适配包
	 * 	3、mapper。xml中使用自定义缓存<cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
	 * 	
	 * @throws IOException 
	 */
	@Test
	public void testFirstLevelCache() throws IOException {
		SqlSession openSession = getSqlSessionFactory().openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			Employee employee = mapper.getEmpById(1);
			System.out.println(employee);
			//openSession.clearCache();
			Employee employee2 = mapper.getEmpById(1);
			System.out.println(employee2);
			System.out.println(employee==employee2);
		} finally {
			openSession.close();
		}
	}
	
	@Test
	public void testSecondLevelCache() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		SqlSession openSession2 = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			EmployeeMapper mapper2 = openSession2.getMapper(EmployeeMapper.class);
			
			Employee employee = mapper.getEmpById(1);
			System.out.println(employee);
			openSession.close();
			
			//第二次查询是从二级缓存中拿到的数据，并没有发送新的SQL
			Employee employee2 = mapper2.getEmpById(1);
			System.out.println(employee2);
			openSession2.close();
			
		} finally {
			
		}
	}
	
	
}

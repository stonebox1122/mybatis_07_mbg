package com.stone.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.stone.mybatis.bean.Employee;

public interface EmployeeMapper {
	
	//返回一条记录的map，key就是列名，值就是对应的值
	public Map<String, Object> getEmpByIdReturnMap(Integer id);
	
	//多条记录封装一个map：Map<Integer, Employee>：键是这条记录的主键，值是记录封装后的javabean
	//告诉Mybatis封装这个map的时候使用哪个属性作为map的key
	@MapKey("id")
	public Map<Integer, Employee> getEmpByLastNameLikeReturnMap(String lastName);
	
	public List<Employee> getEmpsByLastNameLike(String lastName);
	
	public Employee getEmpByMap(Map<String, Object> map);
	
	public Employee getEmpByIdAndLastName(@Param("id")Integer id, @Param("lastName")String LastName);
	
	public Employee getEmpById(Integer id);
	
	public void insertEmp(Employee employee);
	
	public boolean updateEmp(Employee employee);
	
	public void deleteEmpById(Integer id);
}
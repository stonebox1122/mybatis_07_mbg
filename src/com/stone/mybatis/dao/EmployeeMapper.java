package com.stone.mybatis.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.stone.mybatis.bean.Employee;

public interface EmployeeMapper {
	
	public Employee getEmpByMap(Map<String, Object> map);
	
	public Employee getEmpByIdAndLastName(@Param("id")Integer id, @Param("lastName")String LastName);
	
	public Employee getEmpById(Integer id);
	
	public void insertEmp(Employee employee);
	
	public boolean updateEmp(Employee employee);
	
	public void deleteEmpById(Integer id);
}
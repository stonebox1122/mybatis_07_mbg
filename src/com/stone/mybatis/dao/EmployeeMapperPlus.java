package com.stone.mybatis.dao;

import com.stone.mybatis.bean.Employee;

public interface EmployeeMapperPlus {
	
	public Employee getEmpById(Integer id);
	
	public Employee getEmpAndDept(Integer id);
	
}
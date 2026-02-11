package com.apex.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Employee {
	
	@Value("1")
	private int empID ;
	
	@Value("Shivanshu")
	private String empName ;
	
	@Value("Ethical Hacker")
	private String department;
	
	public int getEmpID() {
		return empID;
	}
	public void setEmpID(int empID) {
		this.empID = empID;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public void display() {
		System.out.println("Employee ID --> " + empID);
		System.out.println("Employee Name --> " + empName);
		System.out.println("Employee Department --> " + department);

	}

}

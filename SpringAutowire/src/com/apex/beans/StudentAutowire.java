package com.apex.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class StudentAutowire {
	
	private String name;
	private int rollNo;
	
	@Autowired
	@Qualifier("Addr1")  // @Qualifier("createAddrObj2()")
	private Address address;
	
	@Autowired
	private Subject subject;
	
	public void setName(String name) {
		this.name = name;
	}
	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		return "Student [name=" + name + ", rollNo=" + rollNo + ", address=" + address + ", subject=" + subject + "]";
	}
	
	public void show() {
		System.out.println("Name : " + name);
		System.out.println("Roll No : " + rollNo);
		System.out.println("Address : " + address);
		System.out.println("Subject : " + subject);


	}

}

package com.apex.beans;

public class Student {

	private int rollNo;
	private String name;
	private String grade;
	
	public int getRollNo() {
		return rollNo;
	}
	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public void display() {
		System.out.println("Roll No --> "+ rollNo);
		System.out.println("Name --> "+ name);
		System.out.println("Grade --> "+ grade);
	}
}

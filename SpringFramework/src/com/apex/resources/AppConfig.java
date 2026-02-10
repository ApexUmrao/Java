package com.apex.resources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.apex.beans.Student;

@Configuration
public class AppConfig {
	
	@Bean("stud3")
	public Student stdObj1() {
		Student std = new Student();
		std.setRollNo(3);
		std.setName("Ankit");
		std.setGrade("AA+");
		return std;
	}
	
	@Bean
	public Student stud4() {
		Student std = new Student();
		std.setRollNo(4);
		std.setName("Aman");
		std.setGrade("AA-");
		return std;
	}
}

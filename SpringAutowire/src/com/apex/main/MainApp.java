package com.apex.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.apex.beans.Student;

import resources.JavaConfigAW;

public class MainApp {
	
	public static void main(String [] args) {

		System.out.println("AutoWiring With XML Config");
		String path = "/resources/XMLConfig.xml";
		ApplicationContext ac = new ClassPathXmlApplicationContext(path);
		Student s1 = (Student) ac.getBean("studentObj");
		s1.show();
		
		System.out.println("-----------------------------------------");

		System.out.println("AutoWiring With Java Config");
		ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigAW.class);
		Student s2 = (Student) context.getBean("stud");
		s2.show();
	}

}

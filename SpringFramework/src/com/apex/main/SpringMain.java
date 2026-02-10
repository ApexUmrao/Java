package com.apex.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.apex.beans.Student;

public class SpringMain {

	public static void main(String[] args) {
		try {
			
			ApplicationContext config = new ClassPathXmlApplicationContext("/com/apex/resources/configuration.xml");
			Student s1 = (Student) config.getBean("stud1");
			s1.display();
			System.out.println("---------------------------------");
			Student s2  = (Student) config.getBean("stud2");
			s2.display();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

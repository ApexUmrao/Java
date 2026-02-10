package com.apex.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.apex.beans.Student;
import com.apex.resources.AppConfig;

public class SpringMain {

	public static void main(String[] args) {
		try {
			
			String xmlLocation = "/com/apex/resources/configuration.xml";
			ApplicationContext config = new ClassPathXmlApplicationContext(xmlLocation);
			System.out.println("----- Using XML Config -------");
			Student s1 = (Student) config.getBean("stud1");
			s1.display();
			System.out.println("---------------------------------");
			Student s2  = (Student) config.getBean("stud2");
			s2.display();
			System.out.println("---------------------------------");

			System.out.println("----- Using Java Config -------");
			ApplicationContext configjava = new AnnotationConfigApplicationContext(AppConfig.class);
			Student s3 =  (Student) configjava.getBean("stud3");
			s3.display();
			System.out.println("---------------------------------");
			Student s4 =  (Student) configjava.getBean("stud4");
			s4.display();
			System.out.println("---------------------------------");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

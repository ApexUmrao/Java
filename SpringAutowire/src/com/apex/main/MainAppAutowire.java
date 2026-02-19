package com.apex.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.apex.beans.StudentAutowire;

import resources.JavaConfigAW;

public class MainAppAutowire {
	
	public static void main(String [] args) {

		System.out.println("AutoWiring With XML Config");
		String path = "/resources/XMLConfig.xml";
		ApplicationContext ac = new ClassPathXmlApplicationContext(path);
		StudentAutowire s1 = (StudentAutowire) ac.getBean("studentObj");
		s1.show();
		
		System.out.println("-----------------------------------------");

		System.out.println("AutoWiring With Java Config");
		ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigAW.class);
		StudentAutowire s2 = (StudentAutowire) context.getBean("stud");
		s2.show();
	}

}

package com.apex.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.apex.beans.Car;

import resources.JavaDIConfig;

public class MainApp {

	public static void main(String[] args) {
		try {
			
			String path = "/resources/XmlDIConfig.xml";

			System.out.println("-----------------------------------------------");
			System.out.println(" Showing Setter Dependency Injection Through XML ");
			ApplicationContext ac = new ClassPathXmlApplicationContext(path);
			Car obj = (Car) ac.getBean("carObj");
			obj.show();
			
			System.out.println("-----------------------------------------------");
			System.out.println(" Showing Setter Dependency Injection Through Java ");
			ApplicationContext cont = new AnnotationConfigApplicationContext(JavaDIConfig.class);
			Car obj2 = (Car) cont.getBean("carBean");
			obj2.show();
			
			System.out.println("-----------------------------------------------");

			System.out.println("-----------------------------------------------");
			System.out.println(" Showing Constructor Dependency Injection Through XML ");
			ApplicationContext app = new ClassPathXmlApplicationContext(path);
			Car obj3 = (Car) app.getBean("carConstruct");
			obj3.show();
			
			System.out.println("-----------------------------------------------");
			System.out.println(" Showing Constructor Dependency Injection Through Java ");
			ApplicationContext con = new AnnotationConfigApplicationContext(JavaDIConfig.class);
			Car obj4 = (Car) con.getBean("carCon");
			obj4.show();

		}catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}

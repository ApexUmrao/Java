package resources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.apex.beans.Car;
import com.apex.beans.Engine;

@Configuration
public class JavaDIConfig {
	
	@Bean
	public Engine e1() {
		Engine obj = new Engine();
		obj.setStrokes(4);
		obj.setHorsepower(1100);
		obj.setEnginetype("Diesel");
		return obj ;
	}
	
	@Bean("carBean")
	public Car c1() {
		Car obj = new Car();
		obj.setSeater(7);
		obj.setColour("Black");
		obj.setEngine(e1());
		return obj;
	}
	
	// Constructor Injection Logic 
	
	@Bean("engCon")
	public Engine engConstruct() {
		Engine obj = new Engine(6,2400,"EV");
		return obj;
		
	}
	@Bean("carCon")
	public Car carConstruct() {
		Car obj = new Car(6, "Red", engConstruct());
		return obj;
	}
}

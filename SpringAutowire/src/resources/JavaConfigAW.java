package resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.apex.beans.Address;
import com.apex.beans.StudentAutowire;
import com.apex.beans.Subject;

@Configuration
public class JavaConfigAW {
	
	@Bean
	public Address createAddrObj() {
		Address addr = new Address();
		addr.setHouseNo(456);
		addr.setCity("Noida");
		addr.setState("UP");
		return addr;
	}
	
	@Bean("Addr1")
	public Address createAddrObj2() {
		Address addr = new Address();
		addr.setHouseNo(353);
		addr.setCity("Delhi");
		addr.setState("Delhi");
		return addr;
	}
	
	@Bean
	public Subject createSubObj() {
		Subject sub = new Subject();
		
		List<String> listSub = new ArrayList<String>();
		listSub.add("Java");
		listSub.add("Python");
		listSub.add("DSA");
		
		sub.setSub(listSub);
		
		return sub;
	}
	
	@Bean("stud")
	public StudentAutowire createStdObj() {
		StudentAutowire std = new StudentAutowire();
		
		std.setRollNo(1);
		std.setName("Aryan");
		
		
		//Manual Deopendcy Injection -- NO Autowiring
		//std.setAddress(createAddrObj());
		
		//std.setSubject(createSubObj());
		
		
		return std;
	}

}

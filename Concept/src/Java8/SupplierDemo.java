package Java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//Supplier Represents a supplier of results (no input, only output).
//Method: T get()



public abstract class SupplierDemo implements Supplier<String>{
	
	public static void main(String[] args) {
		Supplier<String> supply = () -> "Shivanshu";
		System.out.println(supply.get());
		
		List<String> list3 = Arrays.asList("");
		System.out.println(list3.stream().findAny().orElseGet(supply));
		
		System.out.println(list3.stream().findAny().orElseGet(() -> "Anything can be printed"));
	}


}

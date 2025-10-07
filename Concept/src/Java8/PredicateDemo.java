package Java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

//Predicate Represents a boolean-valued function of one argument. -- return true/false
//Method: boolean test(T t)

public abstract class PredicateDemo implements Predicate<Integer>{
   	
	
	public static void main(String[] args) {
		Predicate<Integer> predicate = (t) -> t%2==0;
		if(predicate.test(20)) {
			System.out.println(true);
		};
		
		System.out.println(predicate.test(21));
		
		List<Integer> list2 = Arrays.asList(23,456,234,2341,5678);
		list2.stream().filter( t -> t%2==0).forEach(t -> System.out.println("Printing Even Number" + t));
		
	}
	
}

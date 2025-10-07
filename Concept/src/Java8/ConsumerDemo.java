package Java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

//Consumer accepts only one argument and has no return value.
//Method: void accept(T t)

public abstract class ConsumerDemo implements Consumer<Integer>{

	public static void main (String[] args) {
		Consumer<Integer> consume = (t) -> System.out.println(" Number is Printing : "+ t);
		consume.accept(10);
		
		List<Integer> list1 = Arrays.asList(2,3,1242,435,132);
		
		list1.stream().forEach(consume);
		
		list1.stream().forEach(t -> System.out.println(" Run Number : "+t));

	}
}

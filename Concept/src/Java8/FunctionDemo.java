package Java8;

import java.util.function.Function;

//It is a function that accepts one argument and produces a result.
//Method: R apply(T t)
// Acts like a Vending Machine -- You put in “5”, the machine does some work (5 × 5), and gives you “25” back.

public class FunctionDemo<T, R> implements Function<T, R> {
	
	public static void main(String[] args) {
		
		FunctionDemo<Integer, Integer> squareFunc = new FunctionDemo<Integer, Integer>(){
			
			@Override
			public Integer apply(Integer t) {
				return t*t;
			}
		};
		
		System.out.println(squareFunc.apply(5));
	}

	@Override
	public R apply(T t) {
		return null;
	}

	
	
	

}

package InterviewCoding;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Congizant {

    public static void main (String[] args) {

        String str = "abracadabra";
             str.chars()
                    .distinct()
                    .mapToObj(c -> String.valueOf((char) c))
                    .forEach(System.out::print);
                   // .collect(Collectors.joining());

          //  System.out.println(result);
             
             
          List<Integer> numbers = Arrays.asList(0,1, 2, 3, 4, 5, 3, 4, 4,4,5, 6, 7, 8, 3,9, 10);
          
          numbers.stream()
          		 .distinct()
          		 .filter((n -> numbers.stream()
          				 .filter(x -> x.equals(n))
          				 .count() == n))
          		 .forEach(System.out::println);
          				 
          numbers.stream()
		  		 .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		  		 .entrySet()
		  		 .stream()
	                .filter(e -> e.getKey() != null && e.getValue() == e.getKey().longValue()) // avoid nulls
		  		 .map(Map.Entry::getKey)
		  		 .forEach(System.out::println);
        }
    }


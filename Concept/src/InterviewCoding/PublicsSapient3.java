package InterviewCoding;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PublicsSapient3 {

	
	public class Employee {

	    private Long id;
	    private String name;
	    private String department;
	    private Double salary;

	    // getters setters
	}
	
	
	
//	Immutable means
//
//	Once object is created
//
//	NO modification
//
//	Rules
//
//	class final
//	fields private final
//	no setters
//	constructor
//	defensive copy
//	getter returns copy
	
	
	public final class Person {

	    private final String name;

	    private final Integer age;

	    private final List<String> addresses;

	    public Person(String name,
	                  Integer age,
	                  List<String> addresses){

	        this.name=name;

	        this.age=age;

	        this.addresses=new ArrayList<>(addresses);
	    }

	    public String getName(){

	        return name;
	    }

	    public Integer getAge(){

	        return age;
	    }

	    public List<String> getAddresses(){

	        return new ArrayList<>(addresses);
	    }
	}
	
	public static void main(String[] args) {
		
//		@RestController
//		@RequestMapping("/employees")
//		public class EmployeeController {
//
//		    @Autowired
//		    private EmployeeService service;
//
//		    @PostMapping
//		    public ResponseEntity<Employee> addEmployee(
//		            @RequestBody Employee employee){
//
//		        Employee saved = service.save(employee);
//
//		        return ResponseEntity.status(HttpStatus.CREATED)
//		                             .body(saved);
//		    }
//		}
//		
//		@GetMapping("/{id}")
//		public ResponseEntity<Employee> getEmployee(
//		        @PathVariable Long id){
//
//		    Employee employee = service.findById(id);
//
//		    return ResponseEntity.ok(employee);
//		}
		
		
		
		//Find the first non-repeating character in "Welcome to Java" using Java Streams.
		String input = "Welcome to Java W";

		Character result =
		input.replace(" ", "")
		     .chars()
		     .mapToObj(c -> Character.toLowerCase((char)c))
		     .collect(Collectors.groupingBy(
		             c -> c,
		             LinkedHashMap::new,
		             Collectors.counting()))
		     .entrySet()
		     .stream()
		     .filter(e -> e.getValue()==1)
		     .map(Map.Entry::getKey)
		     .findFirst()
		     .orElse(null);

		System.out.println(result);
		
		
		//Second Highest Salary SQL
		
//		SELECT MAX(salary)
//		FROM Employee
//		WHERE salary <
//		(
//		SELECT MAX(salary)
//		FROM Employee
//		);
		
		
//		SELECT DISTINCT salary
//		FROM Employee
//		ORDER BY salary DESC
//		LIMIT 1 OFFSET 1;
		
		
		//Merge 3 lists into a single list and remove duplicates using Java Streams.
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		List<Integer> list1 = List.of(1, 2, 3, 4, 5);
		List<Integer> list2 = List.of(4, 5, 6, 7, 8);
		List<Integer> list3 = List.of(7, 8, 9, 10);
		
		List<Integer> mergedList = Stream.of(list1, list2, list3)
		                                  .flatMap(List::stream)
		                               //   .distinct()                             ///Remove duplicates
		                               //   .sorted()                               ///Sort the list
		                                  .collect(Collectors.toList());
		
		System.out.println(mergedList);
         
	}

}

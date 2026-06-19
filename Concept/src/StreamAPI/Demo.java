package StreamAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Demo {

	public static void main(String [] agrs) {
		
		List<Employee> list = new ArrayList<>();
		
		list.add(new Employee(1, "ASD", 1234123,"IT"));
		
		List<Employee> employees = Arrays.asList(
	    new Employee(1, "Ali", 90000, "IT"),
	    new Employee(2, "Bob", 60000, "HR"),
	    new Employee(3, "Charl", 120000, "Finance"),
	    new Employee(4, "Dale", 7000, "IT"),
	    new Employee(5, "Eve", 5000, "HR")
	);
		
		//Fetching Count of Employee
		System.out.println(		employees.stream().count() );
		
//		employees.stream().forEach(System.out::println);
		
		//Filtering Employee based on salary and fetching only name & Salary
		employees.stream()
		         .filter(emp -> emp.getSalary() > 50000)
		         .map(emp -> emp.getName() + " and " + emp.getSalary() )
				 .forEach(System.out::println);
		
		//Sorting the Employee
		employees.stream()
		         .sorted(Comparator.comparing(Employee::getName))
		         .forEach(System.out::println);
		
		//Sorting Employee based salary in desc
		employees.stream()
        .sorted(Comparator.comparing(Employee::getSalary).reversed())
        .forEach(System.out::println);
		
		
		employees.stream()
				.sorted(Comparator.comparing(Employee :: getSalary)
				.thenComparing(Employee :: getName))
				.forEach(System.out::println);
		
		//employee names in uppercase (sorted)
		employees.stream()
				.map(emp -> emp.getName())
				.map(String :: toUpperCase)
				.sorted()
				.forEach(System.out::println);
		
//		int totalSalary = employees.stream()
//			    .collect(Collectors.averagingInt(Employee::getSalary));
		
		employees.stream().collect(Collectors.toList());
		
		Map <String,Integer> map =employees.stream().collect(Collectors.toMap(Employee :: getName, Employee :: getEmpID));
		System.out.println(map);
		
//		String result =employees.stream().collect(Collectors.joining("||", "<<", ">>"));

		
		
	}
}

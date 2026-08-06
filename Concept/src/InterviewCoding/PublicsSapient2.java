package InterviewCoding;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PublicsSapient2 {
	
	static class Employee {
	    private int id;
	    private String name;
	    private double salary;
	    
	    public Employee(int id, String name, double salary) {
	        this.id = id;
	        this.name = name;
	        this.salary = salary;
	    }
	    
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getSalary() {
			return salary;
		}
		public void setSalary(double salary) {
			this.salary = salary;
		}
		

	    // getters
	}
	
	public static void main(String [] a) {
		
		String str = "aaabbbbccddddeeeeee";

		int maxLen = 1;
		int currentLen = 1;

		int start = 0;
		int maxStart = 0;

		for (int i = 1; i < str.length(); i++) {

		    if (str.charAt(i) == str.charAt(i - 1)) {
		        currentLen++;
		    } else {

		        if (currentLen > maxLen) {
		            maxLen = currentLen;
		            maxStart = start;
		        }

		        start = i;
		        currentLen = 1;
		    }
		}

		// Check last sequence
		if (currentLen > maxLen) {
		    maxLen = currentLen;
		    maxStart = start;
		}

		System.out.println(str.substring(maxStart, maxStart + maxLen));
		
		System.out.println(test());
		
		
		List<Employee> employees = Arrays.asList(
			    new Employee(1, "Ali", 90000),
			    new Employee(2, "Bob", 60000),
			    new Employee(3, "Charl", 120000),
			    new Employee(4, "Dale", 7000),
			    new Employee(5, "Eve", 5000));
		
		Employee secondHighest = employees.stream()
		        .sorted(Comparator.comparing(Employee::getSalary).reversed())
		        .skip(1)
		        .findFirst()
		        .orElse(null);
		
        System.out.println(secondHighest.getName());
	
 }
	
	public static String test() {
		String s = null;

		try {
		    s = "Hello";
		    return s;
		} catch (Exception e) {
		    s = s + " return from catch block";
		    return s;
		} finally {
		    s = s + " return from finally block";
		}
	}
	
	class ParentDemo {
	    void doThis() throws FileNotFoundException {
	        // Parent method can throw a checked exception.
	    }
	}

	class ChildDemo extends ParentDemo {
		
//		 @Override
//		    void doThis() throws FileNotFoundException {   // ✅ This is valid
//		        // Child class can throw the same exception as the parent method.
//		    }
		 
		 @Override
		    void doThis() throws IllegalArgumentException {   // ✅ This is valid
		        // Child class can throw the same exception as the parent method.
		    }
		 
		 //Java only restricts checked exceptions during method overriding;
		 //unchecked exceptions can always be added or removed.

//	    @Override
//	    void doThis() throws IOException {   // ❌ Compile-time Error
//	    	It will give a compile-time error because IOException is a broader checked exception than FileNotFoundException.
//	    	In method overriding, the child class cannot throw a broader checked exception than the parent method. 
//	    	It can only throw the same exception, a subclass of it, or no checked exception.
//	    }
	}
	
	  
	
	
}
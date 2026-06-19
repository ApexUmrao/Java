package StreamAPI;

public class Employee {

	public int empID;
	public String name;
	public long salary;
	public String department;
	

	

	public Employee(int empID, String name, long salary, String department) {
		super();
		this.empID = empID;
		this.name = name;
		this.salary = salary;
		this.department = department;
	}




	public int getEmpID() {
		return empID;
	}




	public void setEmpID(int empID) {
		this.empID = empID;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public long getSalary() {
		return salary;
	}




	public void setSalary(long salary) {
		this.salary = salary;
	}




	public String getDepartment() {
		return department;
	}




	public void setDepartment(String department) {
		this.department = department;
	}




	@Override
	public String toString() {
		return "Employee [empID=" + empID + ", name=" + name + ", salary=" + salary + ", department=" + department
				+ "]";
	}
	
//	List<Employee> employees = Arrays.asList(
//		    new Employee(1, "Ali", 90000, "IT"),
//		    new Employee(2, "Bob", 60000, "HR"),
//		    new Employee(3, "Charl", 120000, "Finance"),
//		    new Employee(4, "Dale", 70000, "IT"),
//		    new Employee(5, "Eve", 50000, "HR")
//		);
	
	
}

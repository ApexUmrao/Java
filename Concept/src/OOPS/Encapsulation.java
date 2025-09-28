package OOPS;


//Encapsulation ---> Combine the properties and method into a single unit and serves Data hiding functionality
//Achieves through -- Access modifier -- Private, Protected, Public

public class Encapsulation {

    private String name;        // Private field - data hiding
    private int age;           // Private field - data hiding
    
    // Public getter method - controlled access
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
    
    // Public setter method - controlled access with validation
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    public void setAge(int age) {
        if (age>0){
            this.age = age;
        } else{
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }


    public static void main(String[] args){
        Encapsulation E1 = new Encapsulation();
        E1.setName("Shiv");
        E1.setAge(25);

        System.out.println("Name : " + E1.getName());
        System.out.println("Age : " + E1.getAge());
    }
}

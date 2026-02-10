package OOPS;

//Inheritance  -->  A concept where a class can inherit the properties and methods of another class
//Achieves through -- extend keyword + implements keyword


public class Inheritance {
    public static void main(String[] args) {
        Vehicle v = new Vehicle();
        v.start(); // Vehicle starts    vehicle is an object of Vehicle class

        Vehicle ref = new Car(); // Upcasting: Car is-a Vehicle
        ref.start(); // Car starts with a key (overridden method is called)

        //Car c1 = new Vehicle(); // Not Feasible;
        Car c1 = (Car) new Vehicle(); // Feasible as Vehicle is a parent class of Car
        c1.start();
        c1.openTrunk();
        // If you need Car-specific methods, you can downcast:
         ((Car) ref).openTrunk();
    }

}

// Base (super) class
class Vehicle {
    void start() {
        System.out.println("Vehicle starts");
    }
}

interface Dashboard {
     default void openTrunk() {

    }
}
// Derived (sub) class inheriting from Vehicle
class Car extends Vehicle implements Dashboard{
    @Override
    void start() { // Method overriding (runtime polymorphism)
        System.out.println("Car starts with a key");
    }

    @Override
    public void openTrunk() { // Subclass-specific behavior
        System.out.println("Trunk opened");
    }
}

package OOPS;

//Inheritance  -->  A concept where a class can inherit the properties and methods of another class
//Achieves through -- extend keyword


public class Inheritance {
    public static void main(String[] args) {
        Vehicle v = new Vehicle();
        v.start(); // Vehicle starts

        Vehicle ref = new Car(); // Upcasting: Car is-a Vehicle
        ref.start(); // Car starts with a key (overridden method is called)

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

// Derived (sub) class inheriting from Vehicle
class Car extends Vehicle {
    @Override
    void start() { // Method overriding (runtime polymorphism)
        System.out.println("Car starts with a key");
    }

    void openTrunk() { // Subclass-specific behavior
        System.out.println("Trunk opened");
    }
}

package OOPS;

//Polymorphism  --->  A concept where a single method can perform different operations depending on the type of object
// One Method can take different form

//Achieves through -- Overriding(runtime polymorphism) and Overloading(compile time polymorphism)

public class Polymorphism {

    
    // Compile-time polymorphism (Method Overloading)
    static int add(int a, int b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    static double add(double a, double b) {
        return a + b;
    }

    // Utility method to demonstrate runtime polymorphism via a common interface
    static void makeItSpeak(Animal animal) {
        animal.speak(); // Calls the overridden version based on actual object type
    }

    public static void main(String[] args) {
        // Demonstrate compile-time polymorphism (overloading)
        System.out.println("add(int, int): " + add(2, 3));
        System.out.println("add(int, int, int): " + add(1, 2, 3));
        System.out.println("add(double, double): " + add(2.5, 3.75));

        // Demonstrate runtime polymorphism (overriding)
        Animal a1 = new Dog();
        Animal a2 = new Cat();
        Animal a3 = new Animal();

        makeItSpeak(a1); // Dog barks
        makeItSpeak(a2); // Cat meows
        makeItSpeak(a3); // Animal speaks
    }
}

// Base type
class Animal {
    void speak() {
        System.out.println("Animal speaks");
    }
}

// Derived types overriding the behavior
class Dog extends Animal {
    @Override
    void speak() {
        System.out.println("Dog barks");
    }
}

class Cat extends Animal {
    @Override
    void speak() {
        System.out.println("Cat meows");
    }
}

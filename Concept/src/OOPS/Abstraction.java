package OOPS;

//Abstraction  -->  A concept where a class is made to hide its properties and methods
//Hides its implementation details and shows only its necessary functionality.
//Achieves through -- Abstract keyword and Interface

public class Abstraction {
    public static void main(String[] args) {
        // Interface-based abstraction
        Notifier email = new EmailNotifier();
        email.send("Welcome to the system!");

        // Abstract class-based abstraction
        Shape r = new Rectangle(4.0, 5.0);
        r.describe();
    }
}

interface Notifier {
    void send(String message);
}

class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        // Implementation detail hidden behind the Notifier contract
        System.out.println("Email sent: " + message);
    }
}

abstract class Shape {
    // Abstract method: subclasses must provide the implementation
    abstract double area();

    // Concrete method uses the abstract method, hiding how area() is computed
    void describe() {
        System.out.println(getClass().getSimpleName() + " area = " + area());
    }
}


class Rectangle extends Shape {
    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }
}

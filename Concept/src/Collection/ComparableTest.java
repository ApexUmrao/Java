package Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparableTest {


//    Java provides a Comparable interface to define a natural ordering for objects of a user-defined class.
//    By implementing the Comparable interface, a class can provide a single natural ordering that can be used to sort its instances.
//    This is particularly useful when you need a default way to compare and sort objects.

    // Single Natural Ordering: The primary limitation of Comparable is that it allows only one natural ordering for the objects of a class

    public static void main(String[] args) {
        List<Student> students = new ArrayList<Student>();

        students.add(new Student("shiv",21));
        students.add(new Student("aryan",19));
        students.add(new Student("yash",18));
        students.add(new Student("abhi",26));
        students.add(new Student("shiv",26));

        Collections.sort(students);
        for (Student student : students) {
            System.out.println(student.name +"  &   " + student.age);
        }
    }

    public static class Student implements Comparable<Student>{
        private String name;
        private int age;

        Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(Student o) {
            if (!(this.age == o.age))
                return this.age - o.age;
            else {
                return this.name.compareTo(o.name);
            }
        }
    }
}

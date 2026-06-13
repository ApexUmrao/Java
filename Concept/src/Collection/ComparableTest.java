package Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparableTest {

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

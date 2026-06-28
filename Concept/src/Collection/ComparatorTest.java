package Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorTest {
//    The Comparator interface allows you to create multiple Comparator instances, each defining a different ordering for objects.
//    This flexibility means you can sort objects by various attributes or in different orders without altering the object's class.

    public static void main(String[] args) {
        List<Student> list = new ArrayList<Student>();
        list.add(new Student("shiv",1,96));
        list.add(new Student("aryan",2,90));
        list.add(new Student("yash",3,99));
        list.add(new Student("abhi",4,100));

        Comparator<Student> c1 = new sortByName();
        Comparator<Student> c2 = new sortByMarks();
        Comparator<Student> c3 = new sortByRollNo();

        Collections.sort(list,c3);


        //Calling through Anonymous Class

//        Collections.sort(list, new Comparator<Student>() {
//            @Override
//            public int compare(Student o1, Student o2) {
//                return o1.rollNo- o2.rollNo;
//            }
//        });

        for (Student student : list) {
            System.out.println(student.name + " " + student.marks + " " + student.rollNo);
        }

    }

    static class sortByName implements Comparator<Student>{
        @Override
        public int compare(Student o1, Student o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    static class sortByRollNo implements Comparator<Student>{
        @Override
        public int compare(Student o1, Student o2) {
            return o1.rollNo- o2.rollNo;
        }
    }

    static class sortByMarks implements Comparator<Student>{
        @Override
        public int compare(Student o1, Student o2) {
            return o1.marks-o2.marks;
        }
    }

    static class Student {

        String name;
        int rollNo;
        int marks;

        public Student(String name, int rollNo, int marks) {
            this.name = name;
            this.rollNo = rollNo;
            this.marks = marks;
        }
    }

}

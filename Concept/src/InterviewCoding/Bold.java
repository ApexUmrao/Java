package InterviewCoding;

public class Bold {

    Given a string, return the first character that occurs only once.

            Example:

    Input:  "swiss"
    Output: 'w'


    String s = "swiss "; --A B
    int[] count = new int[256];--starting a -- 9897

            for ( char c: s.toCharArray()){ //ASCII S--count --1 -- w -- 1-1 //i-- 1
        count[c]++; ///count[s] --- count[]--
        //count[s] -- count stores 2
        //count[w] --count stores 1
    }

//count -- 1-- non -w ifrst --i

for(char c : s.toCharArray()){
        if (count[c] == 1){
            System.out.println("Result" + c);
        }


    }


    class Parent {
        void process() {
            System.out.println("Parent process");
        }

        static void execute() {
            System.out.println("Parent execute"); -- 2
        }
    }

    class Child extends Parent {
        @Override
        void process() {
            System.out.println("Child process"); 1,3
        }

        static void execute() {
            System.out.println("Child execute"); 4
        }
    }

    public class Test {
        public static void main(String[] args) {
            Parent obj = new Child();

            obj.process(); --
                    obj.execute();--

                    Child child = new Child();

            child.process();
            child.execute();
        }

        Employee Table: EmpId, Name, DOB, Salary, DOJ, DeptId
        Department Table: DeptId, DeptName, Description


        employee & department
    --
        select DeptName, Max(Salary) from Employee e Join
        Department d ON e.DeptId=d.DeptId
        group by d.DeptName;



//Convert recursion into a loop:

        public int RecursiveMethod(int x){
            if (x<0)
                return 0;
            return MethodA(x)+ RecursiveMethod(x-1);

        }

        public class Job{

            private static Job instance;


            private Job(){} --constructor


            public static Job getInstance(){
                if (instance == null){
                    instance = new Job();
                }

                return instance;
            }
        }

        public int RecursiveMethod(int x){
            int sum = 0;
            // if (x<0)
            //     return 0;
            for (int i = x; i>=0; i--){
                sum=sum+MethodA(x);
            }
            // return MethodA(x)+ RecursiveMethod(x-1);
            return sum;
        }




    }

}

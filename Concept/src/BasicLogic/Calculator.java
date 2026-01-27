package BasicLogic;

import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter First Numbers --->");
        int num1 = sc.nextInt();
        System.out.println("Please Enter Second Numbers --->");
        int num2 = sc.nextInt();
        System.out.println("Enter a Operation to Perform ---->");
        System.out.println(" Multiplication : * \n Division : / \n Substraction : - \n Addition : + \n Modulus : %");
        char op = sc.next().charAt(0);

        switch(op) {
            case '*' : System.out.println("Result --> "+ num1*num2);
            break;
            case '/' : System.out.println("Result --> "+ num1/num2);
            break;
            case '-' : System.out.println("Result --> "+ (num1-num2));
            break;
            case '+' : System.out.println("Result --> "+ num1+num2);
            break;
            case '%' : System.out.println("Result --> "+ num1%num2);
            break;
            default : System.out.println("Enter a valid Operation");
        }
        sc.close();
    }
}

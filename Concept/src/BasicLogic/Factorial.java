package BasicLogic;

import java.util.Scanner;

public class Factorial {
    public static void main(String[] args){
        System.out.println("Enter a number to find a factorial --->");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        int fact = 1;
        if (num < 0){
            System.out.println("Factorial of number less than 0 is not possible");
        }

        while (num>1){

            fact=fact*num;
            num--;

        }

//       for (int i = 1; i <= num; i++) {
//           fact=fact*i;
//       }

        System.out.println("Factorial of Number is --> " +fact);

    }

}

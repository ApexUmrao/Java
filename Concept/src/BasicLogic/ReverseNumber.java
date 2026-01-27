package BasicLogic;

import java.util.Scanner;

public class ReverseNumber {

    public static void main(String[] args) {

        System.out.println("Please Enter a Number --->");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        int remainder = 0;
        int reverseNo = 0;
        while(num>0){
            remainder = num%10;
            reverseNo = reverseNo*10 + remainder;
            num = num/10;
        }

        System.out.println("Reverse Number is ---> "+reverseNo);

        sc.close();

        //1234 --- 4321
        //4 --- 123
        //3 -- 43 ? -- 4x10 = 40 + 3 = 43    --- 12
        //2 ---- 432 ? --- 4x100 = 400 + 30 + 2= 432
        //1 ---- 4321 ? --- 4x1000 = 4000 + 300 + 20 + 1 = 4321

    }
}

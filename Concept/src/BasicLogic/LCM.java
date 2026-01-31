package BasicLogic;

import java.util.Scanner;

public class LCM {

	public static void main(String[] args) {
		Scanner obj = new Scanner(System.in);
		System.out.println("Please enter two numbers to get LCM -->");
		int num1 = obj.nextInt();
		int num2 = obj.nextInt();
		int big = num2;
		int small = num1;
		if(num1>num2) {
			big=num1;
			small=num2;
		}
		
		for(int i = 1; i<=small;i++) {
			if ((big*i)%small==0) {
				System.out.println("LCM is --> "+ big*i);
				break;
			}	
		}
		obj.close();

	}

}

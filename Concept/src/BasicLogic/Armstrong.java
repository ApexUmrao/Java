package BasicLogic;

import java.util.Scanner;

public class Armstrong {

	public static void main(String[] args) {
	   Armstrong A1 = new Armstrong();
	   //A1.getArmstrong();
	   Scanner sc = new Scanner(System.in);
	   System.out.println("Enter a limit to get the Armstrong Number");
	   int limit = sc.nextInt();
	  
	   for (int i=1;i<=limit;i++) {
		   double power = 0; 
			double rem = 0;
			double sum = 0;
			int temp = i;
			int original = i;
			while (temp> 0) {
				temp=temp/10;
				power++;
			}
			temp = i;
			while (temp>0) {
				rem = temp%10;
				sum = sum + Math.pow(rem, power);
				temp=temp/10;
			}
			if(original == sum) {
				System.out.println("Number is Armstrong --> " + i);
			} 
	   }
	sc.close();

	}

	public void getArmstrong() {
		Scanner obj = new Scanner(System.in);
		System.out.println("Please enter numbers -->");
		int num = obj.nextInt();
		num = Math.abs(num);   ///// Handle Negative Scenario if user enter -ve value.
		double power = 0; 
		double rem = 0;
		double sum = 0;
		int temp = num;
		int original = num;
		while (temp> 0) {
			temp=temp/10;
			power++;
		}
		while (num>0) {
			rem = num%10;
			sum = sum + Math.pow(rem, power);
			num=num/10;
		}
		if(original == sum) {
			System.out.println("Number is Armstrong");
		} else {
			System.out.println("Number is not Armstrong");

		}
		obj.close();
	}
}

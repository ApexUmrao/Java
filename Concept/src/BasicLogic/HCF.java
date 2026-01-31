package BasicLogic;

import java.util.Scanner;

public class HCF {

	public static void main(String[] args) {
		
		System.out.println("Enter two number to find the HCF");
		Scanner obj = new Scanner(System.in);
		int num1 = obj.nextInt();
		int num2 = obj.nextInt();
		int hcf =1;
		int limit = num1;
		if (num1>num2) {
			limit=num2;
		}
		
		for (int i = 1; i <= limit; i++ ) {      /// for (int i = 1; i<=num1 && i<=num2 ;i++){
			if (num1%i == 0 && num2%i == 0) {
				hcf = i;
			}
		}
	 System.out.println("HCF is --> "+ hcf);
	 obj.close();
	}

}

package BasicLogic;
import java.util.Scanner;

public class NumberOfDigit {

	public static void main(String[] args) {
		Scanner obj = new Scanner(System.in);
		System.out.println("Please enter numbers -->");
		int num = obj.nextInt();
		num = Math.abs(num);   ///// Handle Negative Scenario if user enter -ve value.
		int sum = 0;
		int count = 0;
		int rem = 0;
		
		if (num == 0) {        /// Handles Edgcase -- when number enters as 0
		    count = 1;
		}
		
		while (num > 0) {
			rem = num%10;
			count++;
			sum = sum + rem;
			num = num/10;
		}
		System.out.println("Total Number of Digit is "+ count);
		System.out.println("Total Sum of all Digit is "+ sum);
		obj.close();

	}

}

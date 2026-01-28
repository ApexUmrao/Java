package BasicLogic;

import java.util.Scanner;

public class PrimeNumber {

	public static void main(String[] args) {
		//primeNumber();
		primeNoLimit();
	}
	
	public static void primeNumber() {

		System.out.println("Enter a Number to check whether it is prime or not -->");
		Scanner sc = new Scanner (System.in);
		int num  = sc.nextInt();
		int temp = num;
		int i =2;
		boolean flag = true;  //Prime -- true and false -- composite
		if (num>1) {
//			while(num>1) {
//				num--;
//				if (temp%num == 0) {
//					flag = false;
//					break;
//				}
//				
//			}
			
			while (i<num) {
				if (num%i==0) {
					flag = false;
					break;
				}
				i++;
			}
			if (flag) {
				System.out.println("Number is Prime");
			}else {
				System.out.println("Number is Composite");
			}
		} else if(num == 1) {
			System.out.println("This number is neither prime nor composite number");
		} else {
			System.out.println("Invalid Number");
		}
		sc.close();
	
	}
	
	public static void primeNoLimit() {
		
		System.out.println("Enter a limit upto which you want prime number");
		Scanner sc = new Scanner(System.in);
		int limit = sc.nextInt();
		
		for(int i=2; i<=limit;i++) {
			boolean flag = true;
			for (int j =2; j*j<i;j++) {    ///for (int j =2; j<i/2;j++){
				if(i%j==0) {
					flag = false;
					break;
				}
			}
			if(flag) {
				System.out.println("Prime No --> "+ i);
			}
		}
	}

}

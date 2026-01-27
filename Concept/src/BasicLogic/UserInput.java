package BasicLogic;

import java.util.Scanner;

public class UserInput{
    public static void main(String[] args){
        System.out.println("Enter a  Value to show ---> ");
        Scanner obj = new Scanner(System.in);
        String value = obj.nextLine();
        String val = obj.next();
        int num = obj.nextInt();

        System.out.println("Value is ---> "+val);
        System.out.println("Value is ---> "+value);
        System.out.println("Value is ---> "+num);

        obj.close();
    }
}


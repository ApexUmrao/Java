package OOPS;

public class Variable {

    int a = 100;    //Instance variable
    static int b = 200; //Static variable
    int c; //Instance variable withput initialization

    Variable(){
        a++;
        b++;
        System.out.println(" Static Variable after increment  : "+b );
        System.out.println(" Instance Variable after increment  : "+a );
    }
    
    void display(){
        int d = 300; //Local variable
        System.out.println("Local Variable : "+d + " Instance Variable : "+a + " Static Variable : "+b);

    }

    public static void main(String[] args){
        Variable v1 = new Variable();

        System.out.println(" Instance Variable : "+v1.a);
        System.out.println(" Static Variable : "+b);
        System.out.println(" Calling display method ");

        v1.a=500;
        System.out.println(" Instance Variable after modifying : "+v1.a);

        b=1000;
        System.out.println(" Static Variable after modifying : "+b);

        v1.display();

        Variable v2 = new Variable();
        Variable v3 = new Variable();


    }
}

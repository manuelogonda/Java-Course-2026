import java.util.Scanner;

public class GrossPayCalculator {
    public static void main(String[] args){
        System.out.println("How many hours did you work!");
        Scanner scanner = new Scanner(System.in);
        int hours = scanner.nextInt();

        System.out.println("What is you hourly pay?");
        double rate = scanner.nextDouble();
        scanner.close();

        double payRate = rate * hours;
        System.out.println("Your Gross Pay is: " + payRate);
    }

}

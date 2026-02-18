package com.manu.java.learning.projects;


import java.util.Scanner;

public class SimpleCLICalculator {
  public  static void main(String[] args) {
        System.out.println("Welcome to Simple CLI Calculator");
      System.out.println("***************************");
        double num1;
        double num2;
        char operator;
        double result = 0;
        boolean validOperation = true;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter first number operand : ");
        num1 = scanner.nextDouble();

        System.out.println("Enter operator ( - , / , * , + , ^ , %)");
        operator = scanner.next().charAt(0);

        System.out.println("Enter second number operand : ");
        num2 = scanner.nextDouble();

         switch (operator){
             case '+'->
                 result = num1 + num2;
             case '-' ->
                 result = num1 - num2;
             case '*' ->
                 result = num1 * num2;
             case '/' -> {
                if (num2 == 0) {
                    System.out.println("Cannot divide by zero");
                    validOperation = false;
                } else {
                    result = num1 / num2;
                }
            }
             case '%' ->
                 result = num1 % num2;
             case '^' ->
                 result = Math.pow(num1, num2);
             default -> {
                System.out.println("Invalid Operation!");
                validOperation = false;
            }
        }
        if (validOperation) {
        System.out.println("The result is " + result);
        }

    }
}

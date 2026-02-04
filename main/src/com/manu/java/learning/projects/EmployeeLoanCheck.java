package com.manu.java.learning.projects;

import java.util.Scanner;

public class EmployeeLoanCheck {
    public static void main(String[] args) {
        String loanType = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your age : ");
        int age = sc.nextInt();

        if(age < 20){
            System.out.println("Too young for a loan at " + age + " years");
            return;
        }

        System.out.println("Enter your experience : ");
        int experience = sc.nextInt();

        System.out.println("Enter credit score : ");
        int creditScore = sc.nextInt();

        System.out.println("Enter your salary : ");
        double salary = sc.nextDouble();

        System.out.println("Enter your Employment type (permanent or contract) : ");
        String employmentType = sc.next();

        System.out.println("Do you have an existing loan (yes or no) : ");
        String hasExistingLoan = sc.next();

        if (age >= 20 && age <= 60 && hasExistingLoan.equals("no")) {
            if (employmentType.equals("permanent") && salary >= 50000) {
                if (creditScore >= 700) {
                    loanType = "premium";
                } else {
                    loanType = "standard";
                }
                    System.out.println("Your loan is approved : " + loanType + " at " + salary + " a month " + " for employment type " + employmentType);

            } else if (employmentType.equals("contract") && salary >= 70000) {
                if (experience >= 2 && creditScore >= 720) {
                    loanType = "premium";
                    System.out.println("Loan approved " + loanType + " for employment type " + employmentType);
                } else {
                    System.out.println("Contract loan rejected due to low credit score or experience");
                }
            }else{
                System.out.println("You do not meet salary requirements. ");
            }
        }else{
            if(age > 60){
                System.out.println("You are too old for a loan at " + age + " years");
            }
            else{
                System.out.println("You already has an existing loan. ");
            }
        }
}
}


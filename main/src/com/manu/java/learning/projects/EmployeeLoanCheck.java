package com.manu.java.learning.projects;

import java.sql.SQLOutput;
import java.util.Scanner;

public class EmployeeLoanCheck {
    public static void main() {
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

        if (age >= 20 && age <= 60 && hasExistingLoan == "yes") {
            if (employmentType == "permanent" && salary >= 50000) {
                if (creditScore <= 700 && hasExistingLoan != "no") {
                    loanType = "standard";
                    System.out.println("Your employment type is " + employmentType + " loan type " + loanType);
                } else if(creditScore >= 700 && hasExistingLoan != "yes"){
                    loanType = "premium";
                    System.out.println("Your employment type is " + employmentType + " loan type " + loanType);
                }else{
                    System.out.println("YOu are not eligible for the permanent employees loan");
                }

            } else if (employmentType == "contract" && salary >= 70000) {
                if (experience >= 2 && creditScore >= 720) {
                    System.out.println("Welcome you are a " + employmentType + " on " + salary + "a month" + " therefore you are eligible for contract employess loan and you are " + age + " years old");
                } else {
                    System.out.println("You are not eligible for contract a loan. ");
                }
            }else{
                System.out.println("You are not eligible for any of the loans. ");
            }
        }else{
            System.out.println("Too old for a loan at " + age + " years" + " how will you pay back");
        }
}
}


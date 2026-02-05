package com.manu.java.learning.projects;

import java.util.Scanner;

public class EmployeeLoanCheck {
    public static void main(String[] args) {
        String loanType = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your age : ");
        int age = sc.nextInt();

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

        if (age >= 20 && age <= 60) {
            if (employmentType.equals("permanent")) {
                if (creditScore >= 700) {
                    if(salary >= 50000) {
                        if (hasExistingLoan.equals("yes")) {
                            System.out.println("You have already loaned!");
                        }else{
                            loanType = "premium";
                            System.out.println("Loan Approved for loan type " + loanType);
                        }
                    }else{
                        System.out.println("Salary very low to be loaned as a permanent employee");
                    }
                } else {
                    loanType = "standard";
                }
                System.out.println("Your loan is approved : " + loanType + " at " + salary + " a month " + " for employment type " + employmentType);

            } else if (employmentType.equals("contract")) {
                if(salary >= 70000){
                    if(creditScore >= 720) {
                        if (experience >= 2) {
                            if(hasExistingLoan.equals("yes")){
                            loanType = "premium";
                                System.out.println("Loan approved : " + loanType + " at " + salary + " for employment type " + employmentType + " and has existing loan : " + hasExistingLoan);
                            }else{
                                System.out.println("You cannot be loaned as a contract employee if don't have existing loan");
                            }
                        }else{
                            System.out.println("Low experience for a contract loan");
                        }
                    }else{
                        loanType = "standard";
                        System.out.println("Loan approved : " + loanType + " at " + salary + " for employment type " + employmentType + " and has existing loan : " + hasExistingLoan);
                    }
                } else{
                    System.out.println("Salary low to be loaned as a contract employee");
                }
            }else{
                System.out.println("Invalid employment type choose between contract or permanent!. ");
            }
        }else{
            System.out.println("Too old or young for a loan age MUST be between 20 and 60.");
        }
}
}


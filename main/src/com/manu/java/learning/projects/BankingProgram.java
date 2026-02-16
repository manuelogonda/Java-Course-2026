package com.manu.java.learning.projects;

import java.util.Scanner;

public class BankingProgram {
   static Scanner sc = new Scanner(System.in);
     static double balance = 0;

    static void main(String[] args) {
       boolean isRunning = true;
       int choice;

        System.out.println("Welcome to Banking Program!");
//        option 1 show balance
//        option 2 deposit
//        option 3 withdraw
        //option 4  exit
while (isRunning) {
    System.out.println("Please enter your choice: 1 - 4");
    choice = sc.nextInt();

    switch (choice) {
        case 1 -> showBalance(balance);
        case 2 -> balance += deposit();
        case 3 -> balance -= withdraw();
        case 4 -> isRunning = false;
        default -> System.out.println("Invalid choice!");
    }
}


    }

    static void showBalance(double balance){
        System.out.println("Your balance is KESH " + balance);
    }
    static double deposit(){
        double depositAmount;
        System.out.println("Enter deposit amount : ");
        depositAmount = sc.nextDouble();
        double amountAfterDeposit = depositAmount + balance;

        if (depositAmount < 0){
            System.out.println("Deposit amount cannot be negative!");
            return 0;
        }else{
        return depositAmount;

        }
    }

    static double withdraw(){
        double withdrawAmount;
        System.out.println("Enter withdraw amount : ");
        withdrawAmount = sc.nextDouble();
        double amountAfterWithdraw = balance - withdrawAmount;

        if (withdrawAmount > balance){
            System.out.println("You have insufficient funds! Enter withdrawal amount less than balance!");
            return 0;
        }else if (withdrawAmount < 0){
            System.out.println("Deposit amount cannot be negative!");
            return 0;
        }else{
            return withdrawAmount;
        }
    }



}

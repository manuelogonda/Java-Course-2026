package com.manu.java.learning.projects;

import java.util.Scanner;

public class AmountWithdrawnFromAtm {
    public static void main(String[] args){
        System.out.println("Enter Amount you've withdrawn from ATM");
        Scanner sc = new Scanner(System.in);
        int myAmount = sc.nextInt();

        if(myAmount <= 0 && myAmount == 0){
            System.out.println("Enter enough amount,one cannot withdraw amount less than zero!");
        }else if(myAmount >= 0 && myAmount <= 20000){
            if(myAmount <= 5000) {
                System.out.println("Amount less than KES 5,000 → Standard Transaction");
            }else if(myAmount >= 5000 && myAmount <= 10000) {
                System.out.println("Amount between KES 5,000 and KES 10,000 → Verified Transaction");
            }else if(myAmount >= 10000) {
                System.out.println("Amount above KES 10,000 → High-Value Transaction");
            }
        }else if(myAmount > 20000){
            System.out.println("You have insufficient funds enter amount less or equal to 20000!");
        }else{
            System.out.println("Invalid input!");
        }
    }

}

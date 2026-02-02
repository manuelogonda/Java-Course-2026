package com.manu.java.learning.projects;

import java.util.Scanner;

public class DecisionMakingStatements {
    public static void main(String[] args){
//        if else statement
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your number");
        int num1 = sc.nextInt();
        if(num1 < 20){
            System.out.println("This is if statement");
        }else{
            System.out.println("This is else");
        }
//        else if
        if (num1 >= 30){
            System.out.println("The value of num1 is " + num1);
        }else if(num1 == 10){
            System.out.println("The value of num1 is " + num1);
        }else if(num1 == 40){
            System.out.println("The value of num1 is " + num1);
        }else{
            System.out.println("This is an else");
        }
    }
}

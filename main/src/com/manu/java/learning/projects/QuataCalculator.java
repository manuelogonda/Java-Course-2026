package com.manu.java.learning.projects;

import java.util.Scanner;

public class QuataCalculator {
    public static void main(String[] args){
        int quota = 10;
        System.out.println("Enter number of sales made");
        Scanner scanner = new Scanner(System.in);
        int sales = scanner.nextInt();

        scanner.close();
        if(sales >= quota){
            int salesAbove = sales - quota;
            System.out.println("Congrats you made your quota! " + salesAbove);
        }else{
            int salesShort = quota - sales;
            System.out.println("Sorry,you didn't make your quota! " + "You were " + salesShort + " sales away to achieve your quota.");
        }
    }
}

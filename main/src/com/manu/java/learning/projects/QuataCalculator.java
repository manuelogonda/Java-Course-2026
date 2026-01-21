package com.manu.java.learning.projects;

import java.util.Scanner;

public class QuataCalculator {
    public static void main(String[] args){
        int qouta = 10;
        System.out.println("Enter number of sales made");
        Scanner scanner = new Scanner(System.in);
        int sales = scanner.nextInt();

        scanner.close();
        if(sales >= qouta){
            int salesAbove = sales - qouta;
            System.out.println("Congrats you made your qouta! " + salesAbove);
        }else{
            int salesShort = qouta - sales;
            System.out.println("Sorry,you did'nt make your quota! " + "You were " + salesShort + " sales away to achieve your quota.");
        }
    }
}

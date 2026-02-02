package com.manu.java.learning.projects;

import java.util.Scanner;

public class ElectricityUnitsConsumedMonthly {
    public static void main(String[] args) {
        System.out.println("Enter your electricity units consumed this month");
        Scanner sc = new Scanner(System.in);
        int elcUnits = sc.nextInt();
        int cost = 0;
        int surcharge = 0;

        if(elcUnits <= 0){
            System.out.println("Invalid input!");

        }else if(elcUnits > 0 && elcUnits <= 50){
            cost = elcUnits * 15;
            System.out.println("Your units are : " + elcUnits);
            System.out.println("Your Electricity bill this month is " +  cost);

        }else if(elcUnits <= 50){
            cost = elcUnits * 20;
            System.out.println("Your units are : " + elcUnits);
            System.out.println("Your Electricity bill this month is "  + cost);

        }else if(elcUnits <= 150){
            cost = (50 * 15) + (elcUnits - 50) * 25;
            System.out.println("Your units are : " + elcUnits);
            System.out.println("Your Electricity bill this month is "  + cost );

        }else{
            cost = (50 * 15) + (100 * 20) + (elcUnits - 150) * 25;
            System.out.println("Your units are : " + elcUnits);
            System.out.println("Your Electricity bill this month is " + cost);
        }
         if(cost >= 10000){
            surcharge = (int) (cost * 0.05);
            cost += surcharge;
            System.out.println("Your surcharge is " + surcharge);
            System.out.println("Your Electricity total bill this month is " + cost);

        }
    }
}

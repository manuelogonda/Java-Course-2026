package com.manu.java.learning.projects;

import java.util.Scanner;

public class DistanceCoveredInKm {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the distance in km");
        int km = sc.nextInt();

        if(km <= 0){
            System.out.println("Your distance is invalid. Distance must be greater than zero!");
        }

        System.out.println("Enter time day or night");
        String time = sc.next().toLowerCase();


        System.out.println("Enter membership true or false");
        boolean membership = sc.nextBoolean();

        double baseFare = 100;
        double distanceCharge = km * 40;
        double fare = baseFare + distanceCharge;

        if(time == "night"){
            double nightSurcharge = fare * 0.20;
            fare += nightSurcharge;
            System.out.println("Your distance travelled is " + km);
            System.out.println("Your fare is: " + fare + " and night surcharge is " + nightSurcharge);
        }else if(time == "day"){
            System.out.println("Your fare is " + fare + " no night surcharge");
        }

        if(membership == true){
            double discount = fare * 0.10;
            fare -= discount;
            System.out.println("Your distance travelled is " + km);
            System.out.println("Your fare is: " + fare + " and discount is " + discount);
        }else{
            System.out.println("Your fare is " + fare + " no discount for membership applied");
        }
    }
}

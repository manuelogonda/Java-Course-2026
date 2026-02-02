package com.manu.java.learning.projects;

import java.util.Scanner;

public class StudentSystem {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Are you registered? (true or false): ");
        boolean isRegistered = input.nextBoolean();

        if (!isRegistered) {
            System.out.println("Access denied. You MUST be registered.");
            return;
        }

        System.out.print("Enter your exam score (0 - 100): ");
        int score = input.nextInt();

        if (score < 0 || score > 100) {
            System.out.println("Invalid score. Score must be between 0 and 100.");
            return;
        }

        System.out.println("Access granted.");

        if (score >= 40) {
            System.out.println("Status: Passed");
        } else {
            System.out.println("Status: Failed");
        }

        if (score >= 75 && score <= 100) {
            System.out.println("Grade: Distinction");
        } else if (score >= 60 && score <= 74) {
            System.out.println("Grade: Credit");
        } else if (score >= 50 && score <= 59) {
            System.out.println("Grade: Pass");
        } else {
            System.out.println("Grade: Fail");
        }

    }

}

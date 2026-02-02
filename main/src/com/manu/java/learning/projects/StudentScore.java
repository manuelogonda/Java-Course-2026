package com.manu.java.learning.projects;

import java.util.Scanner;

public class StudentScore {
    public static void main(String[] args){
        System.out.println("Enter student score:");
        Scanner sc = new Scanner(System.in);
        int score = sc.nextInt();
        char grade;
        if(score >= 70 && score <= 100){
            grade = 'A';
            System.out.println("Exceeds expectation, your grade is  " + grade);
        }else if(score >= 60 && score <= 69){
            grade = 'B';
            System.out.println("Meets expectation, your grade is  " + grade);
        }else if(score >= 50 && score <= 59){
            grade = 'C';
            System.out.println("Approaching expectation, your grade is  " + grade);
        } else if(score >= 40 && score <= 49){
            grade = 'D';
            System.out.println("Determining expectation, your grade is  " + grade);
        } else if(score >= 0 && score <= 39){
            grade = 'F';
            System.out.println("Below expectation, your grade is  " + grade);
        }else{
            System.out.println("Invalid input!");
        }
    }
}

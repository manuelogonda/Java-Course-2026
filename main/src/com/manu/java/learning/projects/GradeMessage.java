package com.manu.java.learning.projects;

import java.util.Scanner;

public class GradeMessage {
    public static void main(String[] args){

        System.out.println("Enter your grade");
        Scanner scanner = new Scanner(System.in);
        String grade = scanner.next();
        scanner.close();

        String message;
        switch(grade){
            case "A":
                message = "Excellent job";
                break;
            case "B":
                message = "Great job";
                break;
            case "C":
                message = "Good job";
                break;
            case "D":
                message = "Keep trying ,you can do better";
                break;
            default:
                message = "That is very low you need to lock in";
        }
        System.out.println(message);
    }

}

package com.manu.java.learning.projects;

import java.util.Scanner;

public class GradeMessageSwitchExpressons {
    public static void main(String[] args){

        System.out.println("Enter your grade");
        Scanner scanner = new Scanner(System.in);
        String grade = scanner.next();
        scanner.close();

        String message = switch(grade){
            case "A" -> "Excellent job";
            case "B" -> "Great job";
            case "C" -> "Good job";
            case "D" -> "Keep trying ,you can do better";
            default ->  "That is very low you need to lock in";
        };
        System.out.println(message);
    }

}

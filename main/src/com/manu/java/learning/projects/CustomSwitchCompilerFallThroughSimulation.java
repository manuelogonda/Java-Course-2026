package com.manu.java.learning.projects;

import java.util.Scanner;

public class CustomSwitchCompilerFallThroughSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter day of the week");
        String daysOfTheWeek = scanner.next();
        String message;
        switch (daysOfTheWeek){
            case "Monday":
                case "Tuesday":
                    case "Wednesday":
                        case "Thursday":
                            case "Friday":
//                                fall-through intentional
                                message = daysOfTheWeek + " is a Working day";
                                break;
                                case "Saturday":
                                    case "Sunday":
                                        message = daysOfTheWeek + " It's weekend ,take a rest go to church and enjoy";
                                        break;
            default:
                message = "That is an invalid day of the week!";
                break;
        }
        System.out.println(message);
    }
}

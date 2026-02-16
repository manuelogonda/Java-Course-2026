package com.manu.java.learning.projects;

import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
        static void main(String[] args) {
            Random random = new Random();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Number Guessing Game!");
            System.out.println("Please enter your random number to be guessed : ");

            int guess = 0;
            int attempts = 0;

            int randomNumber = scanner.nextInt();

            do{
                System.out.println("Please enter your guess 1-10 : ");
                guess = scanner.nextInt();
                attempts++;

                if(guess < randomNumber){
                    System.out.println("Guessed too low");
                }else if(guess > randomNumber){
                    System.out.println("Guessed too high");
                }else{
                    System.out.println("You guessed correctly! the random number was " + randomNumber );
                    System.out.println("# guesses were " + attempts);
                }

            }while(guess != randomNumber);
            System.out.println("You have won!");
            scanner.close();

        }

}

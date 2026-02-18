package com.manu.java.learning.projects;

import java.util.Random;
import java.util.Scanner;

public class RockPaperScissors {
   static Scanner scanner = new Scanner(System.in);
   static Random random = new Random();

    static void main(String[] args) {
        String[] choices = {"rock", "paper", "scissors"};
        String playerChoice;
        String computerChoice;
        String continuePlaying = "yes";

        do{
            System.out.println("Enter your move (rock, paper, scissors):)");
            playerChoice = scanner.nextLine().toLowerCase();

            if (!playerChoice.equals("rock") && !playerChoice.equals("scissors") && !playerChoice.equals("paper")) {
                continue;
            }

            computerChoice =  choices[random.nextInt(choices.length)];
            System.out.println("Computer choice is : " + computerChoice);

            if (playerChoice.equals(computerChoice)) {
                System.out.println("It's a tie!");
            }else if (playerChoice.equals("rock") && computerChoice.equals("scissors")) {
                System.out.println("You win!");
            }else if (playerChoice.equals("paper") && computerChoice.equals("rock")) {
                System.out.println("You win!");
            }
            else if (playerChoice.equals("scissors") && computerChoice.equals("paper")) {
                System.out.println("You win!");
            }
            else {
                System.out.println("You lose!");

            }

            System.out.println("Do you want to play again? (yes/no)");
            continuePlaying = scanner.nextLine().toLowerCase();

        }while(continuePlaying.equals("yes"));
        System.out.println("Thanks for playing!");
    }
}

package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class MazeRunner {

    static Scanner scanner = new Scanner(System.in);

    // ── Cell types ──
    static final char WALL     = '#';
    static final char EMPTY    = '.';
    static final char START    = 'S';
    static final char EXIT     = 'E';
    static final char PICKUP   = 'P';
    static final char TELEPORT = 'T';

    static final int START_ENERGY = 10;
    static final int PICKUP_BONUS = 3;
    static final int MOVE_COST    = 1;


    // ================================================================
    //  MAIN MENU
    // ================================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════╗");
        System.out.println("║       MAZE RUNNER GAME       ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("  # = Wall        . = Empty");
        System.out.println("  S = Start       E = Exit");
        System.out.println("  P = Pickup +3   T = Teleport");
        System.out.println("  @ = YOU");
        System.out.println();

        boolean running = true;

        while (running) {
            System.out.println("========= MAIN MENU =========");
            System.out.println("  1. Easy maze");
            System.out.println("  2. Medium maze");
            System.out.println("  3. Hard maze (teleports)");
            System.out.println("  4. How to play");
            System.out.println("  0. Exit");
            System.out.print("\nChoice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                playMaze(easyMaze(), "EASY");

            } else if (choice.equals("2")) {
                playMaze(mediumMaze(), "MEDIUM");

            } else if (choice.equals("3")) {
                playMaze(hardMaze(), "HARD");

            } else if (choice.equals("4")) {
                showHowToPlay();

            } else if (choice.equals("0")) {
                System.out.println("Thanks for playing. Goodbye!");
                running = false;

            } else {
                System.out.println("Please enter 0 to 4.");
            }
        }

        scanner.close();
    }


    // ================================================================
    //  HOW TO PLAY
    // ================================================================
    static void showHowToPlay() {
        System.out.println("\n========= HOW TO PLAY =========");
        System.out.println("You are @ on the grid.");
        System.out.println("Move using these keys:");
        System.out.println("  W = UP");
        System.out.println("  S = DOWN");
        System.out.println("  A = LEFT");
        System.out.println("  D = RIGHT");
        System.out.println();
        System.out.println("Rules:");
        System.out.println("  - Every move costs 1 energy.");
        System.out.println("  - You die if energy reaches 0.");
        System.out.println("  - Step on P to gain +" + PICKUP_BONUS + " energy (once only).");
        System.out.println("  - Step on T to teleport to the other T.");
        System.out.println("  - Reach E to escape and WIN.");
        System.out.println("  - You cannot walk through # walls.");
        System.out.println();
        System.out.println("Commands during game:");
        System.out.println("  M = show map again");
        System.out.println("  H = show move history");
        System.out.println("  Q = quit to main menu");
        System.out.println();
    }


    // ================================================================
    //  PLAY MAZE — the full game for one maze
    // ================================================================
    static void playMaze(char[][] grid, String difficulty) {

        int totalRows = grid.length;
        int totalCols = grid[0].length;

        // ── Find S, E, and all T positions ──
        int startRow = -1;
        int startCol = -1;
        int exitRow  = -1;
        int exitCol  = -1;

        // ArrayList — we don't know how many teleports there are
        ArrayList<String> teleportList = new ArrayList<>();

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                char cell = grid[row][col];
                if      (cell == START)    { startRow = row; startCol = col; }
                else if (cell == EXIT)     { exitRow  = row; exitCol  = col; }
                else if (cell == TELEPORT) { teleportList.add(row + "," + col); }
            }
        }

        // ── Build teleport map (both directions) ──
        // HashMap — instant lookup: "where does T at X,Y send me?"
        HashMap<String, String> teleportMap = new HashMap<>();
        for (int i = 0; i + 1 < teleportList.size(); i += 2) {
            String a = teleportList.get(i);
            String b = teleportList.get(i + 1);
            teleportMap.put(a, b); // A → B
            teleportMap.put(b, a); // B → A
        }

        // ── Set up the game state ──
        int    playerRow = startRow;
        int    playerCol = startCol;
        int    energy    = START_ENERGY;
        int    moves     = 0;

        // ArrayList — path grows with every step
        ArrayList<String> history = new ArrayList<>();

        // HashSet — no duplicate pickups
        HashSet<String> pickupsCollected = new HashSet<>();

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║  " + difficulty + " MAZE — GOOD LUCK!        ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Starting energy: " + energy);
        System.out.println("Find the E and escape!");
        System.out.println();

        printGrid(grid, playerRow, playerCol);
        printStatus(energy, moves);

        // ── Main game loop ──
        boolean playing = true;

        while (playing) {

            // CHECK 1 — reached the exit?
            if (playerRow == exitRow && playerCol == exitCol) {
                System.out.println("╔══════════════════════════╗");
                System.out.println("║  🎉 YOU ESCAPED! YOU WIN! ║");
                System.out.println("╚══════════════════════════╝");
                System.out.println("Moves taken : " + moves);
                System.out.println("Energy left : " + energy);
                System.out.println();
                return;
            }

            // CHECK 2 — out of energy?
            if (energy <= 0) {
                System.out.println("╔══════════════════════════╗");
                System.out.println("║   💀 YOU DIED!            ║");
                System.out.println("╚══════════════════════════╝");
                System.out.println("You ran out of energy after " + moves + " moves.");
                System.out.println();
                return;
            }

            // ── Get player input ──
            System.out.print("Move (W/A/S/D) or M=map, H=history, Q=quit: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Q")) {
                System.out.println("Returning to main menu...");
                System.out.println();
                return;
            }

            if (input.equals("M")) {
                printGrid(grid, playerRow, playerCol);
                printStatus(energy, moves);
                continue;
            }

            if (input.equals("H")) {
                printHistory(history);
                continue;
            }

            // ── Work out which direction was chosen ──
            int rowChange = 0;
            int colChange = 0;
            String directionName = "";

            if (input.equals("W")) {
                rowChange = -1; colChange = 0; directionName = "UP";
            } else if (input.equals("S")) {
                rowChange = 1;  colChange = 0; directionName = "DOWN";
            } else if (input.equals("A")) {
                rowChange = 0;  colChange = -1; directionName = "LEFT";
            } else if (input.equals("D")) {
                rowChange = 0;  colChange = 1;  directionName = "RIGHT";
            } else {
                System.out.println("Unknown key. Use W A S D to move.");
                continue;
            }

            // ── Check if the move is valid ──
            int nextRow = playerRow + rowChange;
            int nextCol = playerCol + colChange;

            // Out of bounds?
            if (nextRow < 0 || nextRow >= totalRows
                    || nextCol < 0 || nextCol >= totalCols) {
                System.out.println("You can't move there — outside the maze.");
                continue;
            }

            // Wall?
            if (grid[nextRow][nextCol] == WALL) {
                System.out.println("BUMP! There's a wall there.");
                continue;
            }

            // ── VALID MOVE — apply it ──
            energy--;
            moves++;
            playerRow = nextRow;
            playerCol = nextCol;

            String moveMessage = "Move " + moves + ": " + directionName
                    + " → [" + playerRow + "," + playerCol + "]"
                    + " | energy=" + energy;

            history.add(moveMessage);
            System.out.println();

            // ── Handle the cell we landed on ──
            char landedOn = grid[playerRow][playerCol];

            // PICKUP — only once per cell
            if (landedOn == PICKUP) {
                String key = playerRow + "," + playerCol;
                if (!pickupsCollected.contains(key)) {
                    pickupsCollected.add(key);
                    energy += PICKUP_BONUS;
                    System.out.println("  ⚡ PICKUP! +" + PICKUP_BONUS
                            + " energy | total=" + energy);
                    history.add("     PICKUP at [" + playerRow + "," + playerCol
                            + "] | energy now=" + energy);
                } else {
                    System.out.println("  (Pickup already collected here.)");
                }
            }

            // TELEPORT — jump to partner
            if (landedOn == TELEPORT) {
                String fromKey = playerRow + "," + playerCol;
                String dest    = teleportMap.get(fromKey);

                if (dest != null) {
                    String[] parts = dest.split(",");
                    int toRow = Integer.parseInt(parts[0]);
                    int toCol = Integer.parseInt(parts[1]);

                    System.out.println("  🌀 TELEPORT! ["
                            + playerRow + "," + playerCol
                            + "] → [" + toRow + "," + toCol + "]");

                    history.add("     TELEPORT to [" + toRow + "," + toCol + "]");
                    playerRow = toRow;
                    playerCol = toCol;
                }
            }

            // Show updated grid and status after every valid move
            printGrid(grid, playerRow, playerCol);
            printStatus(energy, moves);
        }
    }


    // ================================================================
    //  PRINT HELPERS
    // ================================================================
    static void printGrid(char[][] grid, int playerRow, int playerCol) {
        System.out.println();
        // Column numbers header
        System.out.print("    ");
        for (int col = 0; col < grid[0].length; col++) {
            System.out.print(col + " ");
        }
        System.out.println();
        System.out.print("   ");
        for (int col = 0; col < grid[0].length; col++) {
            System.out.print("--");
        }
        System.out.println();

        // Grid rows
        for (int row = 0; row < grid.length; row++) {
            System.out.print(row + " | ");
            for (int col = 0; col < grid[0].length; col++) {
                if (row == playerRow && col == playerCol) {
                    System.out.print("@ ");
                } else {
                    System.out.print(grid[row][col] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    static void printStatus(int energy, int moves) {
        System.out.println("  Energy: " + energy
                + "  |  Moves: " + moves);
        System.out.println();
    }

    static void printHistory(ArrayList<String> history) {
        if (history.isEmpty()) {
            System.out.println("No moves yet.");
        } else {
            System.out.println("Move history:");
            for (int i = 0; i < history.size(); i++) {
                System.out.println("  " + history.get(i));
            }
        }
        System.out.println();
    }


    // ================================================================
    //  THE THREE MAZES
    // ================================================================

    // Easy — straight path with one pickup
    static char[][] easyMaze() {
        return new char[][] {
                { 'S', '.', 'P', '.', '.' },
                { '#', '#', '.', '#', '.' },
                { '#', '#', '.', '#', '.' },
                { '#', '#', '.', '.', 'E' }
        };
    }

    // Medium — more walls, tighter path, less energy
    static char[][] mediumMaze() {
        return new char[][] {
                { 'S', '#', '#', '#', '#' },
                { '.', '.', '#', '.', '#' },
                { '#', '.', '#', '.', '#' },
                { '#', '.', '.', '.', '#' },
                { '#', '#', '#', 'P', '.' },
                { '#', '#', '#', '#', 'E' }
        };
    }

    // Hard — teleports required to reach exit
    static char[][] hardMaze() {
        return new char[][] {
                { 'S', '.', 'T', '#', 'E' },
                { '#', '#', '#', '#', '.' },
                { '#', '#', '#', '#', '.' },
                { '#', '#', '#', 'T', '.' },
                { '#', '#', '#', 'P', '.' }
        };
    }
}
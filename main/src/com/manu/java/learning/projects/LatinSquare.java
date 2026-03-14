package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.Scanner;

//  LATIN SQUARE — Puzzle Generator + Solver
public class LatinSquare {
    static Scanner scanner = new Scanner(System.in);
    static int N = 0;
    static int[][] grid = null;
    static boolean[][] rowUsed = null;
    static boolean[][] colUsed = null;

    //  MAIN MENU
    public static void main(String[] args) {
        System.out.println("LATIN SQUARE ");
        boolean running = true;

        while (running) {
            System.out.println("========= MENU =========");
            System.out.println("  1. Generate a puzzle for me to solve");
            System.out.println("  2. Generate a puzzle and show the solution");
            System.out.println("  3. Enter my own puzzle and solve it");
            System.out.println("  4. What is a Latin Square?");
            System.out.println("  0. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                pickSize();
                generateAndPlay(false);
            } else if (choice.equals("2")) {
                pickSize();
                generateAndPlay(true);
            } else if (choice.equals("3")) {
                pickSize();
                enterAndSolve();
            } else if (choice.equals("4")) {
                showExplanation();
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Enter 0 to 4.");
            }
        }
        scanner.close();
    }

    //  PICK SIZE — ask user how big the grid should be
    static void pickSize() {
        System.out.print("Grid size N (2 to 9): ");
        while (true) {
            int n = readNumber();
            if (n >= 2 && n <= 9) {
                N = n;
                System.out.println("Using a " + N + "×" + N + " grid.");
                return;
            }
            System.out.print("Please enter a number between 2 and 9: ");
        }
    }

    //  GENERATE AND PLAY
    static void generateAndPlay(boolean showSolution) {
        // Step 1 — initialize a blank board and fill it completely
        initBoard();
        boolean filled = fillGrid(0, 0);
        if (!filled) {
            System.out.println("Could not generate a grid. Try again.");
            return;
        }
        int[][] solution = copyGrid(grid);

        int cellsToRemove = (N * N) / 2;
        createPuzzle(cellsToRemove);

        System.out.println("Your puzzle  (0 = empty cell):");
        printGrid(grid);

        if (showSolution) {
            System.out.println("Solution:");
            printGrid(solution);
            return;
        }
        playMode(solution);
    }

    //  PLAY MODE — user fills in the empty cells interactively
    static void playMode(int[][] solution) {
        System.out.println("Fill in the empty cells.");
        System.out.println("Commands:");
        System.out.println("place row col number -> e.g. place 1 3 4");
        System.out.println("show -> reveal the solution");
        System.out.println("board -> reprint the board");
        System.out.println("check -> check your progress");
        System.out.println("quit -> back to menu");

        while (true) {
            System.out.print(" Command > ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("quit")) break;
            if (input.equals("board")) {
                printGrid(grid);
                continue;
            }
            if (input.equals("show")) {
                System.out.println("Solution:");
                printGrid(solution);
                break;
            }
            if (input.equals("check")) {
                checkProgress(solution);
                continue;
            }

            if (input.startsWith("place")) {
                String[] parts = input.split("\\s+");
                if (parts.length != 4) {
                    System.out.println("Format: place row col number   (e.g. place 1 3 4)");
                    continue;
                }

                try {
                    int row = Integer.parseInt(parts[1]) - 1;
                    int col = Integer.parseInt(parts[2]) - 1;
                    int num = Integer.parseInt(parts[3]);

                    if (row < 0 || row >= N || col < 0 || col >= N) {
                        System.out.println("Row and col must be between 1 and " + N);
                        continue;
                    }

                    if (num < 1 || num > N) {
                        System.out.println("Number must be between 1 and " + N);
                        continue;
                    }

                    if (solution[row][col] != 0 && grid[row][col] == solution[row][col]
                            && !isCellRemoved(row, col, solution)) {
                        System.out.println("That cell is a given clue — you cannot change it.");
                        continue;
                    }
                    placeNumber(row, col, num);
                    printGrid(grid);

                    if (isBoardComplete()) {
                        if (isBoardCorrect(solution)) {
                            System.out.println(" Correct! Well done!");
                        } else {
                            System.out.println(" Board is full but has mistakes. Type check to see.");
                        }
                        break;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Format: place row col number   (e.g. place 1 3 4)");
                }

            } else {
                System.out.println("Commands: place / show / board / check / quit");
            }
        }
    }
    //  ENTER AND SOLVE — user types their own puzzle, solver runs on it
    static void enterAndSolve() {
        initBoard();

        System.out.println("Enter your puzzle row by row.");
        System.out.println("Use 0 for empty cells.");
        System.out.println("Type numbers separated by spaces.");
        System.out.println("Example for N=4:  1 0 3 0");

        for (int row = 0; row < N; row++) {
            while (true) {
                System.out.print("Row " + (row + 1) + ": ");
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");

                if (parts.length != N) {
                    System.out.println("Need exactly " + N + " numbers. Try again.");
                    continue;
                }

                boolean valid = true;
                int[] numbers = new int[N];

                for (int i = 0; i < N; i++) {
                    try {
                        numbers[i] = Integer.parseInt(parts[i]);
                        if (numbers[i] < 0 || numbers[i] > N) {
                            System.out.println("Numbers must be 0 to " + N + ". Try again.");
                            valid = false;
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Numbers only please. Try again.");
                        valid = false;
                        break;
                    }
                }

                if (!valid) continue;

                boolean rowOk = true;
                for (int col = 0; col < N; col++) {
                    if (numbers[col] != 0) {
                        if (rowUsed[row][numbers[col]] || colUsed[col][numbers[col]]) {
                            System.out.println("Conflict detected at column " + (col + 1)
                                    + " with number " + numbers[col] + ". Try again.");
                            rowOk = false;
                            for (int c = 0; c < col; c++) {
                                if (numbers[c] != 0) removeNumber(row, c);
                            }
                            break;
                        }
                        placeNumber(row, col, numbers[col]);
                    }
                }

                if (rowOk) break;
            }
        }

        System.out.println("\nYour puzzle:");
        printGrid(grid);

        // Run the solver
        System.out.println("Running solver...");
        int[] solutionCount = {0};
        int[][] foundSolution = new int[N][N];

        solve(solutionCount, foundSolution);

        if (solutionCount[0] == 0) {
            System.out.println("Result: NO SOLUTION EXISTS for this puzzle.");
            System.out.println("Check that your puzzle has no conflicts.");

        } else if (solutionCount[0] == 1) {
            System.out.println("Result: UNIQUE SOLUTION found.");
            System.out.println("\nSolution:");
            printGrid(foundSolution);

        } else {
            System.out.println("Result: MULTIPLE SOLUTIONS exist (" + solutionCount[0] + "+ found).");
            System.out.println("The puzzle is not unique. One possible solution:");
            printGrid(foundSolution);
        }
    }
    //  FILL GRID — generates a complete valid Latin Square
    static boolean fillGrid(int row, int col) {
        if (row == N) return true;
        int nextCol = col + 1;
        int nextRow = row;
        if (nextCol == N) {
            nextCol = 0;
            nextRow = row + 1;
        }

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= N; i++) numbers.add(i);
        shuffleList(numbers);

        for (int i = 0; i < numbers.size(); i++) {
            int num = numbers.get(i);

            if (!rowUsed[row][num] && !colUsed[col][num]) {
                placeNumber(row, col, num);
                if (fillGrid(nextRow, nextCol)) {
                    return true;
                }
                removeNumber(row, col);
            }
        }
        return false;
    }
    //  SOLVE — counts how many solutions a puzzle has

    static void solve(int[] solutionCount, int[][] foundSolution) {

        if (solutionCount[0] > 1) return;
        int[] cell = findMRVCell();
        if (cell == null) {
            solutionCount[0]++;

            if (solutionCount[0] == 1) {
                for (int r = 0; r < N; r++) {
                    for (int c = 0; c < N; c++) {
                        foundSolution[r][c] = grid[r][c];
                    }
                }
            }
            return;
        }
        int row = cell[0];
        int col = cell[1];

        for (int num = 1; num <= N; num++) {
            if (solutionCount[0] > 1) return;
            if (!rowUsed[row][num] && !colUsed[col][num]) {
                placeNumber(row, col, num);
                solve(solutionCount, foundSolution);
                removeNumber(row, col);
            }
        }
    }

    //  Scans every empty cell.
    static int[] findMRVCell() {
        int   bestRow     = -1;
        int   bestCol     = -1;
        int   bestCount   = N + 1;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {

                if (grid[row][col] != 0) continue;

                int validCount = 0;
                for (int num = 1; num <= N; num++) {
                    if (!rowUsed[row][num] && !colUsed[col][num]) {
                        validCount++;
                    }
                }

                if (validCount < bestCount) {
                    bestCount = validCount;
                    bestRow   = row;
                    bestCol   = col;
                }
            }
        }

        if (bestRow == -1) return null;

        return new int[]{ bestRow, bestCol };
    }

    //  CREATE PUZZLE — removes numbers from the complete grid
    static void createPuzzle(int cellsToRemove) {
        ArrayList<int[]> cells = new ArrayList<>();
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                cells.add(new int[]{r, c});
            }
        }
        shuffleCells(cells);
        int removed = 0;
        for (int i = 0; i < cells.size() && removed < cellsToRemove; i++) {
            int row = cells.get(i)[0];
            int col = cells.get(i)[1];

            int savedNum = grid[row][col];
            removeNumber(row, col);

            int[] solutionCount = {0};
            int[][] dummy = new int[N][N];
            solve(solutionCount, dummy);

            if (solutionCount[0] == 1) {
                removed++;
            } else {
                placeNumber(row, col, savedNum);
            }
        }
        System.out.println("Removed " + removed + " numbers from the complete grid.");
    }

    //  HELPERS — place and remove numbers, keeping the boolean arrays
    static void placeNumber(int row, int col, int num) {
        grid[row][col] = num;
        rowUsed[row][num]  = true;
        colUsed[col][num] = true;
    }

    static void removeNumber(int row, int col) {
        int num   = grid[row][col];
        grid[row][col]  = 0;
        rowUsed[row][num]  = false;
        colUsed[col][num]  = false;
    }

    static void initBoard() {
        grid  = new int[N][N];
        rowUsed  = new boolean[N][N + 1];
        colUsed  = new boolean[N][N + 1];
    }

    static int[][] copyGrid(int[][] source) {
        int[][] copy = new int[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                copy[r][c] = source[r][c];
            }
        }
        return copy;
    }

    static boolean isCellRemoved(int row, int col, int[][] solution) {
        return grid[row][col] == 0 && solution[row][col] != 0;
    }

    static boolean isBoardComplete() {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (grid[r][c] == 0) return false;
            }
        }
        return true;
    }

    static boolean isBoardCorrect(int[][] solution) {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (grid[r][c] != solution[r][c]) return false;
            }
        }
        return true;
    }

    static void checkProgress(int[][] solution) {
        int correct  = 0;
        int wrong    = 0;
        int empty    = 0;

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (grid[r][c] == 0) {
                    empty++;
                } else if (grid[r][c] == solution[r][c]) {
                    correct++;
                } else {
                    wrong++;
                }
            }
        }
        System.out.println("Correct cells : " + correct);
        System.out.println("Wrong cells   : " + wrong);
        System.out.println("Empty cells   : " + empty);
        if (wrong > 0) {
            System.out.println("You have " + wrong + " mistake(s). Keep trying!");
        }
    }

    static void printGrid(int[][] g) {
        System.out.print("    ");
        for (int c = 0; c < N; c++) {
            System.out.printf(" c%d", c + 1);
        }
        System.out.print("    ");
        for (int c = 0; c < N; c++) System.out.print("---");
        System.out.println("-");

        for (int r = 0; r < N; r++) {
            System.out.printf(" r%d |", r + 1);
            for (int c = 0; c < N; c++) {
                if (g[r][c] == 0) {
                    System.out.print("  .");
                } else {
                    System.out.printf("  %d", g[r][c]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //  SHOW EXPLANATION
    static void showExplanation() {
        System.out.println("A Latin Square is an N×N grid where:");
        System.out.println("  - Every row contains the numbers 1 to N exactly once");
        System.out.println("  - Every column contains the numbers 1 to N exactly once");
        System.out.println("  - (No box rule — that is what makes it different from Sudoku)");
        System.out.println();
        System.out.println("Example 4×4 Latin Square:");
        System.out.println("   1  2  3  4");
        System.out.println("   2  1  4  3");
        System.out.println("   3  4  1  2");
        System.out.println("   4  3  2  1");
        System.out.println();
        System.out.println("The solver uses:");
        System.out.println("  Backtracking : try a number, recurse, undo if it fails");
        System.out.println("  Pruning  : only try numbers valid for that row and column");
        System.out.println("  MRV : always fill the most constrained cell first");
    }

    //  SHUFFLE HELPERS — for random generation each time
    static void shuffleList(ArrayList<Integer> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j  = (int)(Math.random() * (i + 1));
            int temp  = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    static void shuffleCells(ArrayList<int[]> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int   j    = (int)(Math.random() * (i + 1));
            int[] temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    //  INPUT HELPER — reads a positive whole number safely
    static int readNumber() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n >= 0) return n;
                System.out.print("Positive number please: ");
            } catch (NumberFormatException e) {
                System.out.print("Numbers only please: ");
            }
        }
    }
}

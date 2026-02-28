package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MazeRunnerEnergyTeleportsDetection {

    // CELL TYPES
    static final char WALL     = '#';
    static final char EMPTY    = '.';
    static final char START    = 'S';
    static final char EXIT     = 'E';
    static final char PICKUP   = 'P';
    static final char TELEPORT = 'T';

    static final int START_ENERGY = 10;
    static final int PICKUP_BONUS = 3;
    static final int MOVE_COST    = 1;

    // DIRECTIONS — index 0=RIGHT 1=DOWN 2=LEFT 3=UP
    static final int[] DIRECTION_ROW_CHANGE = {  0,  1,  0, -1 };
    static final int[] DIRECTION_COL_CHANGE = {  1,  0, -1,  0 };
    static final String[] DIRECTION_NAMES   = { "RIGHT", "DOWN", "LEFT", "UP" };

    // POSITION — a single cell on the grid
    static class Position {
        int row;
        int col;

        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Position)) return false;
            Position otherPosition = (Position) other;
            return row == otherPosition.row
                    && col == otherPosition.col;
        }

        @Override
        public int hashCode() {
            // Manual hash — works on all Java versions, no import needed
            int result = 17;
            result = 31 * result + row;
            result = 31 * result + col;
            return result;
        }

        @Override
        public String toString() {
            return "(row=" + row + ", col=" + col + ")";
        }
    }

    // STATE — snapshot of the game at one moment.
    static class State {
        Position    currentPosition;
        int         currentEnergy;
        Set<String> alreadyCollectedPickups; // FIX: was Set<Position>

        State(Position    currentPosition,
              int         currentEnergy,
              Set<String> alreadyCollectedPickups) { // FIX: was Set<Position>

            this.currentPosition         = currentPosition;
            this.currentEnergy           = currentEnergy;
            // Copy the set — snapshot must not change when original changes
            this.alreadyCollectedPickups = new HashSet<String>(alreadyCollectedPickups);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof State)) return false;
            State otherState = (State) other;
            return currentEnergy == otherState.currentEnergy
                    && currentPosition.equals(otherState.currentPosition)
                    && alreadyCollectedPickups.equals(otherState.alreadyCollectedPickups);
        }

        @Override
        public int hashCode() {
            // FIX: manual hash — no Objects.hash() needed
            int result = 17;
            result = 31 * result + currentPosition.hashCode();
            result = 31 * result + currentEnergy;
            result = 31 * result + alreadyCollectedPickups.hashCode();
            return result;
        }
    }

    // ─────────────────────────────────────────────
    // MAIN RUNNER
    // ─────────────────────────────────────────────
    public static void run(char[][] grid) {

        int totalRows    = grid.length;
        int totalColumns = grid[0].length;

        // ── Scan grid for S, E, and T positions ────
        Position       startPosition      = null;
        Position       exitPosition       = null;
        List<Position> teleportPositions  = new ArrayList<Position>();

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalColumns; col++) {

                // FIX: was grid[col][row] — wrong, causes ArrayIndexOutOfBounds
                char cell = grid[row][col];

                if      (cell == START)    startPosition = new Position(row, col);
                else if (cell == EXIT)     exitPosition  = new Position(row, col);
                else if (cell == TELEPORT) teleportPositions.add(new Position(row, col));
            }
        }

        // ── Build teleport map (both directions) ───
        Map<String, Position> teleportDestination = new HashMap<String, Position>();

        for (int i = 0; i + 1 < teleportPositions.size(); i += 2) {
            Position teleportA = teleportPositions.get(i);
            Position teleportB = teleportPositions.get(i + 1);

            // FIX: was only A→B, missing B→A so return teleport never worked
            teleportDestination.put(positionKey(teleportA), teleportB);
            teleportDestination.put(positionKey(teleportB), teleportA);
        }

        if (startPosition == null || exitPosition == null) {
            System.out.println("ERROR: Grid must contain S (start) and E (exit)");
            return;
        }

        //Initialize game state
        Position     currentPosition  = startPosition;
        int          currentEnergy    = START_ENERGY;
        List<String> pathTaken        = new ArrayList<String>();
        Set<String>  pickUpsCollected = new HashSet<String>();
        Set<State>   visitedStates    = new HashSet<State>();

        pathTaken.add("START at " + currentPosition
                + " | energy=" + currentEnergy);

        System.out.println("=== MAZE RUNNER START ===");
        System.out.println("Starting energy: " + currentEnergy);
        printGrid(grid, currentPosition);

        // Main game loop
        while (true) {

            // CHECK 1 — reached exit?
            if (currentPosition.equals(exitPosition)) {
                printResult(pathTaken,
                        "ESCAPED — reached the exit successfully!");
                return;
            }

            // CHECK 2 — out of energy?
            if (currentEnergy <= 0) {
                printResult(pathTaken,
                        "DEAD — ran out of energy at " + currentPosition);
                return;
            }

            // CHECK 3 — infinite loop?
            State snapshotOfCurrentState = new State(
                    currentPosition,
                    currentEnergy,
                    pickUpsCollected
            );

            if (visitedStates.contains(snapshotOfCurrentState)) {
                printResult(pathTaken,
                        "LOOP DETECTED!\n"
                                + "  Position   : " + currentPosition    + "\n"
                                + "  Energy     : " + currentEnergy      + "\n"
                                + "  Pickups    : " + pickUpsCollected    + "\n"
                                + "  Explanation: This exact combination of position,\n"
                                + "  energy and pickups collected was seen before.\n"
                                + "  The movement policy will repeat forever. Stopping.");
                return;
            }

            // Save snapshot — detect if we return to this state later
            visitedStates.add(snapshotOfCurrentState);

            // Try RIGHT → DOWN → LEFT → UP
            Position nextPosition        = null;
            String   chosenDirectionName = null;

            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {

                int nextRow = currentPosition.row
                        + DIRECTION_ROW_CHANGE[directionIndex];
                int nextCol = currentPosition.col
                        + DIRECTION_COL_CHANGE[directionIndex];

                // Skip if outside grid bounds
                if (nextRow < 0 || nextRow >= totalRows)    continue;
                if (nextCol < 0 || nextCol >= totalColumns) continue;

                // Skip walls
                if (grid[nextRow][nextCol] == WALL) continue;

                // Valid — take this direction
                nextPosition        = new Position(nextRow, nextCol);
                chosenDirectionName = DIRECTION_NAMES[directionIndex];
                break;
            }

            // CHECK 4 — completely stuck?
            if (nextPosition == null) {
                printResult(pathTaken,
                        "DEAD — stuck at " + currentPosition
                                + ". All directions are walls or out of bounds.");
                return;
            }

            // ── MOVE
            currentEnergy   -= MOVE_COST;
            currentPosition  = nextPosition;

            pathTaken.add("Moved " + chosenDirectionName
                    + " to "  + currentPosition
                    + " | energy=" + currentEnergy);

            System.out.println("  Moved " + chosenDirectionName
                    + " to "  + currentPosition
                    + " | energy=" + currentEnergy);

            // ── HANDLE CELL AT NEW POSITION ─────────
            char cellAtNewPosition = grid[currentPosition.row][currentPosition.col];

            // Energy pickup — only triggers the FIRST time we land here
            if (cellAtNewPosition == PICKUP) {
                String pickupKey = positionKey(currentPosition);

                if (!pickUpsCollected.contains(pickupKey)) {
                    pickUpsCollected.add(pickupKey);
                    currentEnergy += PICKUP_BONUS;

                    String pickupMessage = "  PICKUP at " + currentPosition
                            + " | +" + PICKUP_BONUS
                            + " energy | total=" + currentEnergy;

                    System.out.println(pickupMessage);
                    pathTaken.add(pickupMessage);
                }
            }

            // Teleport — instant jump to partner, no energy cost
            if (cellAtNewPosition == TELEPORT) {
                Position partnerTeleport = teleportDestination
                        .get(positionKey(currentPosition));

                if (partnerTeleport != null) {
                    String teleportMessage = "  TELEPORT from "
                            + currentPosition
                            + " to " + partnerTeleport;

                    System.out.println(teleportMessage);
                    currentPosition = partnerTeleport;
                    pathTaken.add("Teleported to " + currentPosition);
                }
            }
        }
    }

    // HELPER — print result and full path
    private static void printResult(List<String> pathTaken, String resultReason) {
        System.out.println("\n");
        System.out.println("RESULT: " + resultReason);
        System.out.println("Path taken:");
        for (int stepNumber = 0; stepNumber < pathTaken.size(); stepNumber++) {
            System.out.println("  Step " + stepNumber
                    + ": " + pathTaken.get(stepNumber));
        }
        System.out.println("Total moves: " + (pathTaken.size() - 1));
        System.out.println();
    }

    private static void printGrid(char[][] grid, Position playerPosition) {
        System.out.println();
        for (int row = 0; row < grid.length; row++) {
            System.out.print("  ");
            for (int col = 0; col < grid[0].length; col++) {
                boolean isPlayerHere = (row == playerPosition.row
                        && col == playerPosition.col);
                System.out.print(isPlayerHere ? "@ " : grid[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // HELPER — position to unique string key "row,col"
    private static String positionKey(Position position) {
        return position.row + "," + position.col;
    }

    // MAIN — three test scenarios
    public static void main(String[] args) {

        System.out.println("TEST 1 — Happy Path: ESCAPE");
        char[][] happyPathGrid = {
                {'S', '.', 'P', '.', 'E'},
                {'#', '#', '.', '#', '#'},
                {'#', '#', '.', '#', '#'}
        };
        run(happyPathGrid);

        System.out.println("TEST 2 — Loop Detection");
        char[][] loopGrid = {
                {'S', '.', '.'},
                {'#', '#', '.'},
                {'.', '.', '.'},
                {'.', '#', '#'},
                {'E', '#', '#'}
        };
        run(loopGrid);

        System.out.println("TEST 3 — Teleport + Escape");
        char[][] teleportGrid = {
                {'S', '.', 'T', '#', 'E'},
                {'#', '#', '#', '#', '.'},
                {'#', '#', '#', '#', '.'},
                {'#', '#', '#', 'T', '.'},
                {'#', '#', '#', '.', '.'}
        };
        run(teleportGrid);
    }
}
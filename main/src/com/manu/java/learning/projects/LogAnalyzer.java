package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//  ROBUST LOG ANALYZER
public class LogAnalyzer {
    static Scanner scanner = new Scanner(System.in);
    static final int WINDOW_MINUTES   = 10;
    static final int ERROR_THRESHOLD  = 3;
    static final int TOP_N_MESSAGES   = 5;
    static HashMap<String, Integer> levelCounts = new HashMap<>();
    static HashMap<String, Integer> messageCounts = new HashMap<>();
    static ArrayList<Long> errorWindow = new ArrayList<>();

    static int  totalLines  = 0;
    static int  totalErrors    = 0;
    static boolean alertFired  = false;

    //  MAIN MENU
    public static void main(String[] args) {
        System.out.println("ROBUST LOG ANALYZER ");

        boolean running = true;

        while (running) {
            System.out.println("========= MENU =========");
            System.out.println("  1. Type log lines manually");
            System.out.println("  2. Run demo  (safe — no early stop)");
            System.out.println("  3. Run demo  (triggers early stop alert)");
            System.out.println("  4. How to type a log line");
            System.out.println("  0. Exit");
            System.out.print("Enter Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                resetState();
                runManualMode();
            } else if (choice.equals("2")) {
                resetState();
                runDemo(buildSafeDemo());
            } else if (choice.equals("3")) {
                resetState();
                runDemo(buildAlertDemo());
            } else if (choice.equals("4")) {
                showFormat();
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Enter 0 to 4.");
            }
        }
        scanner.close();
    }
    //  SHOW FORMAT — explain how to type a log line
    static void showFormat() {
        System.out.println("Log line format:");
        System.out.println("HH:MM LEVEL  message text");
        System.out.println("HH:MM = time in hours and minutes e.g. 09:23");
        System.out.println("LEVEL = one of: INFO  WARN  ERROR  DEBUG");
        System.out.println("message = anything after the level");
        System.out.println("Examples : ");
        System.out.println("09:23 INFO  User logged in");
        System.out.println("09:45 ERROR Connection timeout");
        System.out.println("10:01 WARN  Disk space below 20 percent");
        System.out.println("10:05 DEBUG Retry attempt 3");
        System.out.println("Type DONE when finished entering lines.");
        System.out.println("Type STATS to see results so far without stopping.");
    }

    //  MANUAL MODE — user types lines one at a time
    static void runManualMode() {
        System.out.println("--- Manual Mode ---");
        System.out.println("Type log lines one at a time.");
        System.out.println("Format: HH:MM  LEVEL  message");
        System.out.println("Type DONE to finish. Type STATS to see current counts.");
        while (true) {
            System.out.print("Enter log > ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("DONE")) {
                break;
            }
            if (line.equalsIgnoreCase("STATS")) {
                printCurrentStats();
                continue;
            }
            if (line.isEmpty()) continue;
            boolean keepGoing = processLine(line);
            if (!keepGoing) {
                printResults();
                return;
            }
        }
        printResults();
    }
    //  DEMO MODE — runs a pre-built list of log lines automatically
    static void runDemo(ArrayList<String> lines) {
        System.out.println("--- Demo Running ---");
        System.out.println("Reading " + lines.size() + " log lines...");

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            System.out.println("  Reading: " + line);

            boolean keepGoing = processLine(line);
            if (!keepGoing) {
                printResults();
                return;
            }
        }
        printResults();
    }
    //  PROCESS LINE — the heart of the analyzer
    static boolean processLine(String line) {

        totalLines++;
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 3) {
            System.out.println("  [SKIP] Bad format (need HH:MM LEVEL message): " + line);
            return true;
        }

        String timeText = parts[0];
        String level = parts[1].toUpperCase();
        String message = parts[2];

        if (!level.equals("INFO") && !level.equals("WARN")
                && !level.equals("ERROR") && !level.equals("DEBUG")) {
            System.out.println("  [SKIP] Unknown level '" + level + "': " + line);
            return true;
        }
        long timeInMinutes = parseTimeToMinutes(timeText);
        if (timeInMinutes < 0) {
            System.out.println("  [SKIP] Bad time format '" + timeText + "': " + line);
            return true;
        }

        if (!levelCounts.containsKey(level)) {
            levelCounts.put(level, 0);
        }
        levelCounts.put(level, levelCounts.get(level) + 1);

        if (!messageCounts.containsKey(message)) {
            messageCounts.put(message, 0);
        }
        messageCounts.put(message, messageCounts.get(message) + 1);

        if (level.equals("ERROR")) {
            totalErrors++;
            errorWindow.add(timeInMinutes);
            long cutoff = timeInMinutes - WINDOW_MINUTES;
            while (!errorWindow.isEmpty() && errorWindow.get(0) < cutoff) {
                errorWindow.remove(0);
            }

            if (errorWindow.size() >= ERROR_THRESHOLD) {
                alertFired = true;
                System.out.println("ALERT — EARLY TERMINATION !");
                System.out.println("Detected " + ERROR_THRESHOLD
                        + " errors within a " + WINDOW_MINUTES + "-minute window!");
                System.out.println("Stopped at line   : " + totalLines);
                System.out.println("Current time  : " + timeText);
                System.out.println("Errors in window  : " + errorWindow.size());
                System.out.print("Window timestamps : ");
                for (int i = 0; i < errorWindow.size(); i++) {
                    System.out.print(minutesToTime(errorWindow.get(i)));
                    if (i < errorWindow.size() - 1) System.out.print(", ");
                }
                System.out.println("Last line : " + line);
                return false;
            }
        }
        return true;
    }
    //  PRINT RESULTS — called when we finish or stop early
    static void printResults() {
        System.out.println("FINAL RESULTS ");
        System.out.println("Total lines read : " + totalLines);
        System.out.println("Stopped early?   : " + (alertFired ? "YES — alert triggered" : "No — read all lines"));
        System.out.println("--- Counts by Level ---");
        String[] levels = { "INFO", "WARN", "ERROR", "DEBUG" };
        for (String lvl : levels) {
            int count = levelCounts.containsKey(lvl) ? levelCounts.get(lvl) : 0;
            System.out.println("  " + lvl   + "  : " + count);
        }
        System.out.println("  Total errors seen : " + totalErrors);
        System.out.println("--- Top " + TOP_N_MESSAGES + " Repeating Messages ---");
        printTopMessages();
        System.out.println();
    }
    //  PRINT CURRENT STATS — shows live stats without stopping
    static void printCurrentStats() {
        System.out.println("--- Stats so far (line " + totalLines + ") ---");
        String[] levels = { "INFO", "WARN", "ERROR", "DEBUG" };
        for (String lvl : levels) {
            int count = levelCounts.containsKey(lvl) ? levelCounts.get(lvl) : 0;
            System.out.println("  " + lvl + " : " + count);
        }
        System.out.println("Errors in current window : " + errorWindow.size());
        System.out.println();
    }

    //  PRINT TOP MESSAGES
    static void printTopMessages() {
        if (messageCounts.isEmpty()) {
            System.out.println("  No messages recorded.");
            return;
        }
        ArrayList<String> messageList = new ArrayList<>();
        for (String msg : messageCounts.keySet()) {
            messageList.add(msg);
        }

        // Bubble sort — sort by count, highest first
        for (int i = 0; i < messageList.size() - 1; i++) {
            for (int j = 0; j < messageList.size() - 1 - i; j++) {
                int countJ     = messageCounts.get(messageList.get(j));
                int countJNext = messageCounts.get(messageList.get(j + 1));
                if (countJ < countJNext) {
                    String temp = messageList.get(j);
                    messageList.set(j,     messageList.get(j + 1));
                    messageList.set(j + 1, temp);
                }
            }
        }

        int limit = Math.min(TOP_N_MESSAGES, messageList.size());
        for (int i = 0; i < limit; i++) {
            String msg   = messageList.get(i);
            int    count = messageCounts.get(msg);
            System.out.printf("  %2d. (%dx)  %s%n", (i + 1), count, msg);
        }
    }
    //  PARSE TIME TO MINUTES
    static long parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        if (parts.length != 2) return -1;

        try {
            long hours   = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //  MINUTES TO TIME — converts 563 back to "09:23" for display
    static String minutesToTime(long totalMinutes) {
        long hours = totalMinutes / 60;
        long mins  = totalMinutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
    //  RESET STATE — clears everything before a fresh run
    static void resetState() {
        levelCounts.clear();
        messageCounts.clear();
        errorWindow.clear();
        totalLines  = 0;
        totalErrors = 0;
        alertFired  = false;
    }


    //  DEMO 1 — safe log, no early stop triggered
    static ArrayList<String> buildSafeDemo() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("09:00 INFO  Server started");
        lines.add("09:01 INFO  User Alice logged in");
        lines.add("09:05 WARN  Memory usage at 75 percent");
        lines.add("09:10 ERROR Connection timeout");
        lines.add("09:12 INFO  User Bob logged in");
        lines.add("09:15 DEBUG Retry attempt 1");
        lines.add("09:18 INFO  User Carol logged in");
        lines.add("09:20 WARN  Disk space below 20 percent");
        lines.add("09:25 ERROR Connection timeout");
        lines.add("09:26 INFO  Backup completed");
        lines.add("09:30 INFO  User Dave logged in");
        lines.add("09:35 WARN  CPU usage spiking");
        lines.add("09:40 ERROR Connection timeout");
        lines.add("09:41 INFO  System health check passed");
        lines.add("09:45 INFO  User Eve logged in");
        lines.add("09:50 INFO  Server running normally");
        lines.add("09:52 INFO  Connection timeout");
        lines.add("09:55 INFO  Connection timeout");
        lines.add("09:58 WARN  Connection timeout");
        lines.add("10:00 INFO  Connection timeout");
        return lines;
    }


    //  DEMO 2 — triggers early stop (3 errors within 10 minutes)
    static ArrayList<String> buildAlertDemo() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("09:00 INFO  Server started");
        lines.add("09:01 INFO  User Alice logged in");
        lines.add("09:05 WARN  Memory usage at 75 percent");
        lines.add("09:10 INFO  User Bob logged in");
        lines.add("09:15 DEBUG Cache cleared");
        lines.add("09:20 INFO  User Carol logged in");
        lines.add("09:25 INFO  Scheduled job started");
        lines.add("09:30 WARN  Disk space below 20 percent");
        lines.add("09:33 ERROR Connection timeout");
        lines.add("09:34 INFO  Retrying connection");
        lines.add("09:38 ERROR Database unreachable");
        lines.add("09:39 WARN  Failover initiated");
        lines.add("09:41 ERROR Connection timeout");
        lines.add("09:42 INFO  This line should NOT be read");
        lines.add("09:43 INFO  This line should NOT be read");
        return lines;
    }
}

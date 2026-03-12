package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//  ROBUST LOG ANALYZER
public class LogAnalyzer {

    static Scanner scanner = new Scanner(System.in);
    static final int WINDOW_MINUTES  = 10;
    static final int ERROR_THRESHOLD = 3;
    static final int TOP_N_MESSAGES  = 5;

    static HashMap<String, Integer> levelCounts   = new HashMap<>();
    static HashMap<String, Integer> messageCounts = new HashMap<>();
    static ArrayList<Long> errorWindow   = new ArrayList<>();

    static int totalLines  = 0;
    static int totalErrors = 0;
    static boolean alertFired  = false;

    //  MAIN MENU
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        System.out.println("ROBUST LOG ANALYZER");
        boolean running = true;

        while (running) {
            System.out.println("*** MAIN MENU ***");
            System.out.println("1. Type log lines manually (one at a time)");
            System.out.println("2. Paste multiple log lines  (bulk input)");
            System.out.println("3. Read from a log file  (file name)");   // updated label
            System.out.println("4. Run demo  (safe — no early stop)");
            System.out.println("5. Run demo  (triggers early stop alert)");
            System.out.println("6. How to type a log line");
            System.out.println("0. Exit");
            System.out.print("Enter Choice: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                resetState();
                runManualMode();
            } else if (choice.equals("2")) {
                resetState();
                runPasteMode();
            } else if (choice.equals("3")) {
                resetState();
                runFileMode();
            } else if (choice.equals("4")) {
                resetState();
                runDemo(buildSafeDemo());
            } else if (choice.equals("5")) {
                resetState();
                runDemo(buildAlertDemo());
            } else if (choice.equals("6")) {
                showFormat();
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Enter 0 to 6.");
            }
        }
        scanner.close();
    }

    //— PASTE MODE
    static void runPasteMode() {
        System.out.println("*** Paste Mode ***");
        System.out.println("Paste your log lines below.");
        System.out.println("When done, type END on its own line and press Enter.");

        ArrayList<String> pastedLines = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("END")) break;
            if (!line.trim().isEmpty()) pastedLines.add(line.trim());
        }

        if (pastedLines.isEmpty()) {
            System.out.println("No lines were pasted. Returning to menu.");
            return;
        }

        System.out.println("Received " + pastedLines.size() + " lines. Analyzing...");

        for (int i = 0; i < pastedLines.size(); i++) {
            String line = pastedLines.get(i);
            System.out.println(" Reading: " + line);
            boolean keepGoing = processLine(line);
            if (!keepGoing) {
                printResults();
                return;
            }
        }
        printResults();
    }


    // — FILE MODE
    // Only asks for the file NAME — not the full path.
    // This works because Java looks in the project root folder by default
    // (called the working directory) and your log file sits there too.
    // So "server.log" is enough — no "C:\Users\..." needed.
    static void runFileMode() {
        System.out.println("+++ File Mode +++");
        System.out.println("Place your log file in the project root folder.");
        System.out.println("Type just the file name — no folder path needed.");
        System.out.println("Example:  server.log   or   mylog.txt");
        System.out.print("File name: ");

        // We store it as fileName to make clear it is just a name, not a path
        String fileName = scanner.nextLine().trim();

        if (fileName.isEmpty()) {
            System.out.println("No file name entered. Returning to menu.");
            return;
        }

        System.out.println("Opening: " + fileName);
        System.out.println("Reading line by line...");

        // FileReader receives just the file name — Java finds it in the
        // working directory automatically (your IntelliJ project root)
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                if (lineNumber % 100 == 0) {
                    System.out.println("  ... processed " + lineNumber + " lines so far");
                }

                boolean keepGoing = processLine(line);
                if (!keepGoing) {
                    System.out.println("File reading stopped early at line " + lineNumber);
                    printResults();
                    return;
                }
            }
            System.out.println("File fully read. Total lines in file: " + lineNumber);

        } catch (IOException e) {
            // Most likely cause: wrong file name or file not in project root
            System.out.println("[ERROR] Could not open file: " + e.getMessage());
            System.out.println("Check that:");
            System.out.println("  1. The file name is spelled correctly (including extension)");
            System.out.println("  2. The file is sitting in the project root folder");
            System.out.println("  Not sure which folder that is? Add this line to main() temporarily:");
            System.out.println("  System.out.println(System.getProperty(\"user.dir\"));");
            return;
        }

        printResults();
    }

    //  SHOW FORMAT
    static void showFormat() {
        System.out.println("Log line format:");
        System.out.println("HH:MM  LEVEL  message text");
        System.out.println();
        System.out.println("HH:MM   = time  e.g. 09:23");
        System.out.println("LEVEL   = INFO  WARN  ERROR  DEBUG");
        System.out.println("message = anything after the level");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("09:23 INFO  User logged in");
        System.out.println("09:45 ERROR Connection timeout");
        System.out.println("10:01 WARN  Disk space below 20 percent");
        System.out.println("10:05 DEBUG Retry attempt 3");
        System.out.println();
        System.out.println("Manual mode : type DONE to finish, STATS to see live counts");
        System.out.println("Paste mode  : type END on its own line when done pasting");
    }

    //  MANUAL MODE — one line at a time
    static void runManualMode() {
        System.out.println("--- Manual Mode ---");
        System.out.println("Format: HH:MM  LEVEL  message");
        System.out.println("Type DONE to finish. Type STATS for live counts.");

        while (true) {
            System.out.print("Type in your logs > ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("DONE"))  break;
            if (line.equalsIgnoreCase("STATS")) { printCurrentStats(); continue; }
            if (line.isEmpty()) continue;

            boolean keepGoing = processLine(line);
            if (!keepGoing) { printResults(); return; }
        }
        printResults();
    }

    //  DEMO MODE — pre-built lines run automatically
    static void runDemo(ArrayList<String> lines) {
        System.out.println("--- Demo Running ---");
        System.out.println("Reading " + lines.size() + " log lines...");

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            System.out.println("  Reading: " + line);
            boolean keepGoing = processLine(line);
            if (!keepGoing) { printResults(); return; }
        }
        printResults();
    }

    //  PROCESS LINE
    static boolean processLine(String line) {
        totalLines++;
        String[] parts = line.split("\\s+", 3);
        if (parts.length < 3) {
            System.out.println("SKIP Bad format: " + line);
            return true;
        }

        String timeText = parts[0];
        String level    = parts[1].toUpperCase();
        String message  = parts[2];

        if (!level.equals("INFO") && !level.equals("WARN")
                && !level.equals("ERROR") && !level.equals("DEBUG")) {
            System.out.println("  [SKIP] Unknown level '" + level + "'");
            return true;
        }

        long timeInMinutes = parseTimeToMinutes(timeText);
        if (timeInMinutes < 0) {
            System.out.println("SKIP Bad time '" + timeText + "'");
            return true;
        }

        // Job 1 — count by level
        if (!levelCounts.containsKey(level)) levelCounts.put(level, 0);
        levelCounts.put(level, levelCounts.get(level) + 1);

        // Job 2 — count message frequency
        if (!messageCounts.containsKey(message)) messageCounts.put(message, 0);
        messageCounts.put(message, messageCounts.get(message) + 1);

        // Job 3 — sliding window for errors
        if (level.equals("ERROR")) {
            totalErrors++;
            errorWindow.add(timeInMinutes);

            long cutoff = timeInMinutes - WINDOW_MINUTES;
            while (!errorWindow.isEmpty() && errorWindow.get(0) < cutoff) {
                errorWindow.remove(0);
            }

            if (errorWindow.size() >= ERROR_THRESHOLD) {
                alertFired = true;
                System.out.println("ALERT — EARLY TERMINATION!");
                System.out.println("Detected " + ERROR_THRESHOLD
                        + " errors within a " + WINDOW_MINUTES + "-minute window!");
                System.out.println("Stopped at line      : " + totalLines);
                System.out.println("Current time         : " + timeText);
                System.out.println("Errors in window     : " + errorWindow.size());
                System.out.print(  "Window timestamps    : ");
                for (int i = 0; i < errorWindow.size(); i++) {
                    System.out.print(minutesToTime(errorWindow.get(i)));
                    if (i < errorWindow.size() - 1) System.out.print(", ");
                }
                System.out.println();
                System.out.println("Triggering line      : " + line);
                return false;
            }
        }
        return true;
    }

    //  PRINT RESULTS
    static void printResults() {
        System.out.println("========= FINAL RESULTS =========");
        System.out.println("Total lines read : " + totalLines);
        System.out.println("Stopped early?   : "
                + (alertFired ? "YES — alert triggered" : "No — read all lines"));

        System.out.println("--- Counts by Level ---");
        String[] levels = { "INFO", "WARN", "ERROR", "DEBUG" };
        for (String lvl : levels) {
            int count = levelCounts.containsKey(lvl) ? levelCounts.get(lvl) : 0;
            System.out.println("  " + lvl + "  : " + count);
        }
        System.out.println("Total errors : " + totalErrors);

        System.out.println("--- Top " + TOP_N_MESSAGES + " Repeating Messages ---");
        printTopMessages();
        System.out.println();
    }

    //  PRINT CURRENT STATS — live view without stopping
    static void printCurrentStats() {
        System.out.println("--- Stats so far (line " + totalLines + ") ---");
        String[] levels = { "INFO", "WARN", "ERROR", "DEBUG" };
        for (String lvl : levels) {
            int count = levelCounts.containsKey(lvl) ? levelCounts.get(lvl) : 0;
            System.out.println("  " + lvl + " : " + count);
        }
        System.out.println("  Errors in current window : " + errorWindow.size());
        System.out.println();
    }

    //  PRINT TOP MESSAGES
    static void printTopMessages() {
        if (messageCounts.isEmpty()) {
            System.out.println("No messages recorded.");
            return;
        }
        ArrayList<String> messageList = new ArrayList<>();
        for (String msg : messageCounts.keySet()) {
            messageList.add(msg);
        }

        // Bubble sort by count — highest first
        for (int i = 0; i < messageList.size() - 1; i++) {
            for (int j = 0; j < messageList.size() - 1 - i; j++) {
                int a = messageCounts.get(messageList.get(j));
                int b = messageCounts.get(messageList.get(j + 1));
                if (a < b) {
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

    //  HELPERS
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

    static String minutesToTime(long totalMinutes) {
        long hours = totalMinutes / 60;
        long mins  = totalMinutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    static void resetState() {
        levelCounts.clear();
        messageCounts.clear();
        errorWindow.clear();
        totalLines  = 0;
        totalErrors = 0;
        alertFired  = false;
    }

    //  DEMO DATA
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

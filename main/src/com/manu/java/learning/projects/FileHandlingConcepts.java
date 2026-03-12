package com.manu.java.learning.projects;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;       // opens a file for writing
import java.io.FileReader;       // opens a file for reading
import java.io.BufferedReader;   // reads a file line by line efficiently
import java.io.PrintWriter;      // makes writing easier — has println()
import java.io.IOException;      // handles all file errors


// ====================================================================
//  FILE HANDLING — CONCEPT BY CONCEPT
//
//  Run each concept one by one from the menu.
//  Each concept does ONE thing and explains it clearly.
//
//  All files are saved in the same folder as this program.
//  The file we work with throughout is called: mylog.txt
// ====================================================================

public class FileHandlingConcepts {

    static Scanner scanner = new Scanner(System.in);

    // The file we will create, write, read, and append
    static String FILE_NAME = "mylog.txt";


    // ================================================================
    //  MAIN MENU
    // ================================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║    FILE HANDLING — CONCEPTS      ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Working file: " + FILE_NAME);

        boolean running = true;

        while (running) {
            System.out.println("\n========= MENU =========");
            System.out.println("  1. WRITE     — create a file and save text");
            System.out.println("  2. READ      — open and print the whole file");
            System.out.println("  3. APPEND    — add more text without losing existing");
            System.out.println("  4. LINE BY LINE — read and process one line at a time");
            System.out.println("  5. ERROR HANDLING — what happens when things go wrong");
            System.out.println("  0. Exit");
            System.out.print("\nChoice: ");

            String choice = scanner.nextLine().trim();

            if      (choice.equals("1")) conceptWrite();
            else if (choice.equals("2")) conceptRead();
            else if (choice.equals("3")) conceptAppend();
            else if (choice.equals("4")) conceptLineByLine();
            else if (choice.equals("5")) conceptErrorHandling();
            else if (choice.equals("0")) { System.out.println("Goodbye!"); running = false; }
            else System.out.println("Enter 0 to 5.");
        }

        scanner.close();
    }


    // ================================================================
    //  CONCEPT 1 — WRITE
    //
    //  WHAT: Create a file and save text into it.
    //
    //  CLASSES USED:
    //    FileWriter  → opens the file (creates it if it doesn't exist)
    //    PrintWriter → wraps FileWriter, gives us println() for easy writing
    //
    //  IMPORTANT:
    //    If the file already exists, FileWriter OVERWRITES it completely.
    //    The old content is gone. (Concept 3 shows how to avoid this.)
    //
    //  REAL LIFE:
    //    Every time you click Save in a text editor — this is what happens.
    //    The editor writes your content to a file on disk.
    // ================================================================
    static void conceptWrite() {

        System.out.println("\n===== CONCEPT 1: WRITE =====");
        System.out.println("We will create '" + FILE_NAME + "' and save lines to it.");
        System.out.println("Type your lines. Type END when done.\n");

        // Collect lines from the user first
        ArrayList<String> linesToWrite = new ArrayList<>();

        while (true) {
            System.out.print("  Line: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;
            if (!input.isEmpty()) linesToWrite.add(input);
        }

        if (linesToWrite.isEmpty()) {
            System.out.println("Nothing to write.");
            return;
        }

        // ── WRITE TO FILE ──────────────────────────────────────────
        //
        // try-with-resources: the writer closes automatically when done
        // FileWriter(FILE_NAME)      → open/create the file
        // new PrintWriter(writer)    → wrap it so we can use println()
        //
        // WHY wrap FileWriter in PrintWriter?
        // FileWriter only has write(String) — no automatic newlines.
        // PrintWriter adds println() which adds a newline after each line.
        // Much easier to use.

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

            for (int i = 0; i < linesToWrite.size(); i++) {
                writer.println(linesToWrite.get(i));  // writes line + newline
            }

            // When the try block ends, writer.close() is called automatically
            // close() flushes the buffer — makes sure everything is saved to disk

            System.out.println("\nDone! Wrote " + linesToWrite.size()
                    + " line(s) to '" + FILE_NAME + "'");
            System.out.println("Run option 2 to read it back.");

        } catch (IOException e) {
            // IOException fires if:
            // - the folder doesn't exist
            // - no permission to create files here
            // - disk is full
            System.out.println("[ERROR] Could not write file: " + e.getMessage());
        }

        /*
            WHAT HAPPENS ON DISK:
            Before: file does not exist (or has old content)
            After : file exists with exactly what you typed

            If you run WRITE again — old content is GONE.
            New content replaces everything.
            That is called OVERWRITE.
        */
    }


    // ================================================================
    //  CONCEPT 2 — READ
    //
    //  WHAT: Open a file and read ALL of it into memory at once,
    //        then print it.
    //
    //  CLASSES USED:
    //    FileReader     → opens the file for reading
    //    BufferedReader → wraps FileReader, gives us readLine()
    //
    //  IMPORTANT:
    //    readLine() returns one line at a time.
    //    When the file ends it returns null — that is our stop signal.
    //
    //  REAL LIFE:
    //    Every time you open a file in a text editor — this is what
    //    happens. The editor reads all lines and displays them.
    // ================================================================
    static void conceptRead() {

        System.out.println("\n===== CONCEPT 2: READ =====");
        System.out.println("Reading all content from '" + FILE_NAME + "'...\n");

        // ── READ FROM FILE ─────────────────────────────────────────
        //
        // BufferedReader reads the file in memory chunks (a buffer).
        // This is faster than reading one character at a time.
        // readLine() gives us one complete line per call.
        // When the file ends, readLine() returns null.

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            int    lineNumber = 0;

            // This loop pattern is the standard Java way to read a file:
            // (line = reader.readLine()) reads the next line AND assigns it
            // != null checks if we have reached the end of the file
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println("  Line " + lineNumber + ": " + line);
            }

            System.out.println("\nTotal lines read: " + lineNumber);

        } catch (IOException e) {
            // The most common error here:
            // FileNotFoundException — the file does not exist yet
            // Run option 1 first to create it!
            System.out.println("[ERROR] Could not read file: " + e.getMessage());
            System.out.println("Tip: Run option 1 first to create the file.");
        }

        /*
            WHAT HAPPENED:
            FileReader   → found the file on disk and opened it
            BufferedReader → loaded a chunk into memory
            readLine()   → gave us one line at a time from that chunk
            null         → told us the file was finished
            close()      → released the file (done automatically)
        */
    }


    // ================================================================
    //  CONCEPT 3 — APPEND
    //
    //  WHAT: Add new lines to an existing file WITHOUT losing
    //        what is already there.
    //
    //  THE KEY DIFFERENCE FROM WRITE:
    //    FileWriter(FILE_NAME)        → OVERWRITE (delete old content)
    //    FileWriter(FILE_NAME, true)  → APPEND    (keep old content)
    //
    //  The second argument 'true' means "append mode".
    //
    //  REAL LIFE:
    //    Log files work exactly like this.
    //    Every new event is APPENDED to the end.
    //    Old events are never deleted.
    //    The log grows over time.
    // ================================================================
    static void conceptAppend() {

        System.out.println("\n===== CONCEPT 3: APPEND =====");
        System.out.println("We will ADD lines to '" + FILE_NAME + "'");
        System.out.println("Existing content will be kept.");
        System.out.println("Type lines to append. Type END when done.\n");

        ArrayList<String> linesToAppend = new ArrayList<>();

        while (true) {
            System.out.print("  Append: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;
            if (!input.isEmpty()) linesToAppend.add(input);
        }

        if (linesToAppend.isEmpty()) {
            System.out.println("Nothing to append.");
            return;
        }

        // ── APPEND TO FILE ─────────────────────────────────────────
        //
        // Notice the ONLY difference from WRITE:
        //   new FileWriter(FILE_NAME, true)
        //                              ^^^^
        //   The 'true' here means APPEND MODE
        //   The file cursor starts at the END of existing content
        //   New lines are added after everything already there

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {

            for (int i = 0; i < linesToAppend.size(); i++) {
                writer.println(linesToAppend.get(i));
            }

            System.out.println("\nAppended " + linesToAppend.size()
                    + " line(s) to '" + FILE_NAME + "'");
            System.out.println("Run option 2 to see the full file now.");

        } catch (IOException e) {
            System.out.println("[ERROR] Could not append: " + e.getMessage());
            System.out.println("Tip: Run option 1 first to create the file.");
        }

        /*
            WRITE  vs  APPEND:

            Before file has: "Hello"

            WRITE  → FileWriter(name)       → file now has: "World"
                                               "Hello" is GONE

            APPEND → FileWriter(name, true) → file now has: "Hello"
                                                             "World"
                                               "Hello" is KEPT
        */
    }


    // ================================================================
    //  CONCEPT 4 — LINE BY LINE PROCESSING
    //
    //  WHAT: Read a file and DO SOMETHING with each line as you read.
    //        This is the key concept used in the log analyzer.
    //
    //  WHY NOT LOAD THE WHOLE FILE FIRST?
    //  A log file can have 10 million lines.
    //  Loading all 10 million into an ArrayList would crash the program.
    //  Reading one line at a time uses almost no memory —
    //  you only ever hold ONE line in memory at a time.
    //
    //  THIS IS EXACTLY WHAT THE LOG ANALYZER DOES:
    //  Read one line → parse it → update counts → check sliding window
    //  → move to next line. Never storing the whole file.
    // ================================================================
    static void conceptLineByLine() {

        System.out.println("\n===== CONCEPT 4: LINE BY LINE PROCESSING =====");
        System.out.println("Reading '" + FILE_NAME + "' one line at a time.");
        System.out.println("For each line we will: count words and find longest line.\n");

        // We track stats AS WE READ — never storing the whole file
        int    totalLines    = 0;
        int    totalWords    = 0;
        String longestLine   = "";
        int    longestLength = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = reader.readLine()) != null) {

                totalLines++;

                // Count words by splitting on spaces
                // "Hello world today" → ["Hello", "world", "today"] → 3 words
                String[] words = line.split("\\s+");
                int wordCount  = line.trim().isEmpty() ? 0 : words.length;
                totalWords    += wordCount;

                // Track the longest line
                if (line.length() > longestLength) {
                    longestLength = line.length();
                    longestLine   = line;
                }

                // Show what we found on this line
                System.out.println("  Line " + totalLines
                        + " (" + wordCount + " words): " + line);
            }

            // Print summary — built from stats collected one line at a time
            System.out.println("\n--- Summary ---");
            System.out.println("Total lines : " + totalLines);
            System.out.println("Total words : " + totalWords);
            System.out.println("Longest line: " + longestLine
                    + " (" + longestLength + " chars)");

        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
            System.out.println("Tip: Run option 1 first to create the file.");
        }

        /*
            LOG ANALYZER CONNECTION:

            The log analyzer does the same thing:
            while ((line = reader.readLine()) != null) {
                processLine(line);   ← does all 3 jobs on this one line
            }

            It never stores the whole file.
            It just updates counters and the sliding window as it reads.
            Memory stays tiny no matter how big the file is.
        */
    }


    // ================================================================
    //  CONCEPT 5 — ERROR HANDLING
    //
    //  WHAT: Understand what can go wrong with files and how to
    //        handle each case cleanly.
    //
    //  WHY THIS MATTERS:
    //  File operations ALWAYS have a chance of failure.
    //  The compiler FORCES you to handle IOException —
    //  your code will not compile without a try-catch or throws clause.
    //  This is called a CHECKED EXCEPTION.
    //
    //  COMMON FILE ERRORS:
    //  FileNotFoundException → file does not exist (reading a missing file)
    //  IOException           → parent of all file errors
    //                          covers: permission denied, disk full,
    //                          disk read error, network file unavailable
    // ================================================================
    static void conceptErrorHandling() {

        System.out.println("\n===== CONCEPT 5: ERROR HANDLING =====");
        System.out.println("We will trigger real file errors and handle them cleanly.\n");

        boolean running = true;

        while (running) {
            System.out.println("Pick an error scenario:");
            System.out.println("  1. Try to read a file that does not exist");
            System.out.println("  2. Try to write to an invalid path");
            System.out.println("  3. Show try-with-resources in action");
            System.out.println("  0. Back to main menu");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                errorScenario1();
            } else if (choice.equals("2")) {
                errorScenario2();
            } else if (choice.equals("3")) {
                errorScenario3();
            } else if (choice.equals("0")) {
                running = false;
            } else {
                System.out.println("Enter 0 to 3.");
            }
        }
    }

    // Scenario 1 — reading a file that does not exist
    static void errorScenario1() {
        System.out.println("\n-- Scenario 1: Reading a missing file --");
        System.out.println("Trying to open 'doesnotexist.txt'...\n");

        try (BufferedReader reader = new BufferedReader(
                new FileReader("doesnotexist.txt"))) {

            String line = reader.readLine();
            System.out.println(line); // we never get here

        } catch (IOException e) {
            // FileNotFoundException is a subclass of IOException
            // Catching IOException catches it too
            System.out.println("[CAUGHT] " + e.getMessage());
            System.out.println("The program did NOT crash.");
            System.out.println("We caught the error and continued safely.");
        }

        /*
            WITHOUT try-catch this would crash the whole program.
            WITH try-catch we catch it, print a friendly message,
            and the program keeps running normally.

            This is exactly what the log analyzer does in file mode:
            catch (IOException e) {
                System.out.println("Could not read file: " + e.getMessage());
                return; // go back to menu safely
            }
        */
    }

    // Scenario 2 — writing to a path that does not exist
    static void errorScenario2() {
        System.out.println("\n-- Scenario 2: Writing to invalid path --");
        System.out.println("Trying to write to '/fakefolder/fake.txt'...\n");

        try (PrintWriter writer = new PrintWriter(
                new FileWriter("/fakefolder/fake.txt"))) {

            writer.println("This will never be written");

        } catch (IOException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
            System.out.println("The folder does not exist.");
            System.out.println("The program did NOT crash.");
        }
    }

    // Scenario 3 — show try-with-resources vs manual close
    static void errorScenario3() {
        System.out.println("\n-- Scenario 3: try-with-resources --");
        System.out.println("Comparing manual close vs automatic close.\n");

        System.out.println("OLD WAY (manual close — risky):");
        System.out.println("  BufferedReader reader = new BufferedReader(new FileReader(file));");
        System.out.println("  try {");
        System.out.println("      String line = reader.readLine();");
        System.out.println("  } catch (IOException e) {");
        System.out.println("      // handle error");
        System.out.println("  } finally {");
        System.out.println("      reader.close(); // YOU must remember this");
        System.out.println("  }");
        System.out.println("  Problem: if you forget close(), the file stays");
        System.out.println("  locked and wastes memory (called a resource leak).");

        System.out.println();

        System.out.println("NEW WAY (try-with-resources — safe):");
        System.out.println("  try (BufferedReader reader = new BufferedReader(...)) {");
        System.out.println("      String line = reader.readLine();");
        System.out.println("  } catch (IOException e) {");
        System.out.println("      // handle error");
        System.out.println("  }");
        System.out.println("  close() is called AUTOMATICALLY when the block ends.");
        System.out.println("  Even if an exception is thrown. Even if you forget.");
        System.out.println("  No resource leak. Always safe.");

        System.out.println();
        System.out.println("The log analyzer uses try-with-resources everywhere.");
        System.out.println("That is why files always close cleanly after reading.");
    }
}

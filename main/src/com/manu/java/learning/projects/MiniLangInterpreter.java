package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;

// ====================================================================
//  WHAT IS THIS PROGRAM?
//
//  This is a mini programming language interpreter.
//  You type instructions one by one (like SET x 5, ADD x 3, PRINT x).
//  When you type RUN, it reads all those instructions and executes them.
//
//  Think of it like teaching the computer a tiny language,
//  then letting it "run" a program written in that language.
//
//  HOW IT RUNS — step by step:
//  1. You type instructions into a BUFFER (a list of lines)
//  2. You type RUN
//  3. LOAD scans the buffer for labels (like LOOP:) and builds a map
//  4. LOAD parses every line into an Instruction object
//  5. RUN executes each instruction one by one using a program counter (pc)
//  6. pc starts at 0 and moves forward by 1 after each instruction
//  7. JUMP and IF instructions can move pc to any label — that's how
//     loops and conditions work
//  8. STOP halts execution
// ====================================================================

public class MiniLangInterpreter {

    static Scanner keyboard = new Scanner(System.in);

    // ================================================================
    //  DATA STRUCTURE 1 — ArrayList<String>  buffer
    //
    //  WHY ArrayList?
    //  The user types lines one at a time. We don't know how many.
    //  ArrayList grows automatically with every .add().
    //  We also need to REMOVE lines by index (REMOVE command),
    //  which ArrayList supports with .remove(index).
    //
    //  This is just the raw text the user typed — not yet parsed.
    // ================================================================
    static ArrayList<String> buffer = new ArrayList<>();


    // ================================================================
    //  DATA STRUCTURE 2 — ArrayList<String[]>  program
    //
    //  WHY ArrayList of String arrays?
    //  After LOAD parses the buffer, each line becomes a String array:
    //    instruction[0] = the opcode  e.g. "SET"
    //    instruction[1] = operand A  e.g. "x"
    //    instruction[2] = operand B  e.g. "5"
    //  We store all parsed instructions in order so RUN can loop through.
    //  ArrayList because we don't know the program size upfront.
    //  Also stores the original source line number for error messages.
    // ================================================================
    static ArrayList<String[]> program = new ArrayList<>();


    // ================================================================
    //  DATA STRUCTURE 3 — HashMap<String, Integer>  variables
    //
    //  WHY HashMap?
    //  Variables are KEY → VALUE pairs.
    //  "x" → 5,  "result" → 120,  "n" → 3
    //  .get("x") gives the value instantly without looping.
    //  .put("x", 10) updates the value.
    //
    //  This is the interpreter's "memory" — it holds all SET variables.
    // ================================================================
    static HashMap<String, Integer> variables = new HashMap<>();


    // ================================================================
    //  DATA STRUCTURE 4 — HashMap<String, Integer>  labels
    //
    //  WHY HashMap?
    //  A label like "LOOP:" marks a position in the program.
    //  labels.put("LOOP", 3) means "LOOP is at instruction index 3".
    //  When we see JUMP LOOP, we do pc = labels.get("LOOP") → jump to 3.
    //  HashMap gives instant lookup — no scanning the whole program.
    // ================================================================
    static HashMap<String, Integer> labels = new HashMap<>();


    // ================================================================
    //  DATA STRUCTURE 5 — HashSet<String>  seenStates
    //
    //  WHY HashSet?
    //  To detect infinite loops.
    //  Before each instruction, we build a snapshot String of:
    //    "PC=2|x=5|result=10|"
    //  If we ever see the exact same snapshot again, the program
    //  will repeat the same steps forever → infinite loop detected.
    //  HashSet.contains() checks this instantly — no looping needed.
    //  We only ever ADD and CHECK — HashSet is perfect for that.
    // ================================================================
    static HashSet<String> seenStates = new HashSet<>();


    // ================================================================
    //  pc — Program Counter
    //
    //  This is just a plain int — not a data structure.
    //  It tracks WHICH instruction we are currently executing.
    //  pc = 0 means "run the first instruction".
    //  After each normal instruction, pc++ moves to the next one.
    //  JUMP and IF instructions set pc directly to a label's index.
    //  This is how the interpreter knows where it is in the program.
    // ================================================================
    static int pc = 0;


    // ================================================================
    //  MAIN — the shell loop
    //  Keeps reading commands until the user types EXIT
    // ================================================================
    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("  Mini-Language Interpreter");
        System.out.println("====================================");
        System.out.println("Type HELP to see all instructions.");
        System.out.println("Type EXIT to quit.");
        System.out.println();

        boolean running = true;

        while (running) {

            System.out.print("Enter Command : > ");
            String rawInput = keyboard.nextLine();
            String input    = rawInput.trim();

            if (input.isEmpty()) continue;

            String upper = input.toUpperCase();

            // ── Shell commands ──
            if (upper.equals("EXIT") || upper.equals("QUIT")) {
                System.out.println("Goodbye.");
                running = false;

            } else if (upper.equals("HELP")) {
                printHelp();

            } else if (upper.equals("LIST")) {
                listBuffer();

            } else if (upper.equals("CLEAR")) {
                buffer.clear();
                System.out.println("Program cleared.");

            } else if (upper.equals("RUN")) {
                runProgram();

            } else if (upper.startsWith("REMOVE")) {
                handleRemove(input);

            } else {
                // Any other line is treated as a program instruction
                buffer.add(rawInput);
                System.out.println("  [line " + buffer.size() + "] added.");
            }
        }

        keyboard.close();
    }


    // ================================================================
    //  LOAD — reads the buffer and prepares the program
    //  Two passes:
    //    Pass 1 — find all labels (LOOP:, END:) and record their index
    //    Pass 2 — parse every instruction line into a String[] array
    // ================================================================
    static void loadProgram() {

        // Clear everything from any previous run
        program.clear();
        variables.clear();
        labels.clear();
        seenStates.clear();
        pc = 0;

        // ── PASS 1: Find labels ──
        // We need to know label positions BEFORE parsing instructions
        // because an instruction might jump to a label defined later
        int instructionIndex = 0;

        for (int i = 0; i < buffer.size(); i++) {
            String line = buffer.get(i).trim();

            // Skip blank lines and comments
            if (line.isEmpty() || line.startsWith(";")) continue;

            if (line.endsWith(":")) {
                // This is a label — save its position in the labels HashMap
                String labelName = line.substring(0, line.length() - 1).trim();

                if (labels.containsKey(labelName)) {
                    throw new RuntimeException("Duplicate label: '" + labelName + "'");
                }

                // labels.put(key, value) — key=label name, value=instruction index
                labels.put(labelName, instructionIndex);

            } else {
                // This is a real instruction — count it
                instructionIndex++;
            }
        }

        // ── PASS 2: Parse instructions into String[] arrays ──
        int lineNumber = 0;

        for (int i = 0; i < buffer.size(); i++) {
            lineNumber++;
            String line = buffer.get(i).trim();

            // Skip blank lines, comments, and labels
            if (line.isEmpty() || line.startsWith(";") || line.endsWith(":")) continue;

            // Split into at most 3 parts: [opcode, operandA, operandB]
            // "SET x 5"   → ["SET", "x", "5"]
            // "PRINT x"   → ["PRINT", "x"]
            // "STOP"      → ["STOP"]
            String[] parts  = line.split("\\s+", 3);
            String   opcode = parts[0].toUpperCase();
            String   opA    = parts.length > 1 ? parts[1] : null;
            String   opB    = parts.length > 2 ? parts[2] : null;

            // Validate that labels used in JUMP/IF exist
            if (opcode.equals("JUMP")) {
                if (opA == null || !labels.containsKey(opA)) {
                    throw new RuntimeException("Line " + lineNumber
                            + ": Unknown label '" + opA + "'");
                }
            }
            if (opcode.equals("IFZ") || opcode.equals("IFP") || opcode.equals("IFN")) {
                if (opB == null || !labels.containsKey(opB)) {
                    throw new RuntimeException("Line " + lineNumber
                            + ": Unknown label '" + opB + "'");
                }
            }

            // Store as a 4-element array:
            // [0]=opcode  [1]=operandA  [2]=operandB  [3]=lineNumber
            String[] instruction = new String[4];
            instruction[0] = opcode;
            instruction[1] = opA;
            instruction[2] = opB;
            instruction[3] = String.valueOf(lineNumber); // for error messages

            program.add(instruction);
        }
    }


    // ================================================================
    //  RUN — executes instructions one by one
    //  pc starts at 0 and moves forward after each instruction
    //  JUMP and IF instructions move pc to a label position
    //  STOP halts the loop
    // ================================================================
    static void runProgram() {

        if (buffer.isEmpty()) {
            System.out.println("Nothing to run. Type some instructions first.");
            return;
        }

        // Load clears memory and parses the buffer fresh every time
        try {
            loadProgram();
        } catch (RuntimeException e) {
            System.out.println("[LOAD ERROR] " + e.getMessage());
            return;
        }

        System.out.println("--- Running ---");

        try {
            while (pc < program.size()) {

                // Get current instruction
                String[] instr      = program.get(pc);
                String   opcode     = instr[0];
                String   opA        = instr[1];
                String   opB        = instr[2];
                int      lineNumber = Integer.parseInt(instr[3]);

                // ── Infinite loop detection ──
                // Build a snapshot of the current state as one String
                String snapshot = buildSnapshot();
                if (seenStates.contains(snapshot)) {
                    throw new RuntimeException(
                            "Infinite loop detected at instruction " + pc
                                    + " [" + opcode + "]");
                }
                seenStates.add(snapshot);

                // ── Execute the instruction ──

                if (opcode.equals("SET")) {
                    // SET x 5  →  create variable x with value 5
                    int value = parseNumber(opB, lineNumber);
                    variables.put(opA, value);
                    pc++;

                } else if (opcode.equals("ADD")) {
                    // ADD x 3  →  x = x + 3
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current + amount);
                    pc++;

                } else if (opcode.equals("SUB")) {
                    // SUB x 3  →  x = x - 3
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current - amount);
                    pc++;

                } else if (opcode.equals("MUL")) {
                    // MUL x 3  →  x = x * 3
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current * amount);
                    pc++;

                } else if (opcode.equals("DIV")) {
                    // DIV x 3  →  x = x / 3
                    int amount = resolveValue(opB, lineNumber);
                    if (amount == 0) {
                        throw new RuntimeException("Line " + lineNumber
                                + ": Cannot divide by zero.");
                    }
                    int current = getVariable(opA, lineNumber);
                    variables.put(opA, current / amount);
                    pc++;

                } else if (opcode.equals("IFZ")) {
                    // IFZ x LABEL  →  if x == 0, jump to LABEL
                    int value = getVariable(opA, lineNumber);
                    if (value == 0) {
                        pc = labels.get(opB); // jump
                    } else {
                        pc++; // skip
                    }

                } else if (opcode.equals("IFP")) {
                    // IFP x LABEL  →  if x > 0, jump to LABEL
                    int value = getVariable(opA, lineNumber);
                    if (value > 0) {
                        pc = labels.get(opB);
                    } else {
                        pc++;
                    }

                } else if (opcode.equals("IFN")) {
                    // IFN x LABEL  →  if x < 0, jump to LABEL
                    int value = getVariable(opA, lineNumber);
                    if (value < 0) {
                        pc = labels.get(opB);
                    } else {
                        pc++;
                    }

                } else if (opcode.equals("JUMP")) {
                    // JUMP LABEL  →  always jump to LABEL
                    pc = labels.get(opA);

                } else if (opcode.equals("PRINT")) {
                    // PRINT x  →  print the value of x
                    int value = getVariable(opA, lineNumber);
                    System.out.println("  " + opA + " = " + value);
                    pc++;

                } else if (opcode.equals("PRINTS")) {
                    // PRINTS "hello"  →  print the text
                    String text = opA;
                    if (text.startsWith("\"") && text.endsWith("\"")) {
                        text = text.substring(1, text.length() - 1);
                    }
                    System.out.println("  " + text);
                    pc++;

                } else if (opcode.equals("STOP")) {
                    // STOP  →  halt
                    break;

                } else {
                    throw new RuntimeException("Line " + lineNumber
                            + ": Unknown instruction '" + opcode + "'");
                }
            }

            System.out.println("--- Done ---");
            printVariables();

        } catch (RuntimeException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }


    // ================================================================
    //  HELPER METHODS
    // ================================================================

    // resolveValue — tries to read opB as a number first,
    // if that fails it treats it as a variable name
    // e.g. "5" → 5    "x" → variables.get("x")
    static int resolveValue(String operand, int lineNumber) {
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return getVariable(operand, lineNumber);
        }
    }

    // parseNumber — converts "5" to 5, throws a clear error if not a number
    static int parseNumber(String text, int lineNumber) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Line " + lineNumber
                    + ": Expected a number, got '" + text + "'");
        }
    }

    // getVariable — looks up a variable, throws clear error if not SET yet
    static int getVariable(String name, int lineNumber) {
        Integer value = variables.get(name);
        if (value == null) {
            throw new RuntimeException("Line " + lineNumber
                    + ": Variable '" + name + "' used before SET.");
        }
        return value;
    }

    // buildSnapshot — combines pc + all variables into one String
    // Used to detect if we've been in this exact state before
    // TreeMap sorts the variables alphabetically for consistent snapshots
    static String buildSnapshot() {
        StringBuilder snapshot = new StringBuilder("PC=" + pc + "|");

        // TreeMap — sorts keys alphabetically
        // WHY TreeMap here and not HashMap?
        // HashMap does not guarantee order. "x=5|y=3" and "y=3|x=5"
        // would look different as Strings even though state is the same.
        // TreeMap always produces the same order → reliable snapshot.
        TreeMap<String, Integer> sorted = new TreeMap<>(variables);

        for (String key : sorted.keySet()) {
            snapshot.append(key).append("=").append(sorted.get(key)).append("|");
        }
        return snapshot.toString();
    }

    // printVariables — shows all variable values after run
    static void printVariables() {
        if (variables.isEmpty()) return;
        System.out.println("Variables:");
        TreeMap<String, Integer> sorted = new TreeMap<>(variables);
        for (String name : sorted.keySet()) {
            System.out.println("  " + name + " = " + sorted.get(name));
        }
    }

    // listBuffer — shows all lines currently in the buffer
    static void listBuffer() {
        if (buffer.isEmpty()) {
            System.out.println("Buffer is empty. Start typing instructions.");
            return;
        }
        System.out.println("Current program (" + buffer.size() + " lines):");
        for (int i = 0; i < buffer.size(); i++) {
            System.out.println("  " + (i + 1) + "  " + buffer.get(i));
        }
    }

    // handleRemove — deletes one line from the buffer by number
    static void handleRemove(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length < 2) {
            System.out.println("Usage: REMOVE <line number>");
            return;
        }
        try {
            int lineNumber = Integer.parseInt(parts[1]);
            if (lineNumber < 1 || lineNumber > buffer.size()) {
                System.out.println("No line " + lineNumber
                        + ". Buffer has " + buffer.size() + " line(s).");
                return;
            }
            String removed = buffer.remove(lineNumber - 1);
            System.out.println("Removed line " + lineNumber + ": " + removed);
        } catch (NumberFormatException e) {
            System.out.println("REMOVE needs a line number. Example: REMOVE 3");
        }
    }

    // printHelp — shows all instructions and an example
    static void printHelp() {
        System.out.println();
        System.out.println("======= INSTRUCTIONS =======");
        System.out.println("  SET  var  number     set var to a number        e.g. SET x 5");
        System.out.println("  ADD  var  val/var    var = var + amount          e.g. ADD x 3");
        System.out.println("  SUB  var  val/var    var = var - amount          e.g. SUB x 1");
        System.out.println("  MUL  var  val/var    var = var * amount          e.g. MUL x 2");
        System.out.println("  DIV  var  val/var    var = var / amount          e.g. DIV x 2");
        System.out.println("  IFZ  var  LABEL      jump to LABEL if var == 0  e.g. IFZ n END");
        System.out.println("  IFP  var  LABEL      jump to LABEL if var >  0  e.g. IFP n LOOP");
        System.out.println("  IFN  var  LABEL      jump to LABEL if var <  0");
        System.out.println("  JUMP LABEL           always jump to LABEL        e.g. JUMP LOOP");
        System.out.println("  PRINT var            print the value of var      e.g. PRINT x");
        System.out.println("  PRINTS \"text\"        print a message             e.g. PRINTS \"Hi\"");
        System.out.println("  STOP                 stop the program");
        System.out.println("  LABEL:               mark a jump target          e.g. LOOP:");
        System.out.println("  ; comment            ignored by the interpreter");
        System.out.println();
        System.out.println("======= SHELL COMMANDS =======");
        System.out.println("  RUN        run the program you typed");
        System.out.println("  LIST       show all lines you have typed");
        System.out.println("  CLEAR      delete everything and start fresh");
        System.out.println("  REMOVE n   delete line number n");
        System.out.println("  HELP       show this guide");
        System.out.println("  EXIT       quit the interpreter");
        System.out.println();
        System.out.println("======= EXAMPLE — Factorial of 5 =======");
        System.out.println("  SET n 5");
        System.out.println("  SET result 1");
        System.out.println("  LOOP:");
        System.out.println("  MUL result n");
        System.out.println("  SUB n 1");
        System.out.println("  IFP n LOOP");
        System.out.println("  PRINT result");
        System.out.println("  STOP");
        System.out.println("  → result = 120");
        System.out.println();
        System.out.println("======= EXAMPLE — Count to 3 =======");
        System.out.println("  SET i 1");
        System.out.println("  LOOP:");
        System.out.println("  PRINT i");
        System.out.println("  ADD i 1");
        System.out.println("  SET limit 3");
        System.out.println("  SUB limit i");
        System.out.println("  IFP limit LOOP");
        System.out.println("  STOP");
        System.out.println();
    }
}
package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;

//  This is a mini programming language interpreter.

public class MiniLangInterpreter {

    static Scanner keyboard = new Scanner(System.in);
    static ArrayList<String> buffer = new ArrayList<>();
    static ArrayList<String[]> program = new ArrayList<>();
    static HashMap<String, Integer> variables = new HashMap<>();
    static HashMap<String, Integer> labels = new HashMap<>();
    static HashSet<String> seenStates = new HashSet<>();
    static int pc = 0;

    //  Keeps reading commands until the user types EXIT
    public static void main(String[] args) {

        System.out.println("Mini-Language Interpreter");
        System.out.println("Type HELP to see all instructions.");
        System.out.println("Type EXIT to quit.");
        System.out.println();

        boolean running = true;

        while (running) {

            System.out.print("Enter Command : > ");
            String rawInput = keyboard.nextLine();
            String input = rawInput.trim();

            if (input.isEmpty()) continue;

            String upper = input.toUpperCase();

            // Shell commands
            if (upper.equals("EXIT") || upper.equals("QUIT")) {
                System.out.println("Goodbye.Thanks for using Mini-Language Interpreter");
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
                buffer.add(rawInput);
                System.out.println("  [line " + buffer.size() + "] added.");
            }
        }
        keyboard.close();
    }

    //  LOAD — reads the buffer and prepares the program
    static void loadProgram() {

        program.clear();
        variables.clear();
        labels.clear();
        seenStates.clear();
        pc = 0;
        // PASS 1: Find labels
        int instructionIndex = 0;

        for (int i = 0; i < buffer.size(); i++) {
            String line = buffer.get(i).trim();

            if (line.isEmpty() || line.startsWith(";")) continue;

            if (line.endsWith(":")) {
                String labelName = line.substring(0, line.length() - 1).trim();

                if (labels.containsKey(labelName)) {
                    throw new RuntimeException("Duplicate label: '" + labelName + "'");
                }
                labels.put(labelName, instructionIndex);
            } else {
                instructionIndex++;
            }
        }

        // PASS 2: Parse instructions into String[] arrays
        int lineNumber = 0;

        for (int i = 0; i < buffer.size(); i++) {
            lineNumber++;
            String line = buffer.get(i).trim();

            if (line.isEmpty() || line.startsWith(";") || line.endsWith(":")) continue;

            String[] parts  = line.split("\\s+", 3);
            String   opcode = parts[0].toUpperCase();
            String   opA    = parts.length > 1 ? parts[1] : null;
            String   opB    = parts.length > 2 ? parts[2] : null;

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

            String[] instruction = new String[4];
            instruction[0] = opcode;
            instruction[1] = opA;
            instruction[2] = opB;
            instruction[3] = String.valueOf(lineNumber);
            program.add(instruction);
        }
    }


    //  RUN — executes instructions one by one
    static void runProgram() {

        if (buffer.isEmpty()) {
            System.out.println("Nothing to run. Type some instructions first.");
            return;
        }
        try {
            loadProgram();
        } catch (RuntimeException e) {
            System.out.println("[LOAD ERROR] " + e.getMessage());
            return;
        }
        System.out.println("Running ...");

        try {
            while (pc < program.size()) {
                String[] instr      = program.get(pc);
                String   opcode     = instr[0];
                String   opA        = instr[1];
                String   opB        = instr[2];
                int      lineNumber = Integer.parseInt(instr[3]);

                // Infinite loop detection
                String snapshot = buildSnapshot();
                if (seenStates.contains(snapshot)) {
                    throw new RuntimeException(
                            "Infinite loop detected at instruction " + pc
                                    + " [" + opcode + "]");
                }
                seenStates.add(snapshot);

                if (opcode.equals("SET")) {
                    int value = parseNumber(opB, lineNumber);
                    variables.put(opA, value);
                    pc++;
                } else if (opcode.equals("ADD")) {
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current + amount);
                    pc++;
                } else if (opcode.equals("SUB")) {
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current - amount);
                    pc++;
                } else if (opcode.equals("MUL")) {
                    int current = getVariable(opA, lineNumber);
                    int amount  = resolveValue(opB, lineNumber);
                    variables.put(opA, current * amount);
                    pc++;
                } else if (opcode.equals("DIV")) {
                    int amount = resolveValue(opB, lineNumber);
                    if (amount == 0) {
                        throw new RuntimeException("Line " + lineNumber
                                + ": Cannot divide by zero.");
                    }
                    int current = getVariable(opA, lineNumber);
                    variables.put(opA, current / amount);
                    pc++;
                } else if (opcode.equals("IFZ")) {
                    int value = getVariable(opA, lineNumber);
                    if (value == 0) {
                        pc = labels.get(opB);
                    } else {
                        pc++;
                    }
                } else if (opcode.equals("IFP")) {
                    int value = getVariable(opA, lineNumber);
                    if (value > 0) {
                        pc = labels.get(opB);
                    } else {
                        pc++;
                    }
                } else if (opcode.equals("IFN")) {
                    int value = getVariable(opA, lineNumber);
                    if (value < 0) {
                        pc = labels.get(opB);
                    } else {
                        pc++;
                    }
                } else if (opcode.equals("JUMP")) {
                    pc = labels.get(opA);
                } else if (opcode.equals("PRINT")) {
                    int value = getVariable(opA, lineNumber);
                    System.out.println("  " + opA + " = " + value);
                    pc++;
                } else if (opcode.equals("PRINTS")) {
                    String text = opA;
                    if (text.startsWith("\"") && text.endsWith("\"")) {
                        text = text.substring(1, text.length() - 1);
                    }
                    System.out.println("  " + text);
                    pc++;

                } else if (opcode.equals("STOP")) {
                    break;
                } else {
                    throw new RuntimeException("Line " + lineNumber
                            + ": Unknown instruction '" + opcode + "'");
                }
            }
            System.out.println("Done");
            printVariables();
        } catch (RuntimeException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }


    //  HELPER METHODS
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
    static String buildSnapshot() {
        StringBuilder snapshot = new StringBuilder("PC=" + pc + "|");
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
        System.out.println("INSTRUCTIONS");
        System.out.println("SET var number set var to a number e.g. SET x 5");
        System.out.println("ADD  var  val/var var = var + amount e.g. ADD x 3");
        System.out.println("SUB  var  val/var var = var - amount   e.g. SUB x 1");
        System.out.println("MUL  var  val/var var = var * amount  e.g. MUL x 2");
        System.out.println("DIV  var  val/var var = var / amount  e.g. DIV x 2");
        System.out.println("IFZ var LABEL jump to LABEL if var == 0  e.g. IFZ n END");
        System.out.println("IFP var LABEL jump to LABEL if var >  0  e.g. IFP n LOOP");
        System.out.println("IFN var LABEL jump to LABEL if var <  0");
        System.out.println("JUMP LABEL  always jump to LABEL  e.g. JUMP LOOP");
        System.out.println("PRINT var print the value of var e.g. PRINT x");
        System.out.println("PRINTS \"text\" print a message e.g. PRINTS \"Hi\"");
        System.out.println("STOP stop the program");
        System.out.println("LABEL: mark a jump target e.g. LOOP:");
        System.out.println("; comment ignored by the interpreter");
        System.out.println();
        System.out.println("***SHELL COMMANDS ***");
        System.out.println("RUN run the program you typed");
        System.out.println("LIST show all lines you have typed");
        System.out.println("CLEAR delete everything and start fresh");
        System.out.println("REMOVE n delete line number n");
        System.out.println("HELP show this guide");
        System.out.println("EXIT quit the interpreter");
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
        System.out.println("  result = 120");
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
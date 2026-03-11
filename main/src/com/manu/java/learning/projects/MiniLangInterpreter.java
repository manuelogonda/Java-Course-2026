package com.manu.java.learning.projects;

import java.util.*;

public class MiniLangInterpreter {
    enum OpCode {
        SET,
        ADD,
        SUB,
        MUL,
        DIV,
        IFZ,
        IFP,
        IFN,
        JUMP,
        PRINT,
        PRINTS,
        STOP
    }

    static class Instruction {
        final OpCode opCode;
        final String operandA;
        final String operandB;
        final int    sourceLine;

       // CONSTRUCTOR
        Instruction(OpCode opCode, String operandA, String operandB, int sourceLine) {
            this.opCode     = opCode;
            this.operandA   = operandA;
            this.operandB   = operandB;
            this.sourceLine = sourceLine;
        }

        // @Override tells us we're replacing the default version.
        @Override
        public String toString() {
            String result = opCode.name();
            if (operandA != null) result += " " + operandA;
            if (operandB != null) result += " " + operandB;
            return result;
        }
    }

    //  INSTANCE FIELDS — The interpreter's "memory"
    private final List<Instruction>    program    = new ArrayList<>();
    private final Map<String, Integer> variables  = new HashMap<>();
    private final Map<String, Integer> labels     = new HashMap<>();
    private final Set<String>          seenStates = new HashSet<>();
    private int pc = 0;

    public void load(List<String> sourceLines) {
        // Clearing collections before a fresh run
        program.clear();
        variables.clear();
        labels.clear();
        seenStates.clear();
        pc = 0;

        // PASS 1: Collect labels only
        int instructionCount = 0;
        for (String rawLine : sourceLines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith(";")) continue;

            if (line.endsWith(":")) {
                String labelName = line.substring(0, line.length() - 1).trim();
                if (labels.containsKey(labelName)) {
                    throw new RuntimeException("Duplicate label: '" + labelName + "'");
                }
                labels.put(labelName, instructionCount);
            } else {
                instructionCount++;
            }
        }

        // PASS 2: Parse instructions
        int lineNum = 0;
        for (String rawLine : sourceLines) {
            lineNum++;
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith(";") || line.endsWith(":")) continue;
            program.add(parseLine(line, lineNum));
        }
    }

    private Instruction parseLine(String line, int sourceLine) {
        String[] tokens = line.split("\\s+", 3);
        OpCode op;
        switch (tokens[0].toUpperCase()) {
            case "SET": op = OpCode.SET; break;
            case "ADD": op = OpCode.ADD; break;
            case "SUB": op = OpCode.SUB;  break;
            case "MUL": op = OpCode.MUL; break;
            case "DIV": op = OpCode.DIV; break;
            case "IFZ": op = OpCode.IFZ; break;
            case "IFP": op = OpCode.IFP; break;
            case "IFN": op = OpCode.IFN; break;
            case "JUMP": op = OpCode.JUMP; break;
            case "PRINT": op = OpCode.PRINT;  break;
            case "PRINTS": op = OpCode.PRINTS; break;
            case "STOP": op = OpCode.STOP; break;
            default:
                throw new RuntimeException(
                        "Line " + sourceLine + ": Unknown opcode '" + tokens[0] + "'");
        }

        String operandA = tokens.length > 1 ? tokens[1] : null;
        String operandB = tokens.length > 2 ? tokens[2] : null;

        validateOperands(op, operandA, operandB, sourceLine);

        if (op == OpCode.JUMP && !labels.containsKey(operandA)) {
            throw new RuntimeException("Line " + sourceLine + ": Undefined label '" + operandA + "'");
        }
        if ((op == OpCode.IFZ || op == OpCode.IFP || op == OpCode.IFN) && !labels.containsKey(operandB)) {
            throw new RuntimeException("Line " + sourceLine + ": Undefined label '" + operandB + "'");
        }

        return new Instruction(op, operandA, operandB, sourceLine);
    }

    // EnumSet
    private void validateOperands(OpCode op, String operandA, String operandB, int sourceLine) {

        EnumSet<OpCode> needsTwoOperands = EnumSet.of(
                OpCode.SET, OpCode.ADD, OpCode.SUB, OpCode.MUL, OpCode.DIV,
                OpCode.IFZ, OpCode.IFP, OpCode.IFN
        );

        EnumSet<OpCode> needsOneOperand = EnumSet.of(
                OpCode.JUMP, OpCode.PRINT, OpCode.PRINTS
        );

        if (needsTwoOperands.contains(op) && (operandA == null || operandB == null)) {
            throw new RuntimeException("Line " + sourceLine + ": " + op + " needs 2 operands.");
        }
        if (needsOneOperand.contains(op) && operandA == null) {
            throw new RuntimeException("Line " + sourceLine + ": " + op + " needs 1 operand.");
        }
    }


    //  THE MAIN EXECUTION LOOP — run()
    public void run() {

        if (program.isEmpty()) {
            System.out.println("  Nothing to run. Type some instructions first.");
            return;
        }

        System.out.println("Program Running");
        boolean running = true;
        while (running && pc < program.size()) {
            //Infinite Loop Detection
            String currentStateKey = buildStateKey();
            if (seenStates.contains(currentStateKey)) {
                throw new RuntimeException(
                        "Infinite loop detected at instruction " + pc
                                + " -> [" + program.get(pc) + "]");
            }
            seenStates.add(currentStateKey);

            Instruction currentInstruction = program.get(pc);

            //Execute
            switch (currentInstruction.opCode) {

                case SET: {
                    int value = parseIntLiteral(currentInstruction.operandB, currentInstruction.sourceLine);
                    variables.put(currentInstruction.operandA, value);
                    pc++;
                    break;
                }

                case ADD: {
                    int currentValue = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    int addAmount    = resolveOperand(currentInstruction.operandB, currentInstruction.sourceLine);
                    variables.put(currentInstruction.operandA, currentValue + addAmount);
                    pc++;
                    break;
                }
                case SUB: {
                    int currentValue   = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    int subtractAmount = resolveOperand(currentInstruction.operandB, currentInstruction.sourceLine);
                    variables.put(currentInstruction.operandA, currentValue - subtractAmount);
                    pc++;
                    break;
                }
                case MUL: {
                    int currentValue   = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    int multiplyAmount = resolveOperand(currentInstruction.operandB, currentInstruction.sourceLine);
                    variables.put(currentInstruction.operandA, currentValue * multiplyAmount);
                    pc++;
                    break;
                }

                case DIV: {
                    int divisor = resolveOperand(currentInstruction.operandB, currentInstruction.sourceLine);
                    if (divisor == 0) {
                        throw new RuntimeException("Line " + currentInstruction.sourceLine + ": Division by zero.");
                    }
                    int currentValue = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    variables.put(currentInstruction.operandA, currentValue / divisor);
                    pc++;
                    break;
                }

                case IFZ: {
                    int variableValue = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    if (variableValue == 0) {
                        pc = labels.get(currentInstruction.operandB);
                    } else {
                        pc++;
                    }
                    break;
                }
                case IFP: {
                    int variableValue = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    pc = (variableValue > 0) ? labels.get(currentInstruction.operandB) : pc + 1;
                    break;
                }

                case IFN: {
                    int variableValue = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    pc = (variableValue < 0) ? labels.get(currentInstruction.operandB) : pc + 1;
                    break;
                }

                case JUMP: {
                    pc = labels.get(currentInstruction.operandA);
                    break;
                }

                case PRINT: {
                    int value = requireVariable(currentInstruction.operandA, currentInstruction.sourceLine);
                    System.out.println("  " + currentInstruction.operandA + " = " + value);
                    pc++;
                    break;
                }

                case PRINTS: {
                    String text = currentInstruction.operandA;
                    boolean hasQuotes = text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2;
                    if (hasQuotes) {
                        text = text.substring(1, text.length() - 1);
                    }
                    System.out.println("  " + text);
                    pc++;
                    break;
                }
                case STOP: {
                    running = false;
                    break;
                }
            }
        }
        System.out.println("Done");
        printFinalState();
    }


    //  HELPER METHODS
    // Resolve an operand: try to parse it as a number first,
    private int resolveOperand(String operand, int sourceLine) {
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return requireVariable(operand, sourceLine);
        }
    }

    // Parse a string as an integer literal — throw a clear error if it's not.
    private int parseIntLiteral(String text, int sourceLine) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Line " + sourceLine + ": Expected an integer, got '" + text + "'");
        }
    }

    // Look up a variable — throw a clear error if it hasn't been SET yet.
    private int requireVariable(String variableName, int sourceLine) {
        Integer value = variables.get(variableName);
        if (value == null) {
            throw new RuntimeException(
                    "Line " + sourceLine + ": Variable '" + variableName + "' used before being SET.");
        }
        return value;
    }

    //  CONCEPT: StringBuilder
    private String buildStateKey() {
        StringBuilder snapshot = new StringBuilder("PC=").append(pc).append("|");

        Map<String, Integer> sortedVariables = new TreeMap<>(variables);
        for (Map.Entry<String, Integer> entry : sortedVariables.entrySet()) {
            snapshot.append(entry.getKey()).append("=").append(entry.getValue()).append("|");
        }

        return snapshot.toString();
    }

    // Print all variable values at the end of a run
    private void printFinalState() {
        if (variables.isEmpty()) return;
        System.out.println("Variables after run:");

        // TreeMap again for alphabetical, consistent output
        new TreeMap<>(variables).forEach((name, value) ->
                System.out.println("  " + name + " = " + value));
    }

    // Display the buffered program lines back to the user, numbered
    private static void listProgram(List<String> buffer) {
        if (buffer.isEmpty()) {
            System.out.println("  (empty — nothing typed yet)");
            return;
        }
        System.out.println("Current program:");
        for (int i = 0; i < buffer.size(); i++) {
            // %3d = right-aligned integer in 3 chars, %s = string, %n = newline
            System.out.printf("  %3d  %s%n", i + 1, buffer.get(i));
        }
    }

    // Print a cheat-sheet of all supported instructions
    private static void printHelp() {
        System.out.println("INSTRUCTIONS");
        System.out.println("SET  var  value  var = integer literal");
        System.out.println("ADD  var  val/var  var = var + operand");
        System.out.println("SUB  var  val/var  var = var - operand");
        System.out.println("MUL  var  val/var var = var * operand");
        System.out.println("DIV  var  val/var  var = var / operand");
        System.out.println("IFZ  var  LABEL jump if var == 0");
        System.out.println("IFP  var  LABEL jump if var >  0");
        System.out.println("IFN  var  LABEL jump if var <  0");
        System.out.println("JUMP LABEL unconditional jump");
        System.out.println("PRINT  var print variable");
        System.out.println("PRINTS \"text\" print string");
        System.out.println("STOP  halt program");
        System.out.println("LABEL: define a jump target");
        System.out.println("  ; text comment (ignored)");
        System.out.println();
        System.out.println("SHELL COMMANDS");
        System.out.println("RUN  parse and execute the program");
        System.out.println("LIST show lines you have typed so far");
        System.out.println("CLEAR wipe everything, start fresh");
        System.out.println("REMOVE n delete line number n");
        System.out.println("HELP show this reference");
        System.out.println("EXIT quit the interpreter");
        System.out.println();
        System.out.println("EXAMPLE — Factorial of 5:");
        System.out.println("SET n 5");
        System.out.println("SET result 1");
        System.out.println("LOOP:");
        System.out.println("MUL result n");
        System.out.println("SUB n 1");
        System.out.println("IFP n LOOP");
        System.out.println("PRINT result");
        System.out.println("STOP");
        System.out.println(" result = 120");
    }


    // The Program Entry Point
    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        List<String> buffer = new ArrayList<>();
        MiniLangInterpreter vm = new MiniLangInterpreter();

        System.out.println("Mini-Language Interpreter");
        System.out.println("Type HELP for instructions and examples.");

        // Shell Loop
        while (true) {
            System.out.print("Enter command > ");

            if (!keyboard.hasNextLine()) break;

            String rawInput = keyboard.nextLine();
            String trimmedInput = rawInput.trim();

            if (trimmedInput.isEmpty()) continue;

            String upperInput = trimmedInput.toUpperCase();
            String[] parts    = trimmedInput.split("\\s+");

            // Shell Commands
            if (upperInput.equals("EXIT") || upperInput.equals("QUIT")) {
                System.out.println("Goodbye.");
                break;
            } else if (upperInput.equals("HELP")) {
                printHelp();
            } else if (upperInput.equals("LIST")) {
                listProgram(buffer);
            } else if (upperInput.equals("CLEAR")) {
                buffer.clear();
                System.out.println("  Program cleared.");
            } else if (upperInput.startsWith("REMOVE")) {
                if (parts.length < 2) {
                    System.out.println("  Usage: REMOVE <line-number>");
                } else {
                    try {
                        int lineNumber = Integer.parseInt(parts[1]);
                        boolean lineExists = lineNumber >= 1 && lineNumber <= buffer.size();

                        if (!lineExists) {
                            System.out.println("  No line " + lineNumber
                                    + ". Program has " + buffer.size() + " line(s).");
                        } else {
                            String removedLine = buffer.remove(lineNumber - 1);
                            System.out.println("  Removed line " + lineNumber + ": " + removedLine);
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("  REMOVE needs an integer line number.");
                    }
                }

            } else if (upperInput.equals("RUN")) {
                if (buffer.isEmpty()) {
                    System.out.println("Nothing to run. Type some instructions first.");
                } else {
                    try {
                        vm.load(buffer);
                        vm.run();
                    } catch (RuntimeException error) {
                        System.out.println("  [ERROR] " + error.getMessage());
                    }
                }

            } else {
                buffer.add(rawInput);
                System.out.println("  [line " + buffer.size() + "] added.");
            }
        }
        keyboard.close();
    }
}
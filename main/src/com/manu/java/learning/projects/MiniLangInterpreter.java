package com.manu.java.learning.projects;

import java.util.*;

//  DETERMINISTIC MINI-LANGUAGE INTERPRETER

public class MiniLangInterpreter {
    //  ENUM — fixed set of named opcodes
    //  Typos become compile errors, not silent runtime bugs.
    enum OpCode {
        SET, ADD, SUB, MUL, DIV,
        IFZ, IFP, IFN, JUMP,
        PRINT, PRINTS,
        STOP
    }


    //  Instruction — one parsed source line
    static class Instruction {

        final OpCode opCode;
        final String operandA;
        final String operandB;
        final int    sourceLine;

        Instruction(OpCode opCode, String operandA, String operandB, int sourceLine) {
            this.opCode     = opCode;
            this.operandA   = operandA;
            this.operandB   = operandB;
            this.sourceLine = sourceLine;
        }

        @Override
        public String toString() {
            String s = opCode.name();
            if (operandA != null) s += " " + operandA;
            if (operandB != null) s += " " + operandB;
            return s;
        }
    }


    //  INTERPRETER STATE
    private final List<Instruction>    program    = new ArrayList<>();
    private final Map<String, Integer> variables  = new HashMap<>();
    private final Map<String, Integer> labels     = new HashMap<>();
    private final Set<String>          seenStates = new HashSet<>();
    private int pc = 0;


    //  LOAD — two-pass parse
    //  Pass 1: collect all label positions
    //  Pass 2: parse every non-label line into an Instruction
    public void load(List<String> sourceLines) {
        program.clear();
        variables.clear();
        labels.clear();
        seenStates.clear();
        pc = 0;

        // Pass 1 — labels only
        int instructionCount = 0;
        for (String raw : sourceLines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith(";")) continue;
            if (line.endsWith(":")) {
                String name = line.substring(0, line.length() - 1).trim();
                if (labels.containsKey(name))
                    throw new RuntimeException("Duplicate label: '" + name + "'");
                labels.put(name, instructionCount);
            } else {
                instructionCount++;
            }
        }

        // Pass 2 — instructions
        int lineNum = 0;
        for (String raw : sourceLines) {
            lineNum++;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith(";") || line.endsWith(":")) continue;
            program.add(parseLine(line, lineNum));
        }
    }

    private Instruction parseLine(String line, int sourceLine) {
        String[] tokens = line.split("\\s+", 3);

        OpCode op;
        switch (tokens[0].toUpperCase()) {
            case "SET":    op = OpCode.SET;    break;
            case "ADD":    op = OpCode.ADD;    break;
            case "SUB":    op = OpCode.SUB;    break;
            case "MUL":    op = OpCode.MUL;    break;
            case "DIV":    op = OpCode.DIV;    break;
            case "IFZ":    op = OpCode.IFZ;    break;
            case "IFP":    op = OpCode.IFP;    break;
            case "IFN":    op = OpCode.IFN;    break;
            case "JUMP":   op = OpCode.JUMP;   break;
            case "PRINT":  op = OpCode.PRINT;  break;
            case "PRINTS": op = OpCode.PRINTS; break;
            case "STOP":   op = OpCode.STOP;   break;
            default:
                throw new RuntimeException(
                        "Line " + sourceLine + ": Unknown opcode '" + tokens[0] + "'");
        }

        String a = tokens.length > 1 ? tokens[1] : null;
        String b = tokens.length > 2 ? tokens[2] : null;

        validateOperands(op, a, b, sourceLine);

        if (op == OpCode.JUMP && !labels.containsKey(a))
            throw new RuntimeException("Line " + sourceLine + ": Undefined label '" + a + "'");
        if ((op == OpCode.IFZ || op == OpCode.IFP || op == OpCode.IFN) && !labels.containsKey(b))
            throw new RuntimeException("Line " + sourceLine + ": Undefined label '" + b + "'");

        return new Instruction(op, a, b, sourceLine);
    }

    private void validateOperands(OpCode op, String a, String b, int line) {
        EnumSet<OpCode> needsTwo = EnumSet.of(
                OpCode.SET, OpCode.ADD, OpCode.SUB, OpCode.MUL, OpCode.DIV,
                OpCode.IFZ, OpCode.IFP, OpCode.IFN);
        EnumSet<OpCode> needsOne = EnumSet.of(
                OpCode.JUMP, OpCode.PRINT, OpCode.PRINTS);

        if (needsTwo.contains(op) && (a == null || b == null))
            throw new RuntimeException("Line " + line + ": " + op + " needs 2 operands.");
        if (needsOne.contains(op) && a == null)
            throw new RuntimeException("Line " + line + ": " + op + " needs 1 operand.");
    }


    //  RUN  fetch -> decode -> execute loop
    public void run() {
        if (program.isEmpty()) {
            System.out.println("  Nothing to run. Type some instructions first.");
            return;
        }
        System.out.println("Program Running");

        boolean running = true;
        while (running && pc < program.size()) {

            // Infinite loop detection
            String stateKey = buildStateKey();
            if (seenStates.contains(stateKey))
                throw new RuntimeException(
                        "Infinite loop detected at instruction " + pc
                                + " → [" + program.get(pc) + "]");
            seenStates.add(stateKey);

            Instruction inst = program.get(pc);

            switch (inst.opCode) {

                case SET: {
                    variables.put(inst.operandA, parseIntLiteral(inst.operandB, inst.sourceLine));
                    pc++;
                    break;
                }
                case ADD: {
                    variables.put(inst.operandA,
                            requireVariable(inst.operandA, inst.sourceLine)
                                    + resolveOperand(inst.operandB, inst.sourceLine));
                    pc++;
                    break;
                }
                case SUB: {
                    variables.put(inst.operandA,
                            requireVariable(inst.operandA, inst.sourceLine)
                                    - resolveOperand(inst.operandB, inst.sourceLine));
                    pc++;
                    break;
                }
                case MUL: {
                    variables.put(inst.operandA,
                            requireVariable(inst.operandA, inst.sourceLine)
                                    * resolveOperand(inst.operandB, inst.sourceLine));
                    pc++;
                    break;
                }
                case DIV: {
                    int divisor = resolveOperand(inst.operandB, inst.sourceLine);
                    if (divisor == 0)
                        throw new RuntimeException(
                                "Line " + inst.sourceLine + ": Division by zero.");
                    variables.put(inst.operandA,
                            requireVariable(inst.operandA, inst.sourceLine) / divisor);
                    pc++;
                    break;
                }
                case IFZ: {
                    int val = requireVariable(inst.operandA, inst.sourceLine);
                    pc = (val == 0) ? labels.get(inst.operandB) : pc + 1;
                    break;
                }
                case IFP: {
                    int val = requireVariable(inst.operandA, inst.sourceLine);
                    pc = (val > 0) ? labels.get(inst.operandB) : pc + 1;
                    break;
                }
                case IFN: {
                    int val = requireVariable(inst.operandA, inst.sourceLine);
                    pc = (val < 0) ? labels.get(inst.operandB) : pc + 1;
                    break;
                }
                case JUMP: {
                    pc = labels.get(inst.operandA);
                    break;
                }
                case PRINT: {
                    System.out.println("  " + inst.operandA + " = "
                            + requireVariable(inst.operandA, inst.sourceLine));
                    pc++;
                    break;
                }
                case PRINTS: {
                    String text = inst.operandA;
                    if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2)
                        text = text.substring(1, text.length() - 1);
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

        System.out.println("--- Done ---");
        printFinalState();
    }


    //  HELPERS
    private int resolveOperand(String operand, int line) {
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return requireVariable(operand, line);
        }
    }

    private int parseIntLiteral(String s, int line) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Line " + line + ": Expected integer literal, got '" + s + "'");
        }
    }

    private int requireVariable(String name, int line) {
        Integer value = variables.get(name);
        if (value == null)
            throw new RuntimeException(
                    "Line " + line + ": Variable '" + name + "' used before being SET.");
        return value;
    }

    private String buildStateKey() {
        StringBuilder sb = new StringBuilder("PC=").append(pc).append("|");
        new TreeMap<>(variables).forEach((k, v) ->
                sb.append(k).append("=").append(v).append("|"));
        return sb.toString();
    }

    private void printFinalState() {
        if (variables.isEmpty()) return;
        System.out.println("Variables after run:");
        new TreeMap<>(variables).forEach((k, v) ->
                System.out.println("  " + k + " = " + v));
    }

    // Shows the buffered program back to the user, numbered
    private static void listProgram(List<String> buffer) {
        if (buffer.isEmpty()) {
            System.out.println("  (empty — nothing typed yet)");
            return;
        }
        System.out.println("Current program ");
        for (int i = 0; i < buffer.size(); i++)
            System.out.printf(" %3d  %s%n", i + 1, buffer.get(i));
    }

    private static void printHelp() {
        System.out.println("  INSTRUCTIONS");
        System.out.println("  SET  var  value      var = integer literal");
        System.out.println("  ADD  var  val/var    var = var + operand");
        System.out.println("  SUB  var  val/var    var = var - operand");
        System.out.println("  MUL  var  val/var    var = var * operand");
        System.out.println("  DIV  var  val/var    var = var / operand");
        System.out.println("  IFZ  var  LABEL      jump if var == 0");
        System.out.println("  IFP  var  LABEL      jump if var >  0");
        System.out.println("  IFN  var  LABEL      jump if var <  0");
        System.out.println("  JUMP LABEL unconditional jump");
        System.out.println("  PRINT  var print variable");
        System.out.println("  PRINTS \"text\" print string");
        System.out.println("  STOP halt program");
        System.out.println("  LABEL:  define a jump target");
        System.out.println("  ; text comment (ignored)");
        System.out.println("  SHELL COMMANDS  (type these at the prompt)");
        System.out.println("  RUN  parse and execute the program");
        System.out.println("  LIST  show lines you have typed so far");
        System.out.println("  CLEAR  wipe everything, start fresh");
        System.out.println("  REMOVE n delete line number n");
        System.out.println("  HELP show this reference");
        System.out.println("  EXIT quit the interpreter");
        System.out.println("  EXAMPLE SESSION");
        System.out.println(" -> SET n 5");
        System.out.println(" -> SET result 1");
        System.out.println(" -> LOOP : ");
        System.out.println(" -> MUL result n");
        System.out.println(" -> SUB n 1");
        System.out.println(" -> IFP n LOOP");
        System.out.println(" -> PRINT result");
        System.out.println(" -> STOP");
        System.out.println("-> RUN");
        System.out.println("->result = 120");
    }

    // Read -> Evaluate -> Print -> Loop
    //  The shell loop reads one line at a time.
    //  Shell commands (RUN, LIST, CLEAR, REMOVE, HELP, EXIT)
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> buffer = new ArrayList<>();

        MiniLangInterpreter vm = new MiniLangInterpreter();

        System.out.println("Mini-Language Interpreter");
        System.out.println("Type HELP for instructions & examples ");
        // The shell runs forever until the user types EXIT.
        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;
            String raw = sc.nextLine();

            String line = raw.trim();
            if (line.isEmpty()) continue;

            // CONTROL FLOW: if/else chain for shell commands
            String upper = line.toUpperCase();
            String[] parts = line.split("\\s+");

            if (upper.equals("EXIT") || upper.equals("QUIT")) {
                System.out.println("Goodbye.");
                break;

            } else if (upper.equals("HELP")) {
                printHelp();

            } else if (upper.equals("LIST")) {
                listProgram(buffer);

            } else if (upper.equals("CLEAR")) {
                buffer.clear();
                System.out.println("  Program cleared.");

            } else if (upper.startsWith("REMOVE")) {
                // REMOVE n  — delete line n (1-based, matching LIST output)
                if (parts.length < 2) {
                    System.out.println("  Usage: REMOVE <line-number>");
                } else {
                    try {
                        int n = Integer.parseInt(parts[1]);
                        if (n < 1 || n > buffer.size()) {
                            System.out.println("  No line " + n
                                    + ". Program has " + buffer.size() + " line(s).");
                        } else {
                            String removed = buffer.remove(n - 1);  // List is 0-based internally
                            System.out.println("  Removed line " + n + ": " + removed);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  REMOVE needs an integer line number.");
                    }
                }

            } else if (upper.equals("RUN")) {
                // Parse and run the buffered program
                if (buffer.isEmpty()) {
                    System.out.println("  Nothing to run. Type some instructions first.");
                } else {
                    try {
                        vm.load(buffer);
                        vm.run();
                    } catch (RuntimeException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }

            } else {
                // Not a shell command — treat as a source code line
                buffer.add(raw);
                System.out.println("  [" + buffer.size() + "] added.");
            }
        }

        sc.close();
    }
}

package com.manu.java.learning.projects;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Stack;



public class StreamingParser {
    static Scanner sc = new Scanner(System.in);

   public static void main(String[] args) throws Exception {
        System.out.println("Enter your Math Expression : ");
          String input = sc.nextLine();
        System.out.println("Result is " + evaluate(new StringReader(input)));
    }

    public static int evaluate(Reader reader) throws IOException {
        Stack<Integer> valueStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        int position = 0;
        boolean expectOperand = true;
        int ch;
        int currentNumber = 0;
        boolean buildingNumber = false;

        while ((ch = reader.read()) != -1) {
            position++;
            char c = (char) ch;

            if (Character.isWhitespace(c)){
                continue;
            }

            if (Character.isDigit(c)) {
                buildingNumber = true;
                currentNumber = (currentNumber * 10) + (c - '0');
                continue;
            }

            if (buildingNumber) {
                valueStack.push(currentNumber);
                currentNumber = 0;
                buildingNumber = false;
                expectOperand = false;
            }

            if (isOperator(c)){
                if (expectOperand){
                    throw new RuntimeException("Missing operand at position " + position);
                }
                while(!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(c)){
                    applyToOperator(valueStack,operatorStack,position);
                }
                operatorStack.push(c);
                expectOperand = true;
                continue;
            }

            if (c == '(') {
                if (!expectOperand) {
                    throw new RuntimeException("Missing closing bracket ')' at position " + position);
                }
                operatorStack.push(c);
                continue;
            }

                if (c == ')') {
                    if (expectOperand) {
                        throw new RuntimeException("Missing opening bracket '(' at or empty brackets " + position);
                    }
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        applyToOperator(valueStack, operatorStack, position);
                    }

                if (operatorStack.isEmpty()) {
                    throw new RuntimeException("Missing opening bracket '(' " +  " at " + position);
                }
                    operatorStack.pop();
                    expectOperand = false;
                    continue;
            }
                throw new RuntimeException("Invalid character " + c + " at position " + position);
        }

        if (buildingNumber){
            valueStack.push(currentNumber);
            expectOperand = false;
        }

        if(expectOperand){
            throw new RuntimeException("Expression ends unexpectedly");
        }

        while(!operatorStack.isEmpty()){
            if (operatorStack.peek() == '('){
                throw new RuntimeException("Missing closing bracket ')' at " + position);
            }
            applyToOperator(valueStack,operatorStack,position);

        }
            if (valueStack.size() != 1){
                throw new RuntimeException("Invalid expression at " + position);
            }
        return valueStack.pop();
    }

    public static void applyToOperator(Stack<Integer> valueStack, Stack<Character> operatorStack, int position)  {
            int value2 = valueStack.pop();
            int value1 = valueStack.pop();
            char operator = operatorStack.pop();

            if (value2 == 0 && operator == '/'){
                throw new RuntimeException("Division by zero");
            }
            int result = switch (operator){
                case '+' -> value1 + value2;
                case '-' -> value1 - value2;
                case '*' -> value1 * value2;
                case '/' -> value1 / value2;
                default -> throw new RuntimeException("Invalid operator " + operator + " at " + position);
            };
            valueStack.push(result);
    }

    public  static boolean isOperator(char c){
            return c == '+' || c == '/' || c == '-' || c == '*';
    }

    public  static int precedence(char c){
            if (c == '+' || c == '-') return 1;
            if (c == '*' || c == '/') return 2;
            return 0;
    }

}


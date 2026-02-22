package com.manu.java.learning.projects;

import  java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class CalculatorWithUi {
    static void main(String[] args) {
        Calculator calculator = new Calculator();
    }
    public static class Calculator {
        int boardWidth = 360;
        int boardHeight = 540;

        Color customLightGray = new Color(212, 212, 210);
        Color customDarkGray = new Color(80, 80, 80);
        Color customBlack = new Color(28, 28, 28);
        Color customOrange = new Color(255, 149, 0);

        String[] buttonValues = {
                "AC", "+/-", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "√", "="
        };
        String[] rightSymbols = {"÷", "×", "-", "+", "="};
        String[] topSymbols = {"AC", "+/-", "%"};

        JFrame frame = new JFrame("Calculator");
        JLabel displayLabel = new JLabel();
        JPanel displayPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();

        //A+B, A-B, A*B, A/B
        String A = "0";
        String operator = null;
        String B = null;

        Calculator() {
            // frame.setVisible(true);
            frame.setSize(boardWidth, boardHeight);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            displayLabel.setBackground(customBlack);
            displayLabel.setForeground(Color.white);
            displayLabel.setFont(new Font("Arial", Font.PLAIN, 80));
            displayLabel.setHorizontalAlignment(JLabel.RIGHT);
            displayLabel.setText("0");
            displayLabel.setOpaque(true);

            displayPanel.setLayout(new BorderLayout());
            displayPanel.add(displayLabel);
            frame.add(displayPanel, BorderLayout.NORTH);

            buttonsPanel.setLayout(new GridLayout(5, 4));
            buttonsPanel.setBackground(customBlack);
            frame.add(buttonsPanel);

            for (String value : buttonValues) {
                JButton button = new JButton();
                String buttonValue = value;
                button.setFont(new Font("Arial", Font.PLAIN, 30));
                button.setText(buttonValue);
                button.setFocusable(false);
                button.setBorder(new LineBorder(customBlack));
                if (Arrays.asList(topSymbols).contains(buttonValue)) {
                    button.setBackground(customLightGray);
                    button.setForeground(customBlack);
                } else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                    button.setBackground(customOrange);
                    button.setForeground(Color.white);
                } else {
                    button.setBackground(customDarkGray);
                    button.setForeground(Color.white);
                }
                buttonsPanel.add(button);

                button.addActionListener(e -> {
                    JButton button1 = (JButton) e.getSource();
                    String buttonValue1 = button1.getText();
                    if (Arrays.asList(rightSymbols).contains(buttonValue1)) {
                        if (Objects.equals(buttonValue1, "=")) {
                            if (A != null) {
                                B = displayLabel.getText();
                                double numA = Double.parseDouble(A);
                                double numB = Double.parseDouble(B);

                                if (Objects.equals(operator, "+")) {
                                    displayLabel.setText(removeZeroDecimal(numA + numB));
                                } else if (Objects.equals(operator, "-")) {
                                    displayLabel.setText(removeZeroDecimal(numA - numB));
                                } else if (Objects.equals(operator, "×")) {
                                    displayLabel.setText(removeZeroDecimal(numA * numB));
                                } else if (Objects.equals(operator, "÷")) {
                                    displayLabel.setText(removeZeroDecimal(numA / numB));
                                }
                                clearAll();
                            }
                        } else if ("+-×÷".contains(buttonValue1)) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                            }
                            operator = buttonValue1;
                        }
                    } else if (Arrays.asList(topSymbols).contains(buttonValue1)) {
                        if (Objects.equals(buttonValue1, "AC")) {
                            clearAll();
                            displayLabel.setText("0");
                        } else if (Objects.equals(buttonValue1, "+/-")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay *= -1;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        } else if (Objects.equals(buttonValue1, "%")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay /= 100;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                    } else { //digits or .
                        if (Objects.equals(buttonValue1, ".")) {
                            if (!displayLabel.getText().contains(buttonValue1)) {
                                displayLabel.setText(displayLabel.getText() + buttonValue1);
                            }
                        } else if ("0123456789".contains(buttonValue1)) {
                            if (Objects.equals(displayLabel.getText(), "0")) {
                                displayLabel.setText(buttonValue1);
                            } else {
                                displayLabel.setText(displayLabel.getText() + buttonValue1);
                            }
                        }
                    }
                });
                frame.setVisible(true);
            }
        }

        void clearAll() {
            A = "0";
            operator = null;
            B = null;
        }

        String removeZeroDecimal(double numDisplay) {
            if (numDisplay % 1 == 0) {
                return Integer.toString((int) numDisplay);
            }
            return Double.toString(numDisplay);
        }
    }
}

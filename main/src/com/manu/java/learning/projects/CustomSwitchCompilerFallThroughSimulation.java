package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomSwitchCompilerFallThroughSimulation {

    record CaseEntry(String label, String action, boolean hasBreak) {
        boolean isDefault() { return label.equalsIgnoreCase("default"); }
    }

    public static void simulateSwitch(String value, List<CaseEntry> cases) {
        int startIndex = -1;

        // Pass 1: find first matching case label (ignore default)
        for (int i = 0; i < cases.size(); i++) {
            CaseEntry c = cases.get(i);
            if (!c.isDefault() && c.label().equals(value)) {
                startIndex = i;
                break;
            }
        }

        // Pass 2: if no match, find default
        if (startIndex == -1) {
            for (int i = 0; i < cases.size(); i++) {
                if (cases.get(i).isDefault()) {
                    startIndex = i;
                    break;
                }
            }
        }

        // No match and no default
        if (startIndex == -1) {
            System.out.println("(no output — no matching case and no default)");
            return;
        }

        //Execute from startIndex with fall-through
        System.out.println("Output:");
        for (int i = startIndex; i < cases.size(); i++) {
            CaseEntry c = cases.get(i);
            System.out.println("  " + c.action());
            if (c.hasBreak()) break;   // stop fall-through
        }
    }

    //  Input reading
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<CaseEntry> cases = new ArrayList<>();

        System.out.print("Enter switch value: ");
        String value = sc.nextLine().trim();

        System.out.print("How many cases (including default if any)? ");
        int n = Integer.parseInt(sc.nextLine().trim());

        for (int i = 0; i < n; i++) {
            System.out.println(" Case " + (i + 1) + " ──");

            System.out.print("  Label (or 'default'): ");
            String label = sc.nextLine().trim();

            System.out.print("  Action string: ");
            String action = sc.nextLine().trim();

            System.out.print("  Has break? (yes/no): ");
            boolean hasBreak = sc.nextLine().trim().equalsIgnoreCase("yes");

            cases.add(new CaseEntry(label, action, hasBreak));
        }

        System.out.println("Simulating switch(" + value + ") ");
        simulateSwitch(value, cases);
    }
}

package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.Scanner;

//  FAIR SCHEDULER — Round Robin + Priority + Aging
public class FairScheduler {

    static Scanner scanner = new Scanner(System.in);
    static class Task {
        String name;
        int    arrivalTime;
        int    burstTime;
        int    remainingTime;
        int    priority;
        int    waitTime;
        int    finishTime;
        int    originalPriority;

        Task(String name, int arrivalTime, int burstTime, int priority) {
            this.name             = name;
            this.arrivalTime      = arrivalTime;
            this.burstTime        = burstTime;
            this.remainingTime    = burstTime;
            this.priority         = priority;
            this.originalPriority = priority;
            this.waitTime         = 0;
            this.finishTime       = 0;
        }
    }

    static ArrayList<Task> incoming = new ArrayList<>();
    static ArrayList<Task> readyQueue = new ArrayList<>();
    static ArrayList<String> ganttLog = new ArrayList<>();
    static ArrayList<Task> finished = new ArrayList<>();
    static int quantum      = 2;
    static int agingLimit   = 5;


    //  MAIN MENU
    public static void main(String[] args) {
        System.out.println("FAIR SCHEDULER SIMULATOR");

        boolean running = true;

        while (running) {
            System.out.println(">>>> MENU <<<<<");
            System.out.println("  1. Add a task");
            System.out.println("  2. View tasks added so far");
            System.out.println("  3. Change quantum (current: " + quantum + ")");
            System.out.println("  4. Change aging limit (current: " + agingLimit + ")");
            System.out.println("  5. RUN the scheduler");
            System.out.println("  6. Load example tasks");
            System.out.println("  7. Clear all tasks");
            System.out.println("  0. Exit");
            System.out.print("Enter Choice : ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                addTask();
            } else if (choice.equals("2")) {
                viewTasks();
            } else if (choice.equals("3")) {
                System.out.print("New quantum value: ");
                quantum = readNumber();
                System.out.println("Quantum set to " + quantum);
            } else if (choice.equals("4")) {
                System.out.print("New aging limit: ");
                agingLimit = readNumber();
                System.out.println("Aging limit set to " + agingLimit);
            } else if (choice.equals("5")) {
                if (incoming.isEmpty()) {
                    System.out.println("No tasks added yet. Add tasks first (option 1 or 6).");
                } else {
                    runScheduler();
                }
            } else if (choice.equals("6")) {
                loadExampleTasks();
                System.out.println("Example tasks loaded. Choose option 5 to run.");
            } else if (choice.equals("7")) {
                incoming.clear();
                readyQueue.clear();
                ganttLog.clear();
                finished.clear();
                System.out.println("All tasks cleared.");
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid choice. Enter 0 to 7.");
            }
        }
        scanner.close();
    }

    //  ADD TASK — user types in a new task
    static void addTask() {
        System.out.println("--- Add New Task ---");

        System.out.print("Task name  : ");
        String name = scanner.nextLine().trim();

        System.out.print("Arrival time : ");
        int arrival = readNumber();

        System.out.print("Burst time (CPU needed): ");
        int burst = readNumber();

        System.out.print("Priority (1=urgent, 5=low): ");
        int priority = readNumber();

        if (priority < 1) priority = 1;
        if (priority > 5) priority = 5;

        incoming.add(new Task(name, arrival, burst, priority));
        System.out.println("Task '" + name + "' added!");
    }

    //  VIEW TASKS — show all tasks currently in the incoming list
    static void viewTasks() {
        if (incoming.isEmpty()) {
            System.out.println("No tasks added yet.");
            return;
        }
        System.out.println("Tasks waiting to be scheduled:");
        System.out.println("Name Arrival  Burst  Priority");
        for (int i = 0; i < incoming.size(); i++) {
            Task t = incoming.get(i);
            System.out.printf("%-10s  %5d    %4d     %d%n",
                    t.name, t.arrivalTime, t.burstTime, t.priority);
        }
    }
    //  LOAD EXAMPLE TASKS — so you can test without typing manually
    static void loadExampleTasks() {
        incoming.clear();
        incoming.add(new Task("Task-A",    0,      6,     3));
        incoming.add(new Task("Task-B",    1,      4,     1));
        incoming.add(new Task("Task-C",    2,      8,     5));
        incoming.add(new Task("Task-D",    4,      2,     2));
        incoming.add(new Task("Task-E",    6,      5,     4));
        System.out.println("5 example tasks loaded.");
    }


    //  RUN SCHEDULER — the main simulation
    static void runScheduler() {
        readyQueue.clear();
        ganttLog.clear();
        finished.clear();

        // Make a fresh copy of incoming so we don't destroy the original
        ArrayList<Task> taskPool = new ArrayList<>();
        for (int i = 0; i < incoming.size(); i++) {
            Task original = incoming.get(i);
            Task copy     = new Task(original.name,
                    original.arrivalTime,
                    original.burstTime,
                    original.priority);
            taskPool.add(copy);
        }
        // STEP 1 — sort taskPool by arrival time (bubble sort — easy to read)
        sortByArrival(taskPool);

        int clock          = 0;
        int totalTasks     = taskPool.size();

        System.out.println("--- Scheduler Running ---");
        System.out.println("Quantum : " + quantum);
        System.out.println("Aging limit: " + agingLimit + " (priority improves after waiting this long)");

        // STEP 3 — main loop: keep going until all tasks finish
        while (finished.size() < totalTasks) {

            for (int i = taskPool.size() - 1; i >= 0; i--) {
                Task t = taskPool.get(i);
                if (t.arrivalTime <= clock) {
                    readyQueue.add(t);
                    taskPool.remove(i);
                    System.out.println("  clock=" + clock
                            + " | ARRIVED: " + t.name
                            + " (priority=" + t.priority
                            + ", burst=" + t.burstTime + ")");
                }
            }

            if (readyQueue.isEmpty()) {
                if (!taskPool.isEmpty()) {
                    clock = taskPool.get(0).arrivalTime;
                    continue;
                } else {
                    break;
                }
            }

            for (int i = 0; i < readyQueue.size(); i++) {
                Task t = readyQueue.get(i);
                t.waitTime++;
                if (t.waitTime >= agingLimit && t.priority > 1) {
                    t.priority--;
                    t.waitTime = 0;
                    System.out.println("  clock=" + clock
                            + " | AGING: " + t.name
                            + " priority improved to " + t.priority);
                }
            }
            sortByPriority(readyQueue);

            Task current = readyQueue.remove(0);

            int timeSlice  = Math.min(quantum, current.remainingTime);
            int startTime  = clock;
            int endTime    = clock + timeSlice;

            current.remainingTime -= timeSlice;
            clock = endTime;

            String logEntry = "[" + startTime + "-" + endTime + "]  "
                    + current.name
                    + "  (remaining=" + current.remainingTime + ")";
            ganttLog.add(logEntry);
            System.out.println("  " + logEntry);

            if (current.remainingTime <= 0) {
                current.finishTime = clock;
                finished.add(current);
                System.out.println("  clock=" + clock
                        + " | FINISHED: " + current.name);
            } else {
                readyQueue.add(current);
            }
        }
        printGanttChart();
        printStatistics(totalTasks);
    }


    //  PRINT CHART — the execution timeline
    static void printGanttChart() {
        System.out.println("GANTT CHART");
        for (int i = 0; i < ganttLog.size(); i++) {
            System.out.println("  " + ganttLog.get(i));
        }
    }
    //  PRINT STATISTICS
    static void printStatistics(int totalTasks) {
        System.out.println("STATISTICS ");
        System.out.printf("  %-10s  %-8s  %-9s  %-8s  %-12s%n",
                "Task", "Arrival", "Burst", "Wait", "Turnaround");
        int totalWait        = 0;
        int totalTurnaround  = 0;

        sortByName(finished);

        for (int i = 0; i < finished.size(); i++) {
            Task t = finished.get(i);
            int turnaround = t.finishTime - t.arrivalTime;
            int wait       = turnaround - t.burstTime;
            totalWait       += wait;
            totalTurnaround += turnaround;

            System.out.printf("  %-10s  %-8d  %-9d  %-8d  %-12d%n",
                    t.name, t.arrivalTime, t.burstTime, wait, turnaround);
        }

        double avgWait = (double) totalWait  / totalTasks;
        double avgTurnaround = (double) totalTurnaround / totalTasks;

        System.out.printf("Average wait time  : %.2f%n", avgWait);
        System.out.printf("Average turnaround time : %.2f%n", avgTurnaround);
    }

    //  SORTING HELPERS
    static void sortByArrival(ArrayList<Task> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (list.get(j).arrivalTime > list.get(j + 1).arrivalTime) {
                    Task temp = list.get(j);
                    list.set(j,     list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // Sort by priority — lowest number first (most urgent)
    static void sortByPriority(ArrayList<Task> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (list.get(j).priority > list.get(j + 1).priority) {
                    Task temp = list.get(j);
                    list.set(j,     list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // Sort by name alphabetically — for clean stats output
    static void sortByName(ArrayList<Task> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                if (list.get(j).name.compareTo(list.get(j + 1).name) > 0) {
                    Task temp = list.get(j);
                    list.set(j,     list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    //  INPUT HELPER — reads a positive whole number safely
    static int readNumber() {
        while (true) {
            String input = scanner.nextLine().trim();
            boolean allDigits = true;
            if (input.isEmpty()) allDigits = false;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c < '0' || c > '9') {
                    allDigits = false;
                    break;
                }
            }
            if (!allDigits) {
                System.out.print("Please enter a whole number: ");
                continue;
            }
            int number = Integer.parseInt(input);
            if (number < 0) {
                System.out.print("Please enter a positive number: ");
                continue;
            }
            return number;
        }
    }
}

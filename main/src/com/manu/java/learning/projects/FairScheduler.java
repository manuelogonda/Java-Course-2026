package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.Scanner;

// ====================================================================
//  FAIR SCHEDULER — Round Robin + Priority + Aging
//
//  HOW IT WORKS IN ONE PARAGRAPH:
//  The scheduler keeps a clock ticking forward. Each tick it checks
//  if new tasks have arrived and adds them to a ready list. It picks
//  the highest priority task, runs it for one quantum (time slice),
//  logs it, then puts it back if not done. Tasks waiting too long get
//  their priority boosted (aging) so nobody waits forever. This runs
//  until every task is finished. Then it prints the timeline and stats.
// ====================================================================

public class FairScheduler {

    static Scanner scanner = new Scanner(System.in);

    // ================================================================
    //  TASK — one unit of work the CPU needs to run
    //
    //  DATA STRUCTURE: plain class with fields
    //  WHY: each task needs to carry 8 pieces of information together.
    //  Keeping them in one object makes it easy to pass around.
    // ================================================================
    static class Task {
        String name;           // e.g. "Task A"
        int    arrivalTime;    // when this task shows up
        int    burstTime;      // total CPU time it needs
        int    remainingTime;  // how much time is still left
        int    priority;       // lower number = more urgent (1 is most urgent)
        int    waitTime;       // tracks how long it has been sitting in queue
        int    finishTime;     // when it completed (filled in at the end)
        int    originalPriority; // saved so we can show it in stats

        Task(String name, int arrivalTime, int burstTime, int priority) {
            this.name             = name;
            this.arrivalTime      = arrivalTime;
            this.burstTime        = burstTime;
            this.remainingTime    = burstTime;  // starts equal to burstTime
            this.priority         = priority;
            this.originalPriority = priority;
            this.waitTime         = 0;
            this.finishTime       = 0;
        }
    }


    // ================================================================
    //  DATA STRUCTURE: ArrayList<Task>  — incoming tasks
    //
    //  WHY ArrayList?
    //  All tasks that will ever arrive are stored here first.
    //  Every clock tick we check: has any task in this list arrived yet?
    //  If yes, move it to the ready list.
    //  ArrayList lets us add, loop, and remove easily.
    // ================================================================
    static ArrayList<Task> incoming = new ArrayList<>();


    // ================================================================
    //  DATA STRUCTURE: ArrayList<Task>  — ready queue
    //
    //  WHY ArrayList and not PriorityQueue?
    //  PriorityQueue in Java does NOT re-sort when you change a value
    //  inside it. Since aging CHANGES the priority of tasks that are
    //  already inside, we need to re-sort manually after aging.
    //  With ArrayList we can just call our own sortByPriority() method
    //  after every aging step. Simple and easy to understand.
    // ================================================================
    static ArrayList<Task> readyQueue = new ArrayList<>();


    // ================================================================
    //  DATA STRUCTURE: ArrayList<String>  — Gantt log
    //
    //  WHY ArrayList?
    //  Every time a task runs we add one line like "[0-2] Task A".
    //  We keep adding until the simulation ends, then print everything.
    //  Size is unknown upfront so ArrayList is perfect.
    // ================================================================
    static ArrayList<String> ganttLog = new ArrayList<>();


    // ================================================================
    //  DATA STRUCTURE: ArrayList<Task>  — finished tasks
    //
    //  WHY ArrayList?
    //  When a task finishes we move it here to calculate stats later.
    //  We need to loop through all finished tasks at the end.
    // ================================================================
    static ArrayList<Task> finished = new ArrayList<>();


    // ================================================================
    //  SETTINGS
    // ================================================================
    static int quantum      = 2;   // time slice each task gets per turn
    static int agingLimit   = 5;   // if a task waits this long, boost priority


    // ================================================================
    //  MAIN MENU
    // ================================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     FAIR SCHEDULER SIMULATOR     ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean running = true;

        while (running) {
            System.out.println("\n========= MENU =========");
            System.out.println("  1. Add a task");
            System.out.println("  2. View tasks added so far");
            System.out.println("  3. Change quantum (current: " + quantum + ")");
            System.out.println("  4. Change aging limit (current: " + agingLimit + ")");
            System.out.println("  5. RUN the scheduler");
            System.out.println("  6. Load example tasks");
            System.out.println("  7. Clear all tasks");
            System.out.println("  0. Exit");
            System.out.print("\nChoice: ");

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


    // ================================================================
    //  ADD TASK — user types in a new task
    // ================================================================
    static void addTask() {
        System.out.println("\n--- Add New Task ---");

        System.out.print("Task name          : ");
        String name = scanner.nextLine().trim();

        System.out.print("Arrival time       : ");
        int arrival = readNumber();

        System.out.print("Burst time (CPU needed): ");
        int burst = readNumber();

        System.out.print("Priority (1=urgent, 5=low): ");
        int priority = readNumber();

        // Clamp priority between 1 and 5
        if (priority < 1) priority = 1;
        if (priority > 5) priority = 5;

        incoming.add(new Task(name, arrival, burst, priority));
        System.out.println("Task '" + name + "' added!");
    }


    // ================================================================
    //  VIEW TASKS — show all tasks currently in the incoming list
    // ================================================================
    static void viewTasks() {
        if (incoming.isEmpty()) {
            System.out.println("No tasks added yet.");
            return;
        }
        System.out.println("\nTasks waiting to be scheduled:");
        System.out.println("  Name        Arrival  Burst  Priority");
        System.out.println("  ----------------------------------------");
        for (int i = 0; i < incoming.size(); i++) {
            Task t = incoming.get(i);
            System.out.printf("  %-10s  %5d    %4d     %d%n",
                    t.name, t.arrivalTime, t.burstTime, t.priority);
        }
    }


    // ================================================================
    //  LOAD EXAMPLE TASKS — so you can test without typing manually
    // ================================================================
    static void loadExampleTasks() {
        incoming.clear();
        //                  name        arrival  burst  priority
        incoming.add(new Task("Task-A",    0,      6,     3));
        incoming.add(new Task("Task-B",    1,      4,     1));
        incoming.add(new Task("Task-C",    2,      8,     5));
        incoming.add(new Task("Task-D",    4,      2,     2));
        incoming.add(new Task("Task-E",    6,      5,     4));
        System.out.println("5 example tasks loaded.");
    }


    // ================================================================
    //  RUN SCHEDULER — the main simulation
    //
    //  STEP BY STEP:
    //  1. Sort incoming tasks by arrival time
    //  2. Start clock at 0
    //  3. Loop until all tasks are done:
    //     a. Move newly arrived tasks into ready queue
    //     b. Apply aging to waiting tasks
    //     c. Sort ready queue by priority
    //     d. Pick the top task
    //     e. Run it for one quantum (or less if it finishes)
    //     f. Log it, check if finished or put back
    //  4. Print Gantt chart and statistics
    // ================================================================
    static void runScheduler() {

        // Reset state from any previous run
        readyQueue.clear();
        ganttLog.clear();
        finished.clear();

        // Make a fresh copy of incoming so we don't destroy the original
        // This lets you run the same tasks multiple times
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

        System.out.println("\n--- Scheduler Running ---");
        System.out.println("Quantum    : " + quantum);
        System.out.println("Aging limit: " + agingLimit + " (priority improves after waiting this long)");
        System.out.println();

        // STEP 3 — main loop: keep going until all tasks finish
        while (finished.size() < totalTasks) {

            // STEP 3a — check arrivals
            // Move any task from taskPool whose arrivalTime <= clock
            // into the readyQueue
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

            // STEP 3b — if nothing is ready, fast-forward clock
            // to when the next task arrives
            if (readyQueue.isEmpty()) {
                if (!taskPool.isEmpty()) {
                    clock = taskPool.get(0).arrivalTime;
                    continue; // go back to top of loop to check arrivals
                } else {
                    break; // no tasks left at all
                }
            }

            // STEP 3c — apply aging
            // Any task that has waited >= agingLimit gets priority boosted by 1
            // Priority 1 is the best so we never go below 1
            for (int i = 0; i < readyQueue.size(); i++) {
                Task t = readyQueue.get(i);
                t.waitTime++;
                if (t.waitTime >= agingLimit && t.priority > 1) {
                    t.priority--;
                    t.waitTime = 0; // reset wait timer after each boost
                    System.out.println("  clock=" + clock
                            + " | AGING: " + t.name
                            + " priority improved to " + t.priority);
                }
            }

            // STEP 3d — sort ready queue by priority (lowest number = first)
            sortByPriority(readyQueue);

            // STEP 3e — pick the top task (index 0 = highest priority)
            Task current = readyQueue.remove(0);

            // STEP 3f — run it for one quantum (or less if it finishes sooner)
            int timeSlice  = Math.min(quantum, current.remainingTime);
            int startTime  = clock;
            int endTime    = clock + timeSlice;

            current.remainingTime -= timeSlice;
            clock = endTime;

            // Log this execution slice to the Gantt chart
            String logEntry = "[" + startTime + "-" + endTime + "]  "
                    + current.name
                    + "  (remaining=" + current.remainingTime + ")";
            ganttLog.add(logEntry);
            System.out.println("  " + logEntry);

            // STEP 3g — did this task finish?
            if (current.remainingTime <= 0) {
                current.finishTime = clock;
                finished.add(current);
                System.out.println("  clock=" + clock
                        + " | FINISHED: " + current.name);
            } else {
                // Not done — put it back in the ready queue
                readyQueue.add(current);
            }
        }

        // STEP 4 — print results
        printGanttChart();
        printStatistics(totalTasks);
    }


    // ================================================================
    //  PRINT GANTT CHART — the execution timeline
    // ================================================================
    static void printGanttChart() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║         GANTT CHART              ║");
        System.out.println("╚══════════════════════════════════╝");
        for (int i = 0; i < ganttLog.size(); i++) {
            System.out.println("  " + ganttLog.get(i));
        }
    }


    // ================================================================
    //  PRINT STATISTICS
    //
    //  Wait time       = finishTime - arrivalTime - burstTime
    //  Turnaround time = finishTime - arrivalTime
    //
    //  Wait time is how long the task sat doing NOTHING (not running).
    //  Turnaround is total time from arrival to completion.
    // ================================================================
    static void printStatistics(int totalTasks) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                   STATISTICS                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.printf("  %-10s  %-8s  %-9s  %-8s  %-12s%n",
                "Task", "Arrival", "Burst", "Wait", "Turnaround");
        System.out.println("  -------------------------------------------------------");

        int totalWait        = 0;
        int totalTurnaround  = 0;

        // Sort finished list by name for clean output
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

        System.out.println("  -------------------------------------------------------");

        double avgWait       = (double) totalWait       / totalTasks;
        double avgTurnaround = (double) totalTurnaround / totalTasks;

        System.out.printf("  Average wait time       : %.2f%n", avgWait);
        System.out.printf("  Average turnaround time : %.2f%n", avgTurnaround);
        System.out.println();
    }


    // ================================================================
    //  SORTING HELPERS
    //  Using bubble sort — simple to read and understand
    //  Not the fastest but easy to follow step by step
    // ================================================================

    // Sort by arrival time — smallest arrival first
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


    // ================================================================
    //  INPUT HELPER — reads a positive whole number safely
    // ================================================================
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

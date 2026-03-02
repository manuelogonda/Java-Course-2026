package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Scanner;

public class TransactionEngine {

    private static final Scanner sc = new Scanner(System.in);

    // Records ONE change to ONE account.
    static class LogEntry {
        String accountId;
        int    oldBalance;
        int    newBalance;
        String operationType;

        LogEntry(String accountId,
                 int    oldBalance,
                 int    newBalance,
                 String operationType) {
            this.accountId     = accountId;
            this.oldBalance    = oldBalance;
            this.newBalance    = newBalance;
            this.operationType = operationType;
        }

        @Override
        public String toString() {
            return operationType
                    + " on [" + accountId + "]"
                    + " | " + oldBalance
                    + "  " + newBalance;
        }
    }

    // TRANSACTION
    static class Transaction {
        List<LogEntry> logEntries  = new ArrayList<>();
        int            transactionId;

        Transaction(int transactionId) {
            this.transactionId = transactionId;
        }

        void addLogEntry(LogEntry entry) {
            logEntries.add(entry);
        }

        @Override
        public String toString() {
            return "Transaction#" + transactionId
                    + " [" + logEntries.size() + " operations]";
        }
    }

    // BANK STATE
    private final Map<String, Integer> accounts  = new HashMap<>();
    private final Stack<Transaction>  transactionStack = new Stack<>();
    private final List<String>  auditLog  = new ArrayList<>();
    private int    nextTransactionId = 1;

    // CREATE ACCOUNT
    public void createAccount(String accountId, int initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative for account: " + accountId);
        }
        if (accounts.containsKey(accountId)) {
            throw new IllegalArgumentException(
                    "Account already exists: " + accountId);
        }
        accounts.put(accountId, initialBalance);
        auditLog.add("CREATED account [" + accountId + "] balance=" + initialBalance);
        System.out.println("Congrats created account [" + accountId + "] balance = " + initialBalance);
    }

    // BEGIN TRANSACTION
    public void beginTransaction() {
        Transaction newTransaction = new Transaction(nextTransactionId++);
        transactionStack.push(newTransaction);
        System.out.println(" BEGIN " + newTransaction
                + " | depth = " + transactionStack.size());
    }

    // DEPOSIT
    public void deposit(String accountId, int amount) {
        validateAccountExists(accountId);
        validatePositiveAmount(amount);

        int oldBalance = accounts.get(accountId);
        int newBalance = oldBalance + amount;
        applyChange(accountId, oldBalance, newBalance, "DEPOSIT");
    }

    // WITHDRAW
    public void withdraw(String accountId, int amount) {
        validateAccountExists(accountId);
        validatePositiveAmount(amount);

        int oldBalance = accounts.get(accountId);

        if (oldBalance < amount) {
            throw new IllegalStateException(
                    "Insufficient funds in [" + accountId + "]"
                            + " | balance = " + oldBalance
                            + " | requested = " + amount);
        }

        int newBalance = oldBalance - amount;
        applyChange(accountId, oldBalance, newBalance, "WITHDRAW");
    }

    // TRANSFER — atomic
    public void transfer(String fromAccountId, String toAccountId, int amount) {
        validateAccountExists(fromAccountId);
        validateAccountExists(toAccountId);
        validatePositiveAmount(amount);

        System.out.println("  TRANSFER " + amount
                + " from [" + fromAccountId + "] to [" + toAccountId + "]");

        beginTransaction();

        try {
            withdraw(fromAccountId, amount);
            deposit(toAccountId, amount);
            commit();
            System.out.println("  TRANSFER successful");

        } catch (Exception transferFailed) {
            rollback();
            throw new IllegalStateException(
                    "TRANSFER FAILED and was rolled back: "
                            + transferFailed.getMessage());
        }
    }

    // COMMIT
    public void commit() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("Cannot commit — no active transaction");
        }

        Transaction completedTransaction = transactionStack.pop();
        System.out.println("COMMIT " + completedTransaction);

        if (transactionStack.isEmpty()) {
            for (LogEntry entry : completedTransaction.logEntries) {
                auditLog.add("COMMITTED: " + entry);
            }
            System.out.println("All changes committed permanently");
        } else {
            Transaction parentTransaction = transactionStack.peek();
            parentTransaction.logEntries.addAll(completedTransaction.logEntries);
            System.out.println("  Merged into parent " + parentTransaction
                    + " | depth now=" + transactionStack.size());
        }
    }

    // ROLLBACK
    public void rollback() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("Cannot rollback — no active transaction");
        }

        Transaction transactionToUndo = transactionStack.pop();
        System.out.println("  ROLLBACK " + transactionToUndo);

        List<LogEntry> entries = transactionToUndo.logEntries;

        // Undo in REVERSE
        for (int i = entries.size() - 1; i >= 0; i--) {
            LogEntry entry = entries.get(i);
            accounts.put(entry.accountId, entry.oldBalance);
            System.out.println("    UNDID: " + entry + " | restored to " + entry.oldBalance);
        }

        auditLog.add("ROLLED BACK: " + entries.size()
                + " operations in " + transactionToUndo);
        System.out.println("  Rollback complete — " + entries.size() + " operations undone");
    }

    // APPLY CHANGE
    private void applyChange(String accountId,
                             int    oldBalance,
                             int    newBalance,
                             String operationType) {
        accounts.put(accountId, newBalance);

        LogEntry entry = new LogEntry(accountId, oldBalance, newBalance, operationType);

        if (!transactionStack.isEmpty()) {
            transactionStack.peek().addLogEntry(entry);
            System.out.println("    " + entry
                    + " [logged in " + transactionStack.peek()
                    + " depth=" + transactionStack.size() + "]");
        } else {
            auditLog.add("IMMEDIATE: " + entry);
            System.out.println("    " + entry + " [immediate commit]");
        }
    }

    // VALIDATION HELPERS
    private void validateAccountExists(String accountId) {
        if (!accounts.containsKey(accountId)) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
    }

    private void validatePositiveAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive, got: " + amount);
        }
    }

    // PRINT HELPERS
    public void printBalances() {
        System.out.println("Current Balances");
        if (accounts.isEmpty()) {
            System.out.println("(no accounts)");
        }
        for (Map.Entry<String, Integer> entry : accounts.entrySet()) {
            System.out.println("  [" + entry.getKey() + "] KES " + entry.getValue());
        }
        System.out.println();
    }

    public void printAuditLog() {
        System.out.println(" Audit Log ");
        if (auditLog.isEmpty()) {
            System.out.println("  (empty)");
        }
        for (int i = 0; i < auditLog.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + auditLog.get(i));
        }
        System.out.println();
    }

    // Shows which accounts exist
    public void printAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("  No accounts created yet.");
            return;
        }
        System.out.println(" Existing accounts:");
        for (Map.Entry<String, Integer> entry : accounts.entrySet()) {
            System.out.println(" [" + entry.getKey() + "] KES " + entry.getValue());
        }
    }

    // Shows current transaction stack depth
    public void printTransactionStatus() {
        if (transactionStack.isEmpty()) {
            System.out.println("  No active transaction.");
        } else {
            System.out.println("  Active transaction depth: " + transactionStack.size());
            System.out.println("  Current: " + transactionStack.peek());
        }
    }

    // Read a non-empty string from the user
    private static String readString(String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("  Input cannot be empty. Please try again.");
            }
        }
        return input;
    }

    // Read a positive integer from the user
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();

            // Manual digit check
            if (raw.isEmpty()) {
                System.out.println("  Please enter a number.");
                continue;
            }

            boolean allDigits = true;
            for (int i = 0; i < raw.length(); i++) {
                char c = raw.charAt(i);
                if (c < '0' || c > '9') {
                    allDigits = false;
                    break;
                }
            }

            if (!allDigits) {
                System.out.println("  '" + raw + "' is not a valid number. Try again.");
                continue;
            }

            try {
                int value = Integer.parseInt(raw);
                return value;
            } catch (NumberFormatException overflow) {
                System.out.println("  Number too large. Please enter a smaller value.");
            }
        }
    }

    // MAIN MENU
    public static void main(String[] args) {

        TransactionEngine bank = new TransactionEngine();

        System.out.println("Type menu numbers to navigate ");

        boolean running = true;

        while (running) {
            printMainMenu(bank);
            String choice = sc.nextLine().trim();

            switch (choice) {

                // ACCOUNT MANAGEMENT
                case "1":
                    handleCreateAccount(bank);
                    break;

                case "2":
                    bank.printBalances();
                    break;

                //  OPERATIONS
                case "3":
                    handleDeposit(bank);
                    break;

                case "4":
                    handleWithdraw(bank);
                    break;

                case "5":
                    handleTransfer(bank);
                    break;

                // TRANSACTION CONTROL
                case "6":
                    bank.beginTransaction();
                    break;

                case "7":
                    try {
                        bank.commit();
                    } catch (IllegalStateException e) {
                        System.out.println(" x " + e.getMessage());
                    }
                    break;

                case "8":
                    try {
                        bank.rollback();
                    } catch (IllegalStateException e) {
                        System.out.println("  x " + e.getMessage());
                    }
                    break;

                // INFO
                case "9":
                    bank.printAuditLog();
                    break;

                case "10":
                    bank.printTransactionStatus();
                    break;

                // EXIT
                case "0":
                    System.out.println(" Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Enter a number from the menu.");
            }
        }
        sc.close();
    }

    // MAIN MENU DISPLAY
    private static void printMainMenu(TransactionEngine bank) {
        System.out.println("MAIN MENU");
        System.out.println("Account Management");
        System.out.println("1. Create Account");
        System.out.println("2. View Balances");
        System.out.println("Operations include ");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5.Transfer");
        System.out.println("Transaction Control");
        System.out.println("6. Begin Transaction");
        System.out.println("7. Commit");
        System.out.println("8. Rollback");
        System.out.println("Info");
        System.out.println("9. View Audit Log");
        System.out.println("10. Transaction Status");
        System.out.println("0. Exit");

        // Show a quick status line so user knows context
        if (!bank.transactionStack.isEmpty()) {
            System.out.println(" Active transaction — depth "
                    + bank.transactionStack.size());
        }

        System.out.print("Enter choice: ");
    }

    // HANDLER METHODS — read input then call engine
    private static void handleCreateAccount(TransactionEngine bank) {
        System.out.println(" Create Account");
        bank.printAccounts();

        String id = readString("  Account ID (e.g. Alice, ACC001): ");
        int balance = readInt(   "  Initial balance (KES): ");

        try {
            bank.createAccount(id, balance);
        } catch (IllegalArgumentException e) {
            System.out.println(" x " + e.getMessage());
        }
    }

    private static void handleDeposit(TransactionEngine bank) {
        System.out.println("Deposit");
        bank.printAccounts();

        if (bank.accounts.isEmpty()) return;

        String id   = readString("  Account ID to deposit into: ");
        int amount  = readInt(   "  Amount to deposit (KES): ");

        try {
            bank.deposit(id, amount);
        } catch (IllegalArgumentException e) {
            System.out.println(" x " + e.getMessage());
        }
    }

    private static void handleWithdraw(TransactionEngine bank) {
        System.out.println(" Withdraw ");
        bank.printAccounts();

        if (bank.accounts.isEmpty()) return;

        String id   = readString("  Account ID to withdraw from: ");
        int amount  = readInt(   "  Amount to withdraw (KES): ");

        try {
            bank.withdraw(id, amount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(" x " + e.getMessage());
        }
    }

    private static void handleTransfer(TransactionEngine bank) {
        System.out.println(" Transfer");
        bank.printAccounts();

        if (bank.accounts.size() < 2) {
            System.out.println(" Need at least 2 accounts to transfer.");
            return;
        }

        String from = readString("  From account ID: ");
        String to   = readString("  To account ID: ");
        int amount  = readInt(   "  Amount to transfer (KES): ");

        try {
            bank.transfer(from, to, amount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  x " + e.getMessage());
        }
    }
}
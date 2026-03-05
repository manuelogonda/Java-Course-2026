package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Scanner;
import java.util.Random;

//  BANK — Transaction Engine
public class TransactionEngine {
    private static final Scanner sc     = new Scanner(System.in);
    private static final Random  random = new Random();
    private static final int MAX_PIN_TRIES = 3;
//  STEP 1 : Account
    static class Account {

        String  accountNumber;
        String  name;
        int     balance;
        String  pin;
        int     wrongPinCount;
        boolean isLocked;

        // This is the constructor — it runs when we create a new Account
        Account(String accountNumber, String name, int balance, String pin) {
            this.accountNumber = accountNumber;
            this.name   = name;
            this.balance = balance;
            this.pin  = pin;
            this.wrongPinCount = 0;
            this.isLocked  = false;
        }
    }


    //  STEP 2 LOG ENTRY
    //  Every time a balance changes we save a record of it.
    static class LogEntry {
        String accountNumber;
        int    balanceBefore;
        int    balanceAfter;
        String operation;

        LogEntry(String accountNumber, int balanceBefore,
                 int balanceAfter, String operation) {
            this.accountNumber = accountNumber;
            this.balanceBefore = balanceBefore;
            this.balanceAfter  = balanceAfter;
            this.operation     = operation;
        }
    }


    //  STEP 3 —  TRANSACTION
    //  Either ALL of them succeed, or NONE of them happen.
    static class Transaction {

        int id;
        List<LogEntry> logEntries;

        Transaction(int id) {
            this.id = id;
            this.logEntries = new ArrayList<>();
        }

        // Add a record of one change to this transaction
        void addChange(LogEntry entry) {
            logEntries.add(entry);
        }
    }


    //  STEP 4 — THE BANK'S DATA
    private Map<String, Account> accounts = new HashMap<>();

    // Stack take from the top
    private Stack<Transaction> openTransactions = new Stack<>();

    // A permanent history of everything that happened
    private List<String> auditLog = new ArrayList<>();

    // Simple counter so each transaction gets a unique number
    private int nextTransactionNumber = 1;

    //  ACCOUNT CREATION
    private String makeAccountNumber() {
        String number;
        do {
            int digits = random.nextInt(9000) + 1000;
            number = String.valueOf(digits);
        } while (accounts.containsKey(number));
        return number;
    }

    // Checks if a pin is exactly 4 digits and nothing else
    private boolean isPinValid(String pin) {
        if (pin == null || pin.length() != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            char c = pin.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // Creates a new account and saves it to the bank
    public void createAccount(String name, int openingBalance, String pin) {

        // Check the inputs before doing anything
        if (openingBalance < 0) {
            System.out.println("Opening balance MUST be more than zero.");
            return;
        }
        if (!isPinValid(pin)) {
            System.out.println("PIN MUST be exactly 4 digits.");
            return;
        }
        // Generate the account number and build the account
        String  number  = makeAccountNumber();
        Account account = new Account(number, name.trim(), openingBalance, pin);

        // Save the account
        accounts.put(number, account);
        auditLog.add("ACCOUNT CREATED FOR : " + name + " (" + number + ")");

        // Show the account details to the user
        System.out.println("Account created successfully!");
        System.out.println("Account Number : " + number );
        System.out.println("Name : " + name.trim().toLowerCase());
        System.out.println("Opening Balance: KES " + openingBalance);
        System.out.println("PIN : **** ");
        System.out.println("IMPORTANT: Write down your number: " + number);
        System.out.println("You will need it for every transaction.");
    }

    // Finds an account by number, returns null if not found
    private Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Checks if the entered PIN matches the account PIN
    private boolean checkPin(Account account, String enteredPin) {

        // If the account is already locked, stop immediately
        if (account.isLocked) {
            System.out.println("Account [" + account.accountNumber + "] is LOCKED.");
            System.out.println("Contact the bank to unlock it.");
            return false;
        }

        // Check if the entered PIN matches
        if (account.pin.equals(enteredPin)) {
            account.wrongPinCount = 0;
            return true;
        }

        // Wrong PIN — increase the counter
        account.wrongPinCount++;
        int triesLeft = MAX_PIN_TRIES - account.wrongPinCount;

        // Check if they have used up all their tries
        if (account.wrongPinCount >= MAX_PIN_TRIES) {
            account.isLocked = true;
            auditLog.add("ACCOUNT LOCKED: " + account.accountNumber
                    + "  too many wrong PINs");
            System.out.println("Wrong PIN. Account is now LOCKED.");
            System.out.println("Use option 11 to reset your PIN.");
        } else {
            System.out.println("Wrong PIN. You have " + triesLeft + " out of " + MAX_PIN_TRIES + "tries left.");
        }

        return false;
    }


    //  PIN RESET
    public void resetPin(String accountNumber, String enteredName,
                         int enteredBalance, String newPin) {
        // Step 1 — does the account exist?
        Account account = findAccount(accountNumber);
        if (account == null) {
            System.out.println("  Account not found: " + accountNumber);
            return;
        }
        // Step 2 — is it actually locked?
        if (!account.isLocked) {
            System.out.println("  Account [" + accountNumber + "] is not locked.");
            System.out.println("  PIN reset is only for locked accounts.");
            return;
        }
        // Step 3 — verify identity using name + balance
        // We compare names case-insensitively .
        boolean nameMatches    = account.name.equalsIgnoreCase(enteredName.trim());
        boolean balanceMatches = account.balance == enteredBalance;

        if (!nameMatches || !balanceMatches) {
            auditLog.add("FAILED PIN RESET attempt on [" + accountNumber + "]");
            System.out.println("Identity verification failed.");
            System.out.println("Name or balance did not match our records.");
            return;
        }
        // Step 4 — validate the new PIN format
        if (!isPinValid(newPin)) {
            System.out.println("New PIN must be exactly 4 digits.");
            return;
        }
        // Step 5 — make sure the new PIN is different from the old one.
        if (account.pin.equals(newPin)) {
            System.out.println("New PIN must be different from your old PIN.");
            return;
        }
        // Step 6 — all checks passed, apply the reset
        account.pin = newPin;
        account.isLocked = false;
        account.wrongPinCount = 0;

        auditLog.add("PIN RESET and UNLOCKED for ACC - : [" + accountNumber + "]");
        System.out.println("PIN reset successful!");
        System.out.println("Account [" + accountNumber + "] is now unlocked.");
        System.out.println("You can log in with your new PIN.");
    }


    //  DEPOSITS AND WITHDRAWALS
    public void deposit(String accountNumber, int amount) {

        Account account = findAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return;
        }
        if (amount <= 0) {
            System.out.println("Deposit amount must be more than zero.");
            return;
        }

        // Save the old balance before we change it
        int oldBalance = account.balance;
        int newBalance = oldBalance + amount;

        account.balance = newBalance;

        recordChange(account, oldBalance, newBalance, "DEPOSIT");
        System.out.println("Deposit done." + "Amount deposited was "  + amount + ", New balance now is : KES " + newBalance);
    }

    public void withdraw(String accountNumber, int amount) {

        Account account = findAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return;
        }
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be more than zero.");
            return;
        }
        if (account.balance < amount) {
            System.out.println("You have insufficient funds.");
            System.out.println("Balance: KES " + account.balance
                    + "  Requested: KES " + amount);
            return;
        }

        int oldBalance = account.balance;
        int newBalance = oldBalance - amount;

        account.balance = newBalance;
        recordChange(account, oldBalance, newBalance, "WITHDRAW");

        System.out.println("Withdrawal done. Amount withdrawn is "+ amount + " New balance is : KES " + newBalance);
    }

    // Records ONE balance change
    private void recordChange(Account account, int oldBalance,
                              int newBalance, String operation) {

        LogEntry change = new LogEntry(
                account.accountNumber, oldBalance, newBalance, operation);

        if (openTransactions.isEmpty()) {
            // No open transaction — commit immediately and permanently
            auditLog.add("IMMEDIATE " + operation + " on ["
                    + account.accountNumber + "] "
                    + oldBalance + " is " + newBalance);
        } else {
            // Inside a transaction — save it so we can undo if needed
            openTransactions.peek().addChange(change);
            System.out.println("  (saved in transaction "
                    + openTransactions.peek().id + ")");
        }
    }


    //  TRANSACTIONS — BEGIN, COMMIT, ROLLBACK
    // Opens a new transaction
    public void beginTransaction() {
        Transaction t = new Transaction(nextTransactionNumber++);
        openTransactions.push(t);
        System.out.println("  Transaction #" + t.id + " started.");
        System.out.println("  Depth: " + openTransactions.size());
    }

    // Makes all changes in the current transaction permanent
    public void commit() {
        if (openTransactions.isEmpty()) {
            System.out.println("Nothing to commit. No open transaction.");
            return;
        }
        // Take the top transaction off the stack
        Transaction done = openTransactions.pop();

        System.out.println("Committing transaction #" + done.id + "...");

        if (openTransactions.isEmpty()) {
            // All changes are now permanent — write to audit log
            for (LogEntry change : done.logEntries) {
                auditLog.add("COMMITTED: " + change.operation
                        + " [" + change.accountNumber + "] "
                        + change.balanceBefore + " to " + change.balanceAfter);
            }
            System.out.println("Done. " + done.logEntries.size()
                    + " change(s) are now permanent.");
        } else {
            // The parent will decide when to truly commit
            Transaction parent = openTransactions.peek();
            for (LogEntry change : done.logEntries) {
                parent.addChange(change);
            }
            System.out.println("Changes moved into parent transaction #"
                    + parent.id);
        }
    }

    // Undoes ALL changes in the current transaction
    public void rollback() {

        if (openTransactions.isEmpty()) {
            System.out.println("Nothing to rollback. No open transaction.");
            return;
        }

        // Take the top transaction off the stack
        Transaction undo = openTransactions.pop();

        System.out.println("Rolling back transaction #" + undo.id + "...");

        // We undo changes in REVERSE ORDER
        List<LogEntry> changes = undo.logEntries;
        for (int i = changes.size() - 1; i >= 0; i--) {
            LogEntry change  = changes.get(i);
            Account  account = findAccount(change.accountNumber);
            if (account != null) {
                account.balance = change.balanceBefore;
                System.out.println("  Undid " + change.operation
                        + " on [" + change.accountNumber + "]"
                        + " — restored to KES " + change.balanceBefore);
            }
        }

        auditLog.add("ROLLED BACK transaction #" + undo.id
                + " — " + changes.size() + " change(s) undone");
        System.out.println("  Rollback done. "
                + changes.size() + " change(s) reversed.");
    }

    //  Transfer is ATOMIC — all or nothing.
    public void transfer(String fromNumber, String toNumber, int amount) {

        // Check both accounts exist BEFORE starting
        Account from = findAccount(fromNumber);
        Account to   = findAccount(toNumber);

        if (from == null) {
            System.out.println("Sending account not found: " + fromNumber);
            return;
        }
        if (to == null) {
            System.out.println("Receiving account not found: " + toNumber);
            return;
        }
        if (amount <= 0) {
            System.out.println("Transfer amount must be more than zero.");
            return;
        }
        if (from.balance < amount) {
            System.out.println("Not enough money to transfer.");
            System.out.println("Your account balance is : KES " + from.balance
                    + " you needed to transfer : KES " + amount);
            return;
        }

        System.out.println(" Transferring KES " + amount
                + " from " + from.name + " to " + to.name + "...");
        // Open a transaction so we can undo both steps if needed
        beginTransaction();

        withdraw(fromNumber, amount);
        deposit(toNumber, amount);
        commit();

        System.out.println("Transfer successful! From " + from.name + " to " +  to.name);
    }

    //  PRINT HELPERS display information to the user
    public void showMyAccount(Account account) {
        System.out.println("Your Account");
        System.out.println( "Account Number : " + account.accountNumber);
        System.out.println("Name : " +  account.name);
        System.out.println("Balance : " + account.balance);

        String status;
        if (account.isLocked) {
            status = "LOCKED";
        } else if (account.wrongPinCount > 0) {
            status = "Warning — " + account.wrongPinCount + " wrong PIN(s)";
        } else {
            status = "Active";
        }
        System.out.printf("Status" + status);
    }

    public void showAccountList() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.");
            return;
        }
        System.out.println("Accounts:");
        for (Account acc : accounts.values()) {
            System.out.println(" -> " + acc.accountNumber
                    + "   " + acc.name
                    + (acc.isLocked ? "  [LOCKED]" : ""));
        }
    }

    public void showAuditLog() {
        System.out.println("Audit Log");
        if (auditLog.isEmpty()) {
            System.out.println("  (empty)");
            System.out.println(); return;
        }
        for (int i = 0; i < auditLog.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + auditLog.get(i));
        }
        System.out.println();
    }

    public void showTransactionStatus() {
        if (openTransactions.isEmpty()) {
            System.out.println("  No open transactions.");
        } else {
            System.out.println("Open transactions: "
                    + openTransactions.size());
            System.out.println("  Current: Transaction #"
                    + openTransactions.peek().id
                    + " with " + openTransactions.peek().logEntries.size()
                    + " change(s)");
        }
    }

    //  INPUT HELPERS — read from the keyboard safely

    private static String readText(String question) {
        String answer = "";
        while (answer.isEmpty()) {
            System.out.print(question);
            answer = sc.nextLine().trim();
            if (answer.isEmpty()) {
                System.out.println("Please type something.");
            }
        }
        return answer;
    }

    // Reads a whole number (no letters, no decimals)
    private static int readNumber(String question) {
        while (true) {
            System.out.print(question);
            String typed = sc.nextLine().trim();

            // Empty check
            if (typed.isEmpty()) {
                System.out.println("Please enter a number.");
                continue;
            }

            // Make sure every character is a digit
            boolean allDigits = true;
            for (int i = 0; i < typed.length(); i++) {
                char c = typed.charAt(i);
                if (c < '0' || c > '9') {
                    allDigits = false;
                    break;
                }
            }

            if (!allDigits) {
                System.out.println("That is not a number. Please try again.");
                continue;
            }

            // Parse it — catch only if the number is too big for int
            try {
                return Integer.parseInt(typed);
            } catch (NumberFormatException e) {
                System.out.println("  Number is too large. Try a smaller value.");
            }
        }
    }

    // Reads a PIN — hides typing on a real terminal
    private static String readPin(String question) {
        while (true) {
            System.out.print(question);

            String pin;
            java.io.Console console = System.console();
            if (console != null) {
                pin = new String(console.readPassword());
            } else {
                pin = sc.nextLine().trim();
            }

            if (pin.length() != 4) {
                System.out.println("  PIN must be exactly 4 digits. Try again.");
                continue;
            }

            boolean allDigits = true;
            for (int i = 0; i < 4; i++) {
                char c = pin.charAt(i);
                if (c < '0' || c > '9') { allDigits = false; break; }
            }

            if (!allDigits) {
                System.out.println("  PIN must contain only digits. Try again.");
                continue;
            }

            return pin;
        }
    }

    // Asks for the PIN twice and makes sure they match
    private static String readAndConfirmPin() {
        while (true) {
            String first  = readPin("  Choose a 4-digit PIN : ");
            String second = readPin("  Confirm your PIN  : ");

            if (first.equals(second)) {
                System.out.println("  PIN set successfully.");
                System.out.println();
                return first;
            }

            System.out.println("  PINs did not match. Please try again.");

        }
    }




    //  MAIN — the program starts here
    static void main(String[] args) {

        TransactionEngine bank = new TransactionEngine();

        System.out.println("Transaction Engine ");
        boolean running = true;
        while (running) {
            showMenu(bank);
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> handleCreateAccount(bank);
                case "2" -> handleViewMyAccount(bank);
                case "3" -> handleDeposit(bank);
                case "4" -> handleWithdraw(bank);
                case "5" -> handleTransfer(bank);
                case "6" -> handleBeginTransaction(bank);
                case "7" -> bank.commit();
                case "8" -> bank.rollback();
                case "9" -> bank.showAuditLog();
                case "10" -> bank.showTransactionStatus();
                case "11" ->
                        handleResetPin(bank);
                case "0" -> {
                    System.out.println("Thank you are welcome next time");
                    running = false;
                }
                default -> System.out.println("That is not a valid choice. Try again.");
            }
        }

        sc.close();
    }


    //  MENU DISPLAY
    private static void showMenu(TransactionEngine bank) {
        System.out.println("MAIN MENU");
        System.out.println("Accounts");
        System.out.println(" 1. Create Account");
        System.out.println(" 2. View My Balance");
        System.out.println("Transactions  (account number + PIN needed)");
        System.out.println("3. Deposit");
        System.out.println(" 4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("Advanced");
        System.out.println("6. Begin Transaction");
        System.out.println("7. Commit");
        System.out.println("8. Rollback");
        System.out.println("Info");
        System.out.println("9.  Audit Log");
        System.out.println("10. Transaction Status");
        System.out.println("Help");
        System.out.println(" 11. Reset PIN (for locked accounts)");
        System.out.println("0. Exit");

        // Remind the user if they have an open transaction
        if (!bank.openTransactions.isEmpty()) {
            System.out.println("[REMINDER] You have an open transaction.");
            System.out.println("Choose 7 to commit or 8 to rollback.");
        }
        System.out.print("Enter choice choice [0 -11]: ");
    }

    //  HANDLER METHODS
    // Handles option 1 — create a new account
    private static void handleCreateAccount(TransactionEngine bank) {
        System.out.println("Welcome create your Account");

        String name = readText("Your full name : ");
        int balance = readNumber("Opening balance (KES) : ");
        System.out.println("Now set your PIN.");
        System.out.println("It must be exactly 4 digits.");
        String pin = readAndConfirmPin();

        bank.createAccount(name, balance, pin);
    }

    // Handles option 2 — view YOUR balance (account number + PIN required)
    private static void handleViewMyAccount(TransactionEngine bank) {
        System.out.println(" View Balance");

        if (bank.accounts.isEmpty()) {
            System.out.println("No accounts yet. Create one first.");
            return;
        }
        bank.showAccountList();

        // Step 1 — get the account number
        String number = readText("Your account number : ");
        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found. Check the number.");
            return;
        }

        // Step 2 — check the PIN before showing anything
        String pin = readPin("Enter your PIN : ");
        boolean pinOk = bank.checkPin(account, pin);
        if (!pinOk) {
            return;
        }
        bank.showMyAccount(account);
    }

    // Handles option 3 — deposit money
    private static void handleDeposit(TransactionEngine bank) {
        System.out.println("Deposit to your account ");

        if (bank.accounts.isEmpty()) {
            System.out.println("No accounts yet. Create one first.");
            return;
        }
        bank.showAccountList();

        String number = readText("Your account number : ");
        // Step 1 — find the account
        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found. Check the number.");
            return;
        }
        // Step 2 — check the PIN before doing anything
        String pin = readPin(" Enter your PIN : ");
        boolean pinOk = bank.checkPin(account, pin);
        if (!pinOk) {
            return;
        }
        // Step 3 — get the amount and deposit
        int amount = readNumber("Amount to deposit : KES ");
        bank.deposit(number, amount);
    }

    // Handles option 4 — withdraw money
    private static void handleWithdraw(TransactionEngine bank) {
        System.out.println(" Withdraw ");

        if (bank.accounts.isEmpty()) {
            System.out.println("No accounts yet. Create one first.");
            return;
        }
        bank.showAccountList();

        String number = readText("Your account number : ");
        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found. Check the number.");
            return;
        }

        String pin = readPin("Enter your PIN : ");
        boolean pinOk = bank.checkPin(account, pin);
        if (!pinOk) {
            return;
        }

        int amount = readNumber("Amount to withdraw : KES ");
        bank.withdraw(number, amount);
    }

    // Handles option 5 — transfer between accounts
    private static void handleTransfer(TransactionEngine bank) {
        System.out.println("Transfer");

        if (bank.accounts.size() < 2) {
            System.out.println(" You need at least 2 accounts to transfer.");
            return;
        }

        bank.showAccountList();
        // Get sender details and verify PIN
        String fromNumber = readText("Your account number      : ");
        Account sender = bank.findAccount(fromNumber);
        if (sender == null) {
            System.out.println("Sending account not found.");
            return;
        }

        String pin = readPin("Enter your PIN : ");
        boolean pinOk = bank.checkPin(sender, pin);
        if (!pinOk) {
            return;
        }

        // Get recipient and amount
        String toNumber = readText("Recipient account number : ");
        int amount = readNumber("Amount to transfer (KES) : ");

        bank.transfer(fromNumber, toNumber, amount);
    }

    // Handles option 6 — begin a transaction manually
    private static void handleBeginTransaction(TransactionEngine bank) {
        System.out.println("Begin Transaction");

        if (bank.accounts.isEmpty()) {
            System.out.println("No accounts yet. Create one first.");
            return;
        }

        bank.showAccountList();

        String number = readText("Your account number : ");
        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        String pin = readPin("Enter your PIN : ");
        boolean pinOk = bank.checkPin(account, pin);
        if (!pinOk) {
            return;
        }

        bank.beginTransaction();
        System.out.println("You can now deposit, withdraw or transfer.");
        System.out.println("Choose 7 to save (commit) or 8 to cancel (rollback).");
    }


    //  Handles option 11 — reset PIN for a locked account

    private static void handleResetPin(TransactionEngine bank) {
        System.out.println(" Reset PIN ");
        System.out.println("We will verify your identity without your PIN.");

        if (bank.accounts.isEmpty()) {
            System.out.println(" No accounts yet. Create one first.");
            return;
        }

        bank.showAccountList();

        // Step 1 — account number
        String number = readText("Your account number : ");

        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("Account not found. Check the number.");
            return;
        }
        if (!account.isLocked) {
            System.out.println("That account is not locked.");
            System.out.println("You can change your PIN through normal login.");
            return;
        }

        System.out.println("Account is locked. Let's verify your identity.");

        // Step 2 — identity verification inputs
        String enteredName    = readText("Your registered full name  : ");
        int    enteredBalance = readNumber("Your current balance (KES) : ");

        // Step 3 — new PIN
        System.out.println("Now choose a new PIN.");
        String newPin = readAndConfirmPin();

        // Step 4 — hand everything to the bank method
        bank.resetPin(number, enteredName, enteredBalance, newPin);
    }
}
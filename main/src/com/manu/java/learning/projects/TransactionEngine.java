package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TransactionEngine {

        // ─────────────────────────────────────────────
        // LOG ENTRY
        // Records ONE change to ONE account.
        // Stores old balance so we can undo if needed.
        // ─────────────────────────────────────────────
        static class LogEntry {
            String accountId;
            int    oldBalance;
            int    newBalance;
            String operationType; // "DEPOSIT", "WITHDRAW"

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
                        + " → " + newBalance;
            }
        }

        // ─────────────────────────────────────────────
        // TRANSACTION
        // Holds all log entries made during one
        // begin→commit/rollback block.
        // ─────────────────────────────────────────────
        static class Transaction {
            List<LogEntry> logEntries = new ArrayList<>();
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

        // ─────────────────────────────────────────────
        // BANK — the main engine
        // ─────────────────────────────────────────────

        // Stores all account balances
        // Key = account id (e.g. "ACC001")
        // Value = current balance
        private final Map<String, Integer> accounts = new HashMap<>();

        // Stack of active transactions
        // Top of stack = innermost / current transaction
        private final Stack<Transaction> transactionStack = new Stack<>();

        // Permanent audit log — survives even after rollbacks
        private final List<String> auditLog = new ArrayList<>();

        // Auto-incrementing id for each new transaction
        private int nextTransactionId = 1;

        // ─────────────────────────────────────────────
        // CREATE ACCOUNT
        // ─────────────────────────────────────────────
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
            auditLog.add("CREATED account [" + accountId + "]"
                    + " with balance=" + initialBalance);
            System.out.println("  Created account [" + accountId + "]"
                    + " balance=" + initialBalance);
        }

        // ─────────────────────────────────────────────
        // BEGIN TRANSACTION
        // Push a new transaction onto the stack.
        // All operations until commit/rollback belong
        // to this transaction.
        // ─────────────────────────────────────────────
        public void beginTransaction() {
            Transaction newTransaction = new Transaction(nextTransactionId++);
            transactionStack.push(newTransaction);
            System.out.println("  BEGIN " + newTransaction
                    + " | depth=" + transactionStack.size());
        }

        // ─────────────────────────────────────────────
        // DEPOSIT
        // Add money to an account.
        // ─────────────────────────────────────────────
        public void deposit(String accountId, int amount) {

            validateAccountExists(accountId);
            validatePositiveAmount(amount);

            int oldBalance = accounts.get(accountId);
            int newBalance = oldBalance + amount;

            applyChange(accountId, oldBalance, newBalance, "DEPOSIT");
        }

        // ─────────────────────────────────────────────
        // WITHDRAW
        // Remove money from an account.
        // Enforces invariant: balance cannot go below 0.
        // ─────────────────────────────────────────────
        public void withdraw(String accountId, int amount) {

            validateAccountExists(accountId);
            validatePositiveAmount(amount);

            int oldBalance = accounts.get(accountId);

            // INVARIANT CHECK — never allow negative balance
            if (oldBalance < amount) {
                throw new IllegalStateException(
                        "Insufficient funds in [" + accountId + "]"
                                + " | balance=" + oldBalance
                                + " | requested=" + amount);
            }

            int newBalance = oldBalance - amount;
            applyChange(accountId, oldBalance, newBalance, "WITHDRAW");
        }

        // ─────────────────────────────────────────────
        // TRANSFER
        // Move money between two accounts.
        // ATOMIC — if either side fails, both are undone.
        // Uses its own internal transaction to guarantee
        // atomicity regardless of outer transactions.
        // ─────────────────────────────────────────────
        public void transfer(String fromAccountId,
                             String toAccountId,
                             int    amount) {

            validateAccountExists(fromAccountId);
            validateAccountExists(toAccountId);
            validatePositiveAmount(amount);

            System.out.println("  TRANSFER " + amount
                    + " from [" + fromAccountId + "]"
                    + " to ["   + toAccountId   + "]");

            // Start an internal transaction to wrap both operations
            // This guarantees atomicity — both or neither
            beginTransaction();

            try {
                withdraw(fromAccountId, amount); // step 1 — deduct
                deposit(toAccountId,    amount); // step 2 — add
                commit();                        // both worked — make it permanent

                System.out.println("  TRANSFER successful");

            } catch (Exception transferFailed) {

                // Something went wrong — undo everything in this transaction
                rollback();
                throw new IllegalStateException(
                        "TRANSFER FAILED and was rolled back: "
                                + transferFailed.getMessage());
            }
        }

        // ─────────────────────────────────────────────
        // COMMIT
        // Finalize the current (innermost) transaction.
        //
        // If this is the outermost transaction (stack
        // becomes empty after pop) → write to audit log.
        //
        // If there is still a parent transaction on the
        // stack → merge log entries upward into parent.
        // Parent will decide when to really commit.
        // ─────────────────────────────────────────────
        public void commit() {

            if (transactionStack.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot commit — no active transaction");
            }

            // Remove the current (innermost) transaction
            Transaction completedTransaction = transactionStack.pop();

            System.out.println("  COMMIT " + completedTransaction);

            if (transactionStack.isEmpty()) {
                // This was the outermost transaction
                // Changes are already applied to accounts map
                // Now make them permanent in the audit log
                for (LogEntry entry : completedTransaction.logEntries) {
                    auditLog.add("COMMITTED: " + entry);
                }
                System.out.println("  All changes committed to accounts permanently");

            } else {
                // There is still a parent transaction on the stack
                // Bubble this transaction's log entries up to the parent
                //  will commit or rollback all of them together
                Transaction parentTransaction = transactionStack.peek();
                parentTransaction.logEntries.addAll(completedTransaction.logEntries);

                System.out.println("  Merged into parent "
                        + parentTransaction
                        + " | depth now=" + transactionStack.size());
            }
        }

        // ─────────────────────────────────────────────
        // ROLLBACK
        // Undo all changes made in the current (innermost)
        // transaction. Outer transactions are unaffected.
        //
        // Undo in REVERSE ORDER — last change first.
        // ─────────────────────────────────────────────
        public void rollback() {

            if (transactionStack.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot rollback — no active transaction");
            }

            // Remove the current (innermost) transaction
            Transaction transactionToUndo = transactionStack.pop();

            System.out.println("  ROLLBACK " + transactionToUndo);

            // Get the log entries and reverse them
            // We undo the LAST change first, working backwards
            List<LogEntry> entries = transactionToUndo.logEntries;

            for (int i = entries.size() - 1; i >= 0; i--) {
                LogEntry entry = entries.get(i);

                // Restore the account to what it was BEFORE this operation
                accounts.put(entry.accountId, entry.oldBalance);

                System.out.println("    UNDID: " + entry
                        + " | restored to " + entry.oldBalance);
            }

            auditLog.add("ROLLED BACK: "
                    + transactionToUndo.logEntries.size()
                    + " operations in "
                    + transactionToUndo);
            System.out.println("  Rollback complete — "
                    + entries.size()
                    + " operations undone");
        }

        // ─────────────────────────────────────────────
        // APPLY CHANGE
        // Central method — every balance change goes here.
        // Decides whether to log (inside transaction)
        // or commit immediately (outside transaction).
        // ─────────────────────────────────────────────
        private void applyChange(String accountId,
                                 int    oldBalance,
                                 int    newBalance,
                                 String operationType) {

            // Apply change to the real accounts map immediately
            accounts.put(accountId, newBalance);

            LogEntry entry = new LogEntry(
                    accountId, oldBalance, newBalance, operationType);

            if (!transactionStack.isEmpty()) {
                // Inside a transaction — log it so we can roll back if needed
                transactionStack.peek().addLogEntry(entry);
                System.out.println("    " + entry
                        + " [logged in "
                        + transactionStack.peek()
                        + " depth=" + transactionStack.size() + "]");
            } else {
                // Outside any transaction — immediate permanent commit
                auditLog.add("IMMEDIATE: " + entry);
                System.out.println("    " + entry + " [immediate commit]");
            }
        }

        // ─────────────────────────────────────────────
        // VALIDATION HELPERS
        // ─────────────────────────────────────────────
        private void validateAccountExists(String accountId) {
            if (!accounts.containsKey(accountId)) {
                throw new IllegalArgumentException(
                        "Account not found: " + accountId);
            }
        }

        private void validatePositiveAmount(int amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "Amount must be positive, got: " + amount);
            }
        }

        // ─────────────────────────────────────────────
        // PRINT HELPERS
        // ─────────────────────────────────────────────
        public void printBalances() {
            System.out.println("\n  --- Current Balances ---");
            for (Map.Entry<String, Integer> entry : accounts.entrySet()) {
                System.out.println("  [" + entry.getKey()
                        + "] KES " + entry.getValue());
            }
            System.out.println();
        }

        public void printAuditLog() {
            System.out.println("\n  --- Audit Log ---");
            for (int i = 0; i < auditLog.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + auditLog.get(i));
            }
            System.out.println();
        }

        // ─────────────────────────────────────────────
        // MAIN — four test scenarios
        // ─────────────────────────────────────────────
        static void main() {

            // ── TEST 1 — Basic deposit and withdraw ────
            System.out.println("========================================");
            System.out.println(" TEST 1 — Basic deposit and withdraw   ");
            System.out.println("========================================");
            TransactionEngine bank1 = new TransactionEngine();
            bank1.createAccount("Alice", 1000);
            bank1.createAccount("Bob",   500);
            bank1.beginTransaction();
            bank1.deposit("Alice", 200);
            bank1.withdraw("Bob",  100);
            bank1.commit();
            bank1.printBalances();
            // Alice = 1200, Bob = 400


            // ── TEST 2 — Rollback restores balances ────
            System.out.println("========================================");
            System.out.println(" TEST 2 — Rollback restores balances   ");
            System.out.println("========================================");
            TransactionEngine bank2 = new TransactionEngine();
            bank2.createAccount("Alice", 1000);
            bank2.createAccount("Bob",   500);
            bank2.beginTransaction();
            bank2.deposit("Alice", 999);  // Alice goes to 1999
            bank2.withdraw("Bob",  200);  // Bob goes to 300
            bank2.rollback();                 // undo both
            bank2.printBalances();
            // Alice = 1000, Bob = 500 (restored)


            // ── TEST 3 — Failed transfer is atomic ─────
            System.out.println("========================================");
            System.out.println(" TEST 3 — Failed transfer is atomic    ");
            System.out.println("========================================");
            TransactionEngine bank3 = new TransactionEngine();
            bank3.createAccount("Alice", 1000);
            bank3.createAccount("Bob",   500);
            try {
                // Try to transfer more than Alice has
                bank3.transfer("Alice", "Bob", 9999);
            } catch (IllegalStateException transferError) {
                System.out.println("  Caught: " + transferError.getMessage());
            }
            bank3.printBalances();
            // Alice = 1000, Bob = 500 (both unchanged — atomic rollback)


            // ── TEST 4 — Nested transactions ───────────
            System.out.println("========================================");
            System.out.println(" TEST 4 — Nested transactions          ");
            System.out.println("========================================");
            TransactionEngine bank4 = getTransactionEngine();
            // wait — Carol has 800
            // withdraw 900 throws exception
            // T2 will be rolled back below

            System.out.println("  Rolling back inner transaction only:");
            bank4.rollback();                          // rollback T2 only
            // Bob = 500 restored
            // Carol = 800 restored

            bank4.printBalances();                     // Alice=1200, Bob=500, Carol=800
            // Alice's 200 still pending in T1

            bank4.commit();                            // commit outer T1
            bank4.printBalances();                     // Alice=1200, Bob=500, Carol=800
            bank4.printAuditLog();
        }

    private static TransactionEngine getTransactionEngine() {
        TransactionEngine bank4 = new TransactionEngine();
        bank4.createAccount("Alice", 1000);
        bank4.createAccount("Bob",   500);
        bank4.createAccount("Carol", 800);

        bank4.beginTransaction();                  // outer T1
        bank4.deposit("Alice", 200);           // Alice = 1200

        bank4.beginTransaction();              // inner T2
        bank4.deposit("Bob",   300);       // Bob = 800
        bank4.withdraw("Carol", 900);      // Carol would go negative!
        return bank4;
    }
}



//        ## Summary of everything
//```
//    CONCEPT            WHAT IT MAPS TO IN CODE
//─────────────────  ──────────────────────────────────────
//    Accounts           HashMap<String, Integer>
//    Nested transactions Stack<Transaction>
//    Undo log           ArrayList<LogEntry> inside Transaction
//    Atomicity          beginTransaction + try/catch + rollback
//    Invariant          balance >= 0 check before every withdraw
//    Rollback           loop log entries in reverse, restore old
//    Commit (inner)     merge log entries into parent transaction
//    Commit (outer)     write to permanent audit log
//

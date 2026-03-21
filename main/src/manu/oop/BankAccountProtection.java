package manu.oop;

public class BankAccountProtection {
    static void main(String[] args) {
        BankAccount bankAccount1 = new BankAccount("0567","Jules");
        bankAccount1.deposit(3200);
        System.out.println(bankAccount1.getBalance());
        System.out.println(bankAccount1.ownerName);
    }
    public static class BankAccount {
        // Private data - hidden from outside
        private String accountNumber;
        private double balance;
        private String ownerName;
        // Constructor
        public BankAccount(String num, String owner) {
            this.accountNumber = num;
            this.ownerName = owner;
            this.balance = 0.0;
        }
        // Public getter - controlled read access
        public double getBalance() {
            return balance;
        }
        // Public setter with validation
        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            } else {
                System.out.println("Invalid amount");
            }
        }
    }

}

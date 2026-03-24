package manu.oop.encapsulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

enum AccountType {
    SAVINGS,CURRENT,FIXED
}
enum TransactionType{
    DEPOSIT,WITHDRAWAL,TRANSFER_IN,TRANSACTION_OUT
}
enum TransactionStatus{
    FAILED,PENDING,SUCCESS
}

public class SecureBankAccountSystem {
    static void main(String[] args) {

    }
    class Customer{
        private static int counter;
        private String customerId;
        private String fullName;
        private String nationalId;
        private String phone;
        private String email;
        private LocalDate dateCreated;

        public Customer(String fullName, String nationalId, String phone, String email) {
            counter++;
            this.dateCreated = LocalDate.now();
            customerId = String.format("CUST-%04d",counter);
            this.nationalId = nationalId;
            this.email = email;
            this.phone = phone;
            this.fullName = fullName;
        }

//        getters
        public String getCustomerId() {
            return customerId;
        }
        public LocalDate getDateCreated() {
            return dateCreated;
        }
        public String getEmail() {
            return email;
        }
        public String getFullName() {
            return fullName;
        }
        public String getNationalId() {
            return nationalId;
        }
        public String getPhone() {
            return phone;
        }
//        setters
        public void setEmail(String email) {
            this.email = email;
        }
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "Thank you here are your details - " +
                    "customerId='" + customerId + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", nationalId='" + nationalId + '\'' +
                    ", phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    ", dateCreated=" + dateCreated
                    ;
        }
    }

    class Transaction {
        private static int counter;
        private String transactionId;
        private  BankAccount account;
        private TransactionType type;
        private  double amount;
        private double balanceBefore;
        private double balanceAfter;
        private TransactionStatus status;
        private  String reason;
        private  LocalDateTime timestamp;

        public Transaction(double amount,BankAccount account,double balanceBefore,double balanceAfter,TransactionStatus status,
                           String reason,TransactionType type) {
            counter++;
            this.transactionId = String.format("TRANS-%04d",counter);
            this.timestamp = LocalDateTime.now();
            this.type = type;
            this.reason = reason;
            this.account = account;
            this.amount = amount;
            this.balanceBefore = balanceBefore;
            this.balanceAfter = balanceAfter;
            this.status = status;
        }

        public BankAccount getAccount() {
            return account;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalanceAfter() {
            return balanceAfter;
        }

        public double getBalanceBefore() {
            return balanceBefore;
        }

        public String getReason() {
            return reason;
        }

        public TransactionStatus getStatus() {
            return status;
        }

        public TransactionType getType() {
            return type;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public  String getAmountDisplay(){
            if(type == TransactionType.DEPOSIT || type == TransactionType.TRANSFER_IN){
                return String.format("+KES %,.Of",amount);
            }else{
                return String.format("-KES %,.0f", amount);
            }
        }

        @Override
        public String toString() {
            return transactionId  + type+ getAmountDisplay() + status + reason;
        }
    }
    class BankAccount{
        private static int idCounter = 0;
        private String accountNumber;
        private  Customer customer;
        private AccountType type;
        private  double balance;
        private double minimumBalance;
        private  LocalDate dateOpened;
        private boolean active = true;



        public BankAccount(String accountNumber, Customer customer, AccountType type
        , double balance, double minimumBalance, boolean active) {
            idCounter++;
            this.active = active;
            this.dateOpened = LocalDate.now();
            this.minimumBalance = minimumBalance;
            this.accountNumber = String.format("AC-%04d",idCounter);
            this.customer = customer;
            this.balance = balance;
            this.type = type;

            if (type == AccountType.SAVINGS) {
                this.minimumBalance = 1000.0;
            } else if (type == AccountType.CURRENT) {
                this.minimumBalance = 0.0;
            } else {
                this.minimumBalance = 5000.0;
            }

        }
//            getters
            public boolean isActive() {
                return active;
            }

        public AccountType getType() {
            return type;
        }

        public Customer getCustomer() {
            return customer;
        }

        public LocalDate getDateOpened() {
            return dateOpened;
        }

//        setters
        public void setActive(boolean active) {
            this.active = active;
        }

//        helpers
        public boolean deposit(double amount){
            if (!active) {
                return false;
            }
            if (amount < 0){
                return false;
            }
            balance += amount;
            return true;
        }

        public boolean withdraw(double amount){
            if (!active) {
                return false;
            }
            if (amount < 0){
                return false;
            }
            if ((balance - amount) < minimumBalance) {
                return false;
            }
            balance -= amount;
            return  true;
        }

        public  String getBalanceDisplay(){
            return String.format("KES %,.Of",balance);
        }

        public boolean canWithdraw(double amount){
            if (withdraw(amount));
            return active
                    && amount > 0
                    && (balance - amount) >= minimumBalance;
        }

        @Override
        public String toString() {
            return accountNumber + " — "
                    + customer.getFullName()
                    +  type
                    + " — " + getBalanceDisplay();
        }
    }
}

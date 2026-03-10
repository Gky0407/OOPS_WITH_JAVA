import java.util.Random;

class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

class BankUtil {
    public static int generateAccountNumber() {
        Random r = new Random();
        return 100000 + r.nextInt(900000);
    }

    public static void validateMinimumBalance(double balance) throws Exception {
        if (balance < 1000) {
            throw new Exception("Minimum balance must be ₹1000");
        }
    }
}

class Account {
    protected int accountNumber;
    protected double balance;

    public Account(int accountNumber, double balance) throws Exception {
        BankUtil.validateMinimumBalance(balance);
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: ₹" + amount);
    }

    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("Withdrawal exceeds balance");
        }

        if (balance - amount < 1000) {
            System.out.println("Minimum balance of ₹1000 must be maintained");
            return;
        }

        balance -= amount;
        System.out.println("Withdrawn: ₹" + amount);
    }

    public double getBalance() {
        return balance;
    }
}

class SavingsAccount extends Account {
    double interestRate;

    public SavingsAccount(int accountNumber, double balance, double interestRate) throws Exception {
        super(accountNumber, balance);
        this.interestRate = interestRate;
    }

    public double calculateInterest() {
        double interest = balance * interestRate / 100;
        return interest;
    }
}

class Customer {
    int customerId;
    String name;
    Account account;

    public Customer(int customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public void linkAccount(Account account) {
        this.account = account;
    }

    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + customerId);
        System.out.println("Name: " + name);

        if (account != null) {
            System.out.println("Account Balance: ₹" + account.getBalance());
        }
    }
}

class Loan {
    double loanAmount;
    double interestRate;
    int tenure;

    public Loan(double loanAmount, double interestRate, int tenure) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.tenure = tenure;
    }

    public double calculateEMI() {
        double emi = (loanAmount * interestRate * tenure) / 100;
        return emi;
    }
}

public class BankApplication {
    public static void main(String[] args) {
        try {
            Customer customer = new Customer(101, "Rahul");

            int accNo = BankUtil.generateAccountNumber();

            SavingsAccount account = new SavingsAccount(accNo, 5000, 5);

            customer.linkAccount(account);

            account.deposit(2000);

            account.withdraw(1500);

            double interest = account.calculateInterest();
            System.out.println("Interest Earned: ₹" + interest);

            Loan loan = new Loan(100000, 5, 2);
            System.out.println("Loan EMI: ₹" + loan.calculateEMI());

            customer.displayCustomerDetails();
        } 
        catch (InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
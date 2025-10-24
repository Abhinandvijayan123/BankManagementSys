import java.util.*;
import java.sql.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Bank bank = new Bank();

    public static void main(String[] args) {
        SetupDB.initialize(); // create all tables
        if (!employeeLogin()) {
            System.out.println("❌ Invalid login. Exiting...");
            return;
        }

        while (true) {
            System.out.println("\n--- Bank Management System ---");
            System.out.println("1. Register Customer");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Balance");
            System.out.println("6. View Transaction History");
            System.out.println("7. Add Employee");
            System.out.println("8. View All Accounts");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> registerCustomer();
                case 2 -> depositMoney();
                case 3 -> withdrawMoney();
                case 4 -> transferMoney();
                case 5 -> viewBalance();
                case 6 -> viewTransactionHistory();
                case 7 -> addEmployee();
                case 8 -> viewAllAccounts();
                case 9 -> {
                    System.out.println("Thank you for using the system!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ==================== EMPLOYEE LOGIN =====================
    private static boolean employeeLogin() {
        System.out.println("\n--- Employee Login ---");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String sql = "SELECT * FROM employees WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Login successful! Welcome, " + user);
                return true;
            } else {
                System.out.println("❌ Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return false;
    }

    // ==================== REGISTER CUSTOMER =====================
    private static void registerCustomer() {
        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        Customer customer = new Customer(name, email, phone);

        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        System.out.print("Enter Initial Balance: ");
        double bal = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Account Type (Savings/Current): ");
        String type = sc.nextLine();

        new Account(accNum, customer, bal, type);
        System.out.println("✅ Customer registered and account created successfully!");
    }

    // ==================== DEPOSIT =====================
    private static void depositMoney() {
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = sc.nextDouble();

        Account acc = bank.findAccount(accNum);
        if (acc != null) {
            acc.deposit(amount);
            System.out.println("✅ Deposit successful!");
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    // ==================== WITHDRAW =====================
    private static void withdrawMoney() {
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = sc.nextDouble();

        Account acc = bank.findAccount(accNum);
        if (acc != null) {
            try {
                acc.withdraw(amount);
                System.out.println("✅ Withdrawal successful!");
            } catch (InsufficientFundsException e) {
                System.out.println("❌ Insufficient funds!");
            }
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    // ==================== TRANSFER =====================
    private static void transferMoney() {
        System.out.print("Enter Sender Account Number: ");
        int sender = sc.nextInt();
        System.out.print("Enter Receiver Account Number: ");
        int receiver = sc.nextInt();
        System.out.print("Enter Transfer Amount: ");
        double amt = sc.nextDouble();

        Account accSender = bank.findAccount(sender);
        Account accReceiver = bank.findAccount(receiver);

        if (accSender != null && accReceiver != null) {
            try {
                accSender.withdraw(amt);
                accReceiver.deposit(amt);
                System.out.println("✅ Transfer successful!");
            } catch (InsufficientFundsException e) {
                System.out.println("❌ Sender has insufficient funds!");
            }
        } else {
            System.out.println("❌ Invalid account number(s).");
        }
    }

    // ==================== VIEW BALANCE =====================
    private static void viewBalance() {
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();

        Account acc = bank.findAccount(accNum);
        if (acc != null) {
            System.out.println("Account Holder: " + acc.getCustomerName());
            System.out.println("Account Type: " + acc.getAccType());
            System.out.println("Current Balance: " + acc.getBalance());
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    // ==================== VIEW TRANSACTIONS =====================
    private static void viewTransactionHistory() {
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT * FROM transactions WHERE accNumber = ? ORDER BY date DESC";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNum);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n--- Transaction History ---");
            while (rs.next()) {
                System.out.println(rs.getString("date") + " | " +
                                   rs.getString("type") + " | " +
                                   rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
    }

    // ==================== ADD EMPLOYEE =====================
    private static void addEmployee() {
        System.out.print("Enter new employee username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        String sql = "INSERT INTO employees (username, password) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            pstmt.executeUpdate();
            System.out.println("✅ Employee added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding employee: " + e.getMessage());
        }
    }

    // ==================== VIEW ALL ACCOUNTS =====================
    private static void viewAllAccounts() {
        System.out.println("\n--- All Accounts ---");
        for (Account acc : bank.getAllAccounts()) {
            System.out.println(acc.getAccNumber() + " | " +
                               acc.getCustomerName() + " | " +
                               acc.getAccType() + " | Balance: " + acc.getBalance());
        }
    }
}

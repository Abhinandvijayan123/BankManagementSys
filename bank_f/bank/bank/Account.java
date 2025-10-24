import java.sql.*;

public class Account {
    protected int accNumber;
    protected Customer customer;
    protected double balance;
    protected String accType;

    public Account(int accNumber, Customer customer, double balance, String accType) {
        this.accNumber = accNumber;
        this.customer = customer;
        this.balance = balance;
        this.accType = accType;
        saveToDB();
    }

    public void deposit(double amount) {
        balance += amount;
        updateBalance();
        Transaction.log(accNumber, "Deposit", amount);
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance)throw new InsufficientFundsException("Insufficient funds to complete the transaction");

        balance -= amount;
        updateBalance();
        Transaction.log(accNumber, "Withdraw", amount);
    }

    private void saveToDB() {
        String sql = "INSERT OR REPLACE INTO accounts (accNumber, customer_id, accType, balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNumber);
            pstmt.setInt(2, customer.getId());
            pstmt.setString(3, accType);
            pstmt.setDouble(4, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    private void updateBalance() {
        String sql = "UPDATE accounts SET balance = ? WHERE accNumber = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, balance);
            pstmt.setInt(2, accNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    public int getAccNumber() { return accNumber; }
    public double getBalance() { return balance; }
    public String getAccType() { return accType; }
    public String getCustomerName() { return customer.getName(); }
}

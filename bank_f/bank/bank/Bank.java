import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    public Account findAccount(int accNumber) {
        String sql = """
            SELECT a.accNumber, a.balance, a.accType, c.id, c.name, c.email, c.phone
            FROM accounts a
            JOIN customers c ON a.customer_id = c.id
            WHERE a.accNumber = ?;
        """;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                return new Account(
                    rs.getInt("accNumber"),
                    c,
                    rs.getDouble("balance"),
                    rs.getString("accType")
                );
            }
        } catch (SQLException e) {}
        return null;
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = """
            SELECT a.accNumber, a.balance, a.accType, c.name, c.email, c.phone
            FROM accounts a
            JOIN customers c ON a.customer_id = c.id;
        """;
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer c = new Customer(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                list.add(new Account(
                    rs.getInt("accNumber"),
                    c,
                    rs.getDouble("balance"),
                    rs.getString("accType")
                ));
            }
        } catch (SQLException e) {}
        return list;
    }
}

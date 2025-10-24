import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet; // ✅ Add this import

public class SetupDB {
    public static void initialize() {
        String customers = """
            CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE,
                phone TEXT
            );
        """;

        String accounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                accNumber INTEGER PRIMARY KEY,
                customer_id INTEGER,
                accType TEXT,
                balance REAL NOT NULL,
                FOREIGN KEY (customer_id) REFERENCES customers(id)
            );
        """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                accNumber INTEGER,
                type TEXT,
                amount REAL,
                date TEXT,
                FOREIGN KEY (accNumber) REFERENCES accounts(accNumber)
            );
        """;

        String employees = """
            CREATE TABLE IF NOT EXISTS employees (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT
            );
        """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(customers);
            stmt.execute(accounts);
            stmt.execute(transactions);
            stmt.execute(employees);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }

        // ✅ Automatically add default admin
        addDefaultAdmin();
    }

    private static void addDefaultAdmin() {
        String check = "SELECT COUNT(*) AS count FROM employees";
        String insert = "INSERT INTO employees (username, password) VALUES ('admin', '1234')";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(check)) {
            if (rs.next() && rs.getInt("count") == 0) {
                stmt.execute(insert);
                System.out.println("✅ Default admin added (username: admin, password: 1234)");
            }
        } catch (SQLException e) {
            System.out.println("Error adding default admin: " + e.getMessage());
        }
    }
}

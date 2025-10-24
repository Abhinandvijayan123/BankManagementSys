import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:bank.db";

    public static Connection connect() {
        try {
            // ✅ Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // ✅ Establish the connection
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return null;
        }
    }
}

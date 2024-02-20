import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // Database connection parameters
        String url = "jdbc:postgresql://localhost:5432/DSA";
        String user = "postgres";
        String password = "123456";

        // Establishing the connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the PostgreSQL server successfully.");

            // Creating a statement
            try (Statement statement = connection.createStatement()) {
                // Executing a query
                try (ResultSet resultSet = statement.executeQuery("SELECT * FROM your_table")) {
                    // Processing the result set
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString("column_name"));
                        // Replace "column_name" with the actual column name in your table
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;

public class admin {
    public static void main(String args[]) {

        // Configuration
        String url = "jdbc:postgresql://localhost:5432/dbms_project";
        String user = "postgres";
        String password = "123456";
        Connection con = null;
        Statement statement = null;

        try {
            // Connecting to database
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database Opened!");
            statement = con.createStatement();

            // Creating tables in database
            Path tables = Path.of("Database\\tables.sql");
            String str = Files.readString(tables);
            statement.executeUpdate(str);
            System.out.println("Tables created successfully!");

            // Creating stored procedures in database
            Path procedures = Path.of("Database\\procedures.sql");
            str = Files.readString(procedures);
            statement.executeUpdate(str);
            System.out.println("Stored Procedures created successfully!");
            
            statement.close();
            con.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
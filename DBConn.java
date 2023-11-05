import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XE";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "2323";

    public static Connection connect() {
        Connection connection = null;
        try {
             //Register the oracle driver
             Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        if(connection!=null)
          System.out.println("COnnected");
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

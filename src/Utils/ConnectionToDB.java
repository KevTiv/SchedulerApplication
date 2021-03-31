package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ConnectionToDB is used throughout the application for establishing a connection with the database.
 */
public class ConnectionToDB {

    /**
     * @return The connection to the database.
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection connectionToDB = DriverManager.getConnection("jdbc:mysql://wgudb.ucertify.com:3306", "U07qY8", "53689106056");
        String query = "USE WJ07qY8";
        try (Statement stmt = connectionToDB.createStatement()) {
            stmt.executeQuery(query);
        } catch (Exception e) {e.printStackTrace();}
        return connectionToDB;
    }
}

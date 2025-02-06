package com.example.cw2_rya3.database;

import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/rya3_db";
    private static final String USER = "root";
    private static final String PASS = "Abatan2004";

    static {
        try {
            // Load MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        // Enforce StrictMode for database operations in Android
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Return the database connection
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static ResultSet executeSelectQuery(String query) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query); // Returns the ResultSet
    }

    public static void executeQuery(String query) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Query executed successfully.");
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}


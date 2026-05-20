package com.chatapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =
            "jdbc:mysql://localhost:3306/chatapp";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    public static Connection connect() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL Driver NOT FOUND");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Database CONNECTION FAILED");
            e.printStackTrace();
        }

        return null;
    }
}
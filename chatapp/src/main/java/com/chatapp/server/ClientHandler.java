package com.chatapp.server;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {

        this.socket = socket;

        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            String message;

            while ((message = reader.readLine()) != null) {

                System.out.println("Received: " + message);

                String[] tokens = message.split(":", 4);

                String command = tokens[0];

                switch (command) {

                    case "REGISTER":
                        if (tokens.length >= 3)
                            register(tokens[1], tokens[2]);
                        else
                            writer.println("Invalid REGISTER format");
                        break;

                    case "LOGIN":
                        if (tokens.length >= 3)
                            login(tokens[1], tokens[2]);
                        else
                            writer.println("Invalid LOGIN format");
                        break;

                    case "MESSAGE":
                        if (tokens.length >= 4)
                            saveMessage(tokens[1], tokens[2], tokens[3]);
                        else
                            writer.println("Invalid MESSAGE format");
                        break;

                    default:
                        writer.println("Unknown command");
                }
            }

        } catch (Exception e) {
            System.out.println("Client disconnected");
        } finally {
            cleanup();
        }
    }

    private void register(String username, String password) {

        try (Connection conn = Database.connect()) {

            if (conn == null) {
                writer.println("DB_ERROR");
                return;
            }

            String sql = "INSERT INTO users(username,password) VALUES(?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();

            writer.println("REGISTER_SUCCESS");

        } catch (Exception e) {
            writer.println("REGISTER_FAILED");
            e.printStackTrace();
        }
    }

    private void login(String username, String password) {

        try (Connection conn = Database.connect()) {

            if (conn == null) {
                writer.println("DB_ERROR");
                return;
            }

            String sql = "SELECT * FROM users WHERE username=? AND password=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                writer.println("SUCCESS");
            } else {
                writer.println("FAILED");
            }

        } catch (Exception e) {
            writer.println("LOGIN_ERROR");
            e.printStackTrace();
        }
    }

    private void saveMessage(String sender, String receiver, String msg) {

        try (Connection conn = Database.connect()) {

            if (conn == null) {
                writer.println("DB_ERROR");
                return;
            }

            String sql =
                    "INSERT INTO messages(sender,receiver,message) VALUES(?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, msg);

            ps.executeUpdate();

            writer.println("MESSAGE_SAVED");

        } catch (Exception e) {
            writer.println("MESSAGE_ERROR");
            e.printStackTrace();
        }
    }


    private void cleanup() {

        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
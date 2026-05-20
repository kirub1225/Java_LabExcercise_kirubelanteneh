package com.chatapp.client;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public SocketClient() {
        try {
            socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected To Server");
        } catch (Exception e) {
            System.out.println("Server Not Running");
            e.printStackTrace();
        }
    }

    public void send(String message) {
        if (writer != null) {
            writer.println(message);
        } else {
            System.out.println("Cannot Send Message");
        }
    }

    public String receive() {
        try {
            if (reader != null) {
                return reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
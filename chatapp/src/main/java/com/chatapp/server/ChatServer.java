package com.chatapp.server;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server Started...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client Connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
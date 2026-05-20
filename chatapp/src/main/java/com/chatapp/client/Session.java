package com.chatapp.client;

public class Session {
    private static String username;
    private static SocketClient client;

    public static String getUsername() { return username; }
    public static void setUsername(String username) { Session.username = username; }

    public static synchronized SocketClient getClient() {
        if (client == null) {
            client = new SocketClient();
        }
        return client;
    }
}
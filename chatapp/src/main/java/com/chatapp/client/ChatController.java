package com.chatapp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatController {

    @FXML private ListView<String> usersList;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField searchField;

    private String currentReceiver;

    @FXML
    public void initialize() {
        new Thread(() -> Session.getClient().send("LOAD_CONVERSATIONS")).start();

        startIncomingMessageListener();

        usersList.setOnMouseClicked(e -> {
            String selected = usersList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentReceiver = selected.split(" ")[0];
                loadHistory();
            }
        });
    }

    private void startIncomingMessageListener() {
        new Thread(() -> {
            try {
                while (true) {
                    String update = Session.getClient().receive();
                    if (update == null) break;

                    String[] tokens = update.split(":", 4);
                    String header = tokens[0];

                    Platform.runLater(() -> {
                        switch (header) {
                            case "CONVERSATIONS_LIST":
                                usersList.getItems().clear();
                                for (int i = 1; i < tokens.length; i++) {
                                    String[] data = tokens[i].split(",");
                                    String name = data[0];
                                    int unreadCount = Integer.parseInt(data[1]);

                                    if (unreadCount > 0) {
                                        usersList.getItems().add(name + " (📩 " + unreadCount + " Unread)");
                                    } else {
                                        usersList.getItems().add(name);
                                    }
                                }
                                break;

                            case "HISTORY_DATA":
                                chatArea.clear();
                                chatArea.appendText("Conversation with " + currentReceiver + "\n\n");
                                for (int i = 1; i < tokens.length; i++) {
                                    String[] msgParts = tokens[i].split("->", 2);
                                    if (msgParts.length < 2) continue;
                                    chatArea.appendText(msgParts[0] + ": " + msgParts[1] + "\n");
                                }
                                break;

                            case "INCOMING_MSG":
                                String messageFrom = tokens[1];
                                String msgText = tokens[2];

                                if (messageFrom.equals(currentReceiver)) {
                                    chatArea.appendText(messageFrom + ": " + msgText + "\n");
                                    new Thread(() -> Session.getClient().send("HISTORY:" + Session.getUsername() + ":" + currentReceiver)).start();
                                } else {
                                    new Thread(() -> Session.getClient().send("LOAD_CONVERSATIONS")).start();
                                }
                                break;

                            case "SEARCH_RESULT":
                                if ("FOUND".equals(tokens[1])) {
                                    String foundUser = tokens[2];
                                    if (foundUser.equals(Session.getUsername())) return;

                                    boolean tracking = false;
                                    for (String item : usersList.getItems()) {
                                        if (item.startsWith(foundUser)) {
                                            tracking = true;
                                            break;
                                        }
                                    }
                                    if (!tracking) {
                                        usersList.getItems().add(foundUser);
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("User not found.");
                                    alert.show();
                                }
                                break;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void searchUser() {
        String username = searchField.getText().trim();
        if (username.isEmpty()) return;

        new Thread(() -> Session.getClient().send("SEARCH:" + username)).start();
        searchField.clear();
    }

    @FXML
    public void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty() || currentReceiver == null) return;

        new Thread(() -> {
            Session.getClient().send("MESSAGE:" + Session.getUsername() + ":" + currentReceiver + ":" + message);
            Platform.runLater(() -> {
                chatArea.appendText("Me: " + message + "\n");
                messageField.clear();
            });
        }).start();
    }

    private void loadHistory() {
        if (currentReceiver == null) return;
        new Thread(() -> {
            Session.getClient().send("HISTORY:" + Session.getUsername() + ":" + currentReceiver);
            Session.getClient().send("LOAD_CONVERSATIONS");
        }).start();
    }
}
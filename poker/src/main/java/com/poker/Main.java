package com.poker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private final DeckEngine deck = new DeckEngine();
    private final List<Card> playerHand = new ArrayList<>();
    private final List<Card> cpuHand = new ArrayList<>();

    private int playerChips = 300;
    private int cpuChips = 300;
    private final int perRoundAnte = 15;
    private boolean isAwaitingDrawState = false;

    private final Label[] cpuVisualCards = new Label[5];
    private final CheckBox[] playerInteractiveCards = new CheckBox[5];

    private final Label scoreboardHUD = new Label();
    private final Label communicationConsole = new Label("Welcome to JavaFX Video Poker. Press Deal to play!");
    private final Button masterActionButton = new Button("DEAL HAND");

    @Override
    public void start(Stage primaryWindow) {
        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #1b4d3e;");
        rootLayout.setPadding(new Insets(20));

        VBox headerArea = new VBox(10);
        headerArea.setAlignment(Pos.CENTER);
        scoreboardHUD.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px; -fx-font-family: 'Verdana'; -fx-font-weight: bold;");
        updateScoreboardDisplay();
        headerArea.getChildren().add(scoreboardHUD);
        rootLayout.setTop(headerArea);

        VBox playingGrid = new VBox(30);
        playingGrid.setAlignment(Pos.CENTER);

        HBox cpuCardRow = new HBox(15);
        cpuCardRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            cpuVisualCards[i] = new Label("??");
            cpuVisualCards[i].setPrefSize(75, 110);
            cpuVisualCards[i].setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            cpuCardRow.getChildren().add(cpuVisualCards[i]);
        }

        HBox playerCardRow = new HBox(15);
        playerCardRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            playerInteractiveCards[i] = new CheckBox("Locked");
            playerInteractiveCards[i].setPrefSize(75, 110);
            playerInteractiveCards[i].setDisable(true);
            playerInteractiveCards[i].setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold; -fx-border-color: transparent; -fx-border-width: 3px;");

            playerInteractiveCards[i].setOnAction(e -> {
                CheckBox box = (CheckBox) e.getSource();
                if (box.isSelected()) {
                    box.setStyle(box.getStyle() + "-fx-border-color: #e67e22;");
                } else {
                    box.setStyle(box.getStyle().replace("-fx-border-color: #e67e22;", "-fx-border-color: transparent;"));
                }
            });
            playerCardRow.getChildren().add(playerInteractiveCards[i]);
        }

        playingGrid.getChildren().addAll(new Label("--- OPPONENT ---") {{ setStyle("-fx-text-fill: white;"); }}, cpuCardRow, playerCardRow, new Label("--- YOUR HAND (Check boxes to HOLD) ---") {{ setStyle("-fx-text-fill: white;"); }});
        rootLayout.setCenter(playingGrid);

        VBox interfaceControls = new VBox(15);
        interfaceControls.setAlignment(Pos.CENTER);

        communicationConsole.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-style: italic;");
        masterActionButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-base: #2980b9; -fx-text-fill: white; -fx-padding: 10px 30px;");

        masterActionButton.setOnAction(e -> handleMasterActionCycle());

        interfaceControls.getChildren().addAll(communicationConsole, masterActionButton);
        rootLayout.setBottom(interfaceControls);

        Scene mainScene = new Scene(rootLayout, 650, 480);
        primaryWindow.setTitle("Modern JavaFX Poker Suite");
        primaryWindow.setScene(mainScene);
        primaryWindow.show();
    }

    private void handleMasterActionCycle() {
        if (!isAwaitingDrawState) {
            if (playerChips < perRoundAnte || cpuChips < perRoundAnte) {
                communicationConsole.setText("Match Termination. Financial reserves exhausted.");
                masterActionButton.setDisable(true);
                return;
            }

            playerChips -= perRoundAnte;
            cpuChips -= perRoundAnte;
            updateScoreboardDisplay();

            deck.populateAndShuffle();
            playerHand.clear();
            cpuHand.clear();

            for (int i = 0; i < 5; i++) {
                playerHand.add(deck.drawFromTop());
                cpuHand.add(deck.drawFromTop());
            }

            for (Label cardBox : cpuVisualCards) {
                cardBox.setText("░░");
                cardBox.setStyle("-fx-background-color: #dcdde1; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 20px; -fx-text-fill: #7f8c8d;");
            }

            for (int i = 0; i < 5; i++) {
                Card currentCard = playerHand.get(i);
                playerInteractiveCards[i].setDisable(false);
                playerInteractiveCards[i].setSelected(false);
                playerInteractiveCards[i].setText(currentCard.toString());
                playerInteractiveCards[i].setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + currentCard.getSuit().colorStyle + "; -fx-border-color: transparent; -fx-border-width: 3px;");
            }

            isAwaitingDrawState = true;
            masterActionButton.setText("DRAW REPLACEMENTS");
            communicationConsole.setText("Mark checkboxes on the cards you want to HOLD, then click Draw.");
        } else {
            for (int i = 0; i < 5; i++) {
                if (!playerInteractiveCards[i].isSelected()) {
                    playerHand.set(i, deck.drawFromTop());
                }
                playerInteractiveCards[i].setDisable(true);
            }

            int[] cpuCounts = new int[15];
            for (Card c : cpuHand) cpuCounts[c.getRank().numericValue]++;
            for (int i = 0; i < 5; i++) {
                if (cpuCounts[cpuHand.get(i).getRank().numericValue] < 2) {
                    cpuHand.set(i, deck.drawFromTop());
                }
            }

            for (int i = 0; i < 5; i++) {
                Card pCard = playerHand.get(i);
                playerInteractiveCards[i].setText(pCard.toString());
                playerInteractiveCards[i].setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + pCard.getSuit().colorStyle + ";");

                Card cCard = cpuHand.get(i);
                cpuVisualCards[i].setText(cCard.toString());
                cpuVisualCards[i].setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + cCard.getSuit().colorStyle + ";");
            }

            int playerWeight = HandRanker.evaluateHandStrength(playerHand);
            int cpuWeight = HandRanker.evaluateHandStrength(cpuHand);
            int tablePot = perRoundAnte * 2;

            if (playerWeight > cpuWeight) {
                playerChips += tablePot;
                communicationConsole.setText("You Win! Your " + HandRanker.convertStrengthToString(playerWeight) + " beat CPU's " + HandRanker.convertStrengthToString(cpuWeight) + ".");
            } else if (cpuWeight > playerWeight) {
                cpuChips += tablePot;
                communicationConsole.setText("CPU Wins! Their " + HandRanker.convertStrengthToString(cpuWeight) + " beat your " + HandRanker.convertStrengthToString(playerWeight) + ".");
            } else {
                playerChips += perRoundAnte;
                cpuChips += perRoundAnte;
                communicationConsole.setText("Split Pot Tie! Hand strengths matched at " + HandRanker.convertStrengthToString(playerWeight) + ".");
            }

            isAwaitingDrawState = false;
            masterActionButton.setText("PLAY NEXT ROUND");
            updateScoreboardDisplay();
        }
    }

    private void updateScoreboardDisplay() {
        scoreboardHUD.setText("USER CHIPS: [" + playerChips + "]   |   CPU BANK: [" + cpuChips + "]");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
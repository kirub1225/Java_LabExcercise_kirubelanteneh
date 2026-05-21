package com.poker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Stage window;
    private Scene welcomeScene;
    private Scene gameScene;
    private Scene instructionsScene;

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
    private final Label communicationConsole = new Label("Welcome to Kira Poker Games. Press Deal to play!");
    private final Button masterActionButton = new Button("DEAL HAND");

    @Override
    public void start(Stage primaryWindow) {
        this.window = primaryWindow;

        buildWelcomeDashboard();
        buildInstructionScreen();
        buildGameLayoutTable();

        window.setTitle("Kira Poker Games");
        window.setScene(welcomeScene);
        window.show();
    }

    private void buildWelcomeDashboard() {
        VBox menuRack = new VBox(25);
        menuRack.setAlignment(Pos.CENTER);
        menuRack.setStyle("-fx-background-color: #1e272e;");
        menuRack.setPadding(new Insets(40));

        Label brandTitle = new Label("KIRA POKER GAMES");
        brandTitle.setStyle("-fx-text-fill: #f5cd79; -fx-font-size: 34px; -fx-font-family: 'Impact'; -fx-letter-spacing: 2px;");

        Label brandSubtitle = new Label("Premium 5-Card Draw Edition");
        brandSubtitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Verdana'; -fx-font-style: italic;");

        Button launchGameBtn = new Button("START GAME");
        launchGameBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-base: #2ecc71; -fx-text-fill: white; -fx-padding: 12px 40px; -fx-background-radius: 20px;");
        launchGameBtn.setOnAction(e -> window.setScene(gameScene));

        Button viewRulesBtn = new Button("HOW TO PLAY");
        viewRulesBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-base: #34495e; -fx-text-fill: white; -fx-padding: 10px 35px; -fx-background-radius: 20px;");
        viewRulesBtn.setOnAction(e -> window.setScene(instructionsScene));

        menuRack.getChildren().addAll(brandTitle, brandSubtitle, launchGameBtn, viewRulesBtn);
        welcomeScene = new Scene(menuRack, 650, 480);
    }

    private void buildInstructionScreen() {
        BorderPane coreFrame = new BorderPane();
        coreFrame.setStyle("-fx-background-color: #2c3e50;");
        coreFrame.setPadding(new Insets(25));

        Label ruleHeader = new Label("GAMEPLAY GUIDE & INSTRUCTIONS");
        ruleHeader.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
        BorderPane.setAlignment(ruleHeader, Pos.CENTER);
        coreFrame.setTop(ruleHeader);

        VBox contentScrollerBox = new VBox(15);
        contentScrollerBox.setStyle("-fx-background-color: #34495e; -fx-padding: 20px; -fx-background-radius: 10px;");

        String rulesText =
                "1. ANTE & ENTRY\n" +
                        "Every round costs a fixed ante of $15 chips extracted from your wallet bankroll.\n\n" +
                        "2. THE INITIAL DEAL\n" +
                        "You and the CPU opponent are both assigned 5 random private cards from a shuffled 52-card deck.\n\n" +
                        "3. STRATEGY DISCARD PHASE\n" +
                        "Examine your hand. Check the boxes underneath cards you want to HOLD. Any card left unchecked will be destroyed and swapped for a fresh drawing block.\n\n" +
                        "4. AUTOMATED AI COMPETITOR\n" +
                        "The computer scans its cards using dynamic ranking filters. It automatically locks pairs and exchanges orphan single cards to attempt to maximize its scoring margin.\n\n" +
                        "5. FINAL SHOWDOWN COMPARISON\n" +
                        "Hands are weighed utilizing classic evaluation values (Pairs, Straights, Flushes, Full Houses). The superior combinations collect the pooled round table pot.";

        Label textContent = new Label(rulesText);
        textContent.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-family: 'Monospaced'; -fx-wrap-text: true;");
        contentScrollerBox.getChildren().add(textContent);

        ScrollPane trackScroll = new ScrollPane(contentScrollerBox);
        trackScroll.setFitToWidth(true);
        trackScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        coreFrame.setCenter(trackScroll);

        Button returnHomeBtn = new Button("RETURN TO MAIN MENU");
        returnHomeBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-base: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 25px;");
        returnHomeBtn.setOnAction(e -> window.setScene(welcomeScene));

        VBox bottomAnchor = new VBox(returnHomeBtn);
        bottomAnchor.setAlignment(Pos.CENTER);
        bottomAnchor.setPadding(new Insets(15, 0, 0, 0));
        coreFrame.setBottom(bottomAnchor);

        instructionsScene = new Scene(coreFrame, 650, 480);
    }

    private void buildGameLayoutTable() {
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

        Button exitToMenuBtn = new Button("Back to Menu");
        exitToMenuBtn.setStyle("-fx-font-size: 12px; -fx-base: #7f8c8d; -fx-text-fill: white;");
        exitToMenuBtn.setOnAction(e -> window.setScene(welcomeScene));

        HBox buttonRack = new HBox(20, masterActionButton, exitToMenuBtn);
        buttonRack.setAlignment(Pos.CENTER);

        interfaceControls.getChildren().addAll(communicationConsole, buttonRack);
        rootLayout.setBottom(interfaceControls);

        gameScene = new Scene(rootLayout, 650, 480);
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
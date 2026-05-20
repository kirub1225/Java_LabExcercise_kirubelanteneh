package com.notepad;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NotepadView {
    private final BorderPane root;
    private final TabPane tabPane;
    private final NotepadController controller;

    public NotepadView(Stage stage) {
        this.root = new BorderPane();
        this.tabPane = new TabPane();
        this.controller = new NotepadController(stage, tabPane);

        // Menu setup
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New Tab (Ctrl+N)");
        newItem.setOnAction(e -> controller.handleNew());

        MenuItem openItem = new MenuItem("Open File... (Ctrl+O)");
        openItem.setOnAction(e -> controller.handleOpen());

        MenuItem saveItem = new MenuItem("Save (Ctrl+S)");
        saveItem.setOnAction(e -> controller.handleSave());

        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction(e -> controller.handleSaveAs());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), new MenuItem("Exit"));
        menuBar.getMenus().add(fileMenu);

        root.setTop(menuBar);
        root.setCenter(tabPane);

        controller.handleNew();
    }

    public BorderPane getRoot() { return root; }
}
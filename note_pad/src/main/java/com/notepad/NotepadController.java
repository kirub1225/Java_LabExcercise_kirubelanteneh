package com.notepad;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NotepadController {
    private final Stage stage;
    private final TabPane tabPane;

    private File lastKnownDirectory = new File("C:\\file");

    public NotepadController(Stage stage, TabPane tabPane) {
        this.stage = stage;
        this.tabPane = tabPane;
    }


    public void handleNew() {
        createNewTab("Untitled", "");
    }

    public void handleOpen() {
        FileChooser chooser = createChooser("Open File");
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            try {
                lastKnownDirectory = file.getParentFile();
                Tab tab = createNewTab(file.getName(), Files.readString(file.toPath()));
                tab.setUserData(file);
            } catch (IOException e) {
                showError("Read Error", "Could not open file: " + e.getMessage());
            }
        }
    }


    public void handleSave() {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        if (activeTab == null) return;

        File file = (File) activeTab.getUserData();
        if (file == null) {
            handleSaveAs();
        } else {
            writeFile(activeTab, file);
        }
    }

    public void handleSaveAs() {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        if (activeTab == null) return;

        FileChooser chooser = createChooser("Save As (Name your file)");
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            lastKnownDirectory = file.getParentFile();
            writeFile(activeTab, file);
        }
    }

    private void writeFile(Tab tab, File file) {
        TextArea area = (TextArea) tab.getContent();
        try {
            Files.writeString(file.toPath(), area.getText());
            tab.setText(file.getName());
            tab.setUserData(file);
            stage.setTitle("Notepad - " + file.getName());
        } catch (IOException e) {
            showError("Save Error", "Could not save: " + e.getMessage());
        }
    }

    private Tab createNewTab(String title, String content) {
        TextArea area = new TextArea(content);
        area.setWrapText(true);
        Tab tab = new Tab(title, area);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        return tab;
    }

    private FileChooser createChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);


        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

        chooser.getExtensionFilters().addAll(textFilter, allFilter);

        chooser.setSelectedExtensionFilter(textFilter);

        if (lastKnownDirectory.exists()) {
            chooser.setInitialDirectory(lastKnownDirectory);
        }
        return chooser;
    }
    private void showError(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
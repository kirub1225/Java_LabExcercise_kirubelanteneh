package com.notepad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        NotepadView view = new NotepadView(stage);

        Scene scene = new Scene(view.getRoot(), 1000, 700);
        stage.setTitle("Multi-Tab Notepad");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
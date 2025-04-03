package io.github.gchape;

import atlantafx.base.theme.Dracula;
import io.github.gchape.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class PGNAnalyzer extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(Controller.root(), 650, 500);
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/styles.css")).toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();
    }
}
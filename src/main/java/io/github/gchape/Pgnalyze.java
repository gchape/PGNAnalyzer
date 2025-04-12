package io.github.gchape;

import atlantafx.base.theme.CupertinoDark;
import io.github.gchape.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Pgnalyze extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Controller().root(), 650, 500);
        stage.setResizable(true);
        stage.setScene(scene);

        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        scene.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("/styles.css"))
                        .toExternalForm());

        stage.show();
    }
}

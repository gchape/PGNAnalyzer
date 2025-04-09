package io.github.gchape.controller;

import io.github.gchape.controller.logic.Analyze;
import io.github.gchape.model.Model;
import io.github.gchape.view.View;
import io.github.gchape.view.handlers.MouseClickEvents;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;

public class Controller {
    private final static View view = View.getInstance();
    private final static Model model = Model.getInstance();

    static {
        model.selectedFilesProperty().addListener((__0, __1, newFiles) -> updateFileTree(newFiles));

        view.setEventHandlers(new MouseClickEvents() {
            @Override
            public void fileChooser(MouseEvent mouseEvent, FileChooser fileChooser) {
                handleFileSelection(mouseEvent, fileChooser);
            }

            @Override
            public void analyzeButton(MouseEvent mouseEvent) {
                handleAnalyzeButton();
            }
        });
    }

    private Controller() {
    }

    public static Region root() {
        return view.getRoot();
    }

    private static void updateFileTree(ObservableList<File> newFiles) {
        var fileTree = model.getFileTree();

        if (!newFiles.isEmpty()) {
            fileTree.setValue(newFiles.getFirst().getParent());
            newFiles.forEach(file -> fileTree.getChildren().add(new TreeItem<>(file.getName())));
        } else {
            fileTree.getChildren().clear();
        }
    }

    private static void handleFileSelection(MouseEvent mouseEvent, FileChooser fileChooser) {
        var stage = ((Node) mouseEvent.getSource()).getScene().getWindow();
        var selectedFiles = fileChooser.showOpenDialog(stage);

        if (selectedFiles != null) {
            model.getSelectedFiles().clear();
            model.getSelectedFiles().addAll(selectedFiles);
            model.analyzeButtonDisabledProperty().set(false);
        }
    }

    private static void handleAnalyzeButton() {
        model.selectFilesButtonDisabledProperty().set(true);

        var file = model.getSelectedFiles().getFirst();
        var games = parseGamesFromFile(file.toPath());

        games.parallelStream().forEach(game -> Thread.ofVirtual().start(game));

        model.analyzeButtonDisabledProperty().set(true);
        model.selectFilesButtonDisabledProperty().set(false);
    }

    private static ArrayList<Analyze> parseGamesFromFile(java.nio.file.Path file) {
        var games = new ArrayList<Analyze>();
        var header = new StringBuilder();
        var body = new StringBuilder();

        try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile())))) {
            while (true) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) break;
                    header.append(line).append("\n");
                }

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) break;
                    body.append(line).append(" ");
                }

                if (!header.isEmpty()) games.add(new Analyze(header, body, view.getTextArea()));
                if (line == null) break;

                header.setLength(0);
                body.setLength(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return games;
    }
}

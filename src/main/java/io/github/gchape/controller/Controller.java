package io.github.gchape.controller;

import io.github.gchape.controller.logic.Analyze;
import io.github.gchape.model.Model;
import io.github.gchape.view.View;
import io.github.gchape.view.handlers.MouseClickEvents;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {
    private final static View view = View.getInstance();
    private final static Model model = Model.getInstance();

    static {
        model.selectedFilesProperty().addListener((__0, __1, newFiles) -> {
            var fileTree = model.getFileTree();

            if (!newFiles.isEmpty()) {
                fileTree.setValue(newFiles.getFirst().getParent());
                newFiles.forEach(f -> fileTree.getChildren().add(new TreeItem<>(f.getName())));
            } else {
                fileTree.getChildren().clear();
            }
        });

        view.setEventHandlers(new MouseClickEvents() {
            @Override
            public void fileChooser(MouseEvent mouseEvent, FileChooser fileChooser) {
                var stage = ((Node) mouseEvent.getSource()).getScene().getWindow();
                var selectedFiles = fileChooser.showOpenDialog(stage);

                if (selectedFiles != null) {
                    model.getSelectedFiles().clear();
                    model.getSelectedFiles().addAll(selectedFiles);
                    model.analyzeButtonDisabledProperty().set(false);
                }
            }

            @Override
            public void analyzeButton(MouseEvent mouseEvent) {
                model.selectFilesButtonDisabledProperty().set(true);

                model.getSelectedFiles()
                        .stream()
                        .map(file -> {
                            var header = new StringBuilder();
                            var body = new StringBuilder();
                            try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.isBlank()) break;

                                    header.append(line);
                                }

                                while ((line = reader.readLine()) != null) {
                                    if (line.isBlank()) break;

                                    body.append(line);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            return new Analyze(header, body);
                        })
                        .parallel()
                        .forEach(Thread.ofVirtual()::start);

                model.saveLogButtonDisabledProperty().set(false);
            }

            @Override
            public void saveLogButton(MouseEvent mouseEvent) {
                model.saveLogButtonDisabledProperty().set(true);
                model.analyzeButtonDisabledProperty().set(false);
                model.selectFilesButtonDisabledProperty().set(false);
            }
        });
    }

    private Controller() {
    }

    public static Region root() {
        return view.getRoot();
    }
}

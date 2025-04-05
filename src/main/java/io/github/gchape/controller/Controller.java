package io.github.gchape.controller;

import io.github.gchape.logic.Analyzer;
import io.github.gchape.model.Model;
import io.github.gchape.view.View;
import io.github.gchape.view.events.EventHandlers;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

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

        view.setEventHandlers(new EventHandlers() {
            @Override
            public void fileChooserMouseClickAction(MouseEvent mouseEvent, FileChooser fileChooser) {
                var stage = ((Node) mouseEvent.getSource()).getScene().getWindow();
                var selectedFiles = fileChooser.showOpenMultipleDialog(stage);

                if (selectedFiles != null) {
                    model.getSelectedFiles().clear();
                    model.getSelectedFiles().addAll(selectedFiles);

                    model.analyzeDisabledProperty().set(false);
                }
            }

            @Override
            public void analyzeMouseClickAction(MouseEvent mouseEvent) {
                model.selectFilesDisabledProperty().set(true);

                model.getSelectedFiles()
                        .parallelStream()
                        .map(f -> new Analyzer(f, model.textAreaProperty()))
                        .forEach(Thread.ofVirtual()::start);

                model.saveLogDisabledProperty().set(false);
            }

            @Override
            public void saveLogMouseClickAction(MouseEvent mouseEvent) {
                model.saveLogDisabledProperty().set(true);
                model.analyzeDisabledProperty().set(false);
                model.selectFilesDisabledProperty().set(false);
            }
        });
    }

    private Controller() {
    }

    public static Region root() {
        return view.getRoot();
    }
}

package io.github.gchape.controller;

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
                var selectedFiles = fileChooser.showOpenMultipleDialog(((Node) mouseEvent.getSource()).getScene().getWindow());

                if (selectedFiles != null) {
                    model.getSelectedFiles().clear();
                    model.getSelectedFiles().addAll(selectedFiles);
                }
            }

            @Override
            public void analyzeButtonMouseClickAction(MouseEvent mouseEvent) {
                // TODO
            }

            @Override
            public void saveLogButtonMouseClickAction(MouseEvent mouseEvent) {
                // TODO
            }
        });
    }

    private Controller() {
    }

    public static Region root() {
        return view.getRoot();
    }
}

package io.github.gchape.controller;

import io.github.gchape.controller.logic.Parser;
import io.github.gchape.model.Model;
import io.github.gchape.view.View;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class Controller {
    private final View view;
    private final Model model;

    public Controller() {
        view = View.getInstance();
        model = Model.getInstance();

        model.selectedFilesProperty().addListener((__0, __1, newFiles) -> updateFileTree(newFiles));
        view.setSelectFilesHandlers(this::selectFilesClickAction);
        view.setAnalyzeHandlers(this::analyzeClickAction);
    }

    public Region root() {
        return view.getRoot();
    }

    private void updateFileTree(ObservableList<File> newFiles) {
        var fileTree = model.getFileTree();

        if (!newFiles.isEmpty()) {
            fileTree.setValue(newFiles.getFirst().getParent());
            newFiles.forEach(file -> fileTree.getChildren().add(new TreeItem<>(file.getName())));
        } else {
            fileTree.getChildren().clear();
        }
    }

    private void selectFilesClickAction(MouseEvent mouseEvent) {
        var stage = ((Node) mouseEvent.getSource()).getScene().getWindow();
        var selectedFiles = new FileChooser().showOpenDialog(stage);

        if (selectedFiles != null) {
            model.getSelectedFiles().clear();
            model.getSelectedFiles().addAll(selectedFiles);
            model.analyzeButtonDisabledProperty().set(false);
        }
    }

    private void analyzeClickAction(MouseEvent mouseEvent) {
        model.selectFilesButtonDisabledProperty().set(true);

        var file = model.getSelectedFiles().getFirst();
        var parser = new Parser(file);

        StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                parser.iterator(), 0),
                        true)
                .forEach(Thread.ofVirtual()::start);

        model.analyzeButtonDisabledProperty().set(true);
        model.selectFilesButtonDisabledProperty().set(false);
    }
}

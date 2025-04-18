package io.github.gchape.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;

public enum Model {
    INSTANCE;

    private final StringProperty textInput;
    private final ListProperty<File> selectedFiles;
    private final ObjectProperty<TreeItem<String>> fileTree;

    private final BooleanProperty analyzeButtonDisabled;
    private final BooleanProperty selectFilesButtonDisabled;

    Model() {
        fileTree = new SimpleObjectProperty<>(new TreeItem<>());
        selectedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

        analyzeButtonDisabled = new SimpleBooleanProperty(true);
        selectFilesButtonDisabled = new SimpleBooleanProperty(false);

        textInput = new SimpleStringProperty("");
    }

    public ObservableList<File> getSelectedFiles() {
        return selectedFiles.get();
    }

    public ReadOnlyListProperty<File> selectedFilesProperty() {
        return selectedFiles;
    }

    public TreeItem<String> getFileTree() {
        return fileTree.get();
    }

    public ReadOnlyObjectProperty<TreeItem<String>> fileTreeProperty() {
        return fileTree;
    }

    public BooleanProperty analyzeButtonDisabledProperty() {
        return analyzeButtonDisabled;
    }

    public BooleanProperty selectFilesButtonDisabledProperty() {
        return selectFilesButtonDisabled;
    }

    public StringProperty textInputProperty() {
        return textInput;
    }
}

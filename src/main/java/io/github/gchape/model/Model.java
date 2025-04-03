package io.github.gchape.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class Model {
    private final static Model INSTANCE = new Model();
    private final ObservableList<File> selectedFiles;

    private Model() {
        selectedFiles = FXCollections.emptyObservableList();
    }

    public static Model getInstance() {
        return INSTANCE;
    }

    public ObservableList<File> getSelectedFiles() {
        return selectedFiles;
    }
}

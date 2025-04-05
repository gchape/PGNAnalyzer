package io.github.gchape.logic;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import java.io.File;

public class Analyzer implements Runnable {
    private final File pgnFile;
    private final StringProperty textArea;

    public Analyzer(File pgnFile, StringProperty textArea) {
        this.pgnFile = pgnFile;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        Platform.runLater(() -> textArea.set(textArea.get() + "Hello\n"));
    }
}

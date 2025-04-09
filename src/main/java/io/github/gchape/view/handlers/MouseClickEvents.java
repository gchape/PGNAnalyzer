package io.github.gchape.view.handlers;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public interface MouseClickEvents {
    void fileChooser(final MouseEvent mouseEvent, final FileChooser fileChooser);

    void analyzeButton(final MouseEvent mouseEvent);
}

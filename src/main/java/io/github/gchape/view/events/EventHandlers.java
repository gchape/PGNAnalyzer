package io.github.gchape.view.events;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public interface EventHandlers {
    void fileChooserMouseClickAction(final MouseEvent mouseEvent, final FileChooser fileChooser);

    void analyzeMouseClickAction(final MouseEvent mouseEvent);

    void saveLogMouseClickAction(final MouseEvent mouseEvent);
}

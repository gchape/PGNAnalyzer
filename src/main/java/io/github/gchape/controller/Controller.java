package io.github.gchape.controller;

import io.github.gchape.model.Model;
import io.github.gchape.view.View;
import javafx.scene.layout.Region;

public class Controller {
    private final static View view = View.getInstance();
    private final static Model model = Model.getInstance();

    private Controller() {
    }

    public static Region root() {
        return view.getRoot();
    }
}

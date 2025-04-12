package io.github.gchape.controller.logic;

import io.github.gchape.model.Model;

import java.util.Map;

public class Game implements Runnable {
    private final String moves;
    private final Map<String, String> headers;
    private final Model model = Model.getInstance();

    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;
    }

    @Override
    public void run() {
        // TODO --- Game Replay ---
    }
}

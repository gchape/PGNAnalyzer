package io.github.gchape.controller.logic;

import java.util.Map;

public class Game implements Runnable {
    private final String moves;
    private final Map<String, String> headers;

    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;
    }

    @Override
    public void run() {

    }
}

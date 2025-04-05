package io.github.gchape.controller.logic;

import io.github.gchape.model.Model;
import io.github.gchape.model.entities.Board;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Analyze implements Runnable {
    private final File file;
    private final Map<String, String> headers = new HashMap<>();
    private final StringProperty textArea = Model.getInstance().textAreaProperty();

    public Analyze(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        var header = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();

                if (line.startsWith("[")) {
                    var h = line.substring(1, line.length() - 1).split(" ");

                    headers.put(h[0], h[1].replaceAll("\"", ""));
                } else if (line.isBlank()) break;
            }
            header.append("Event: ").append(headers.getOrDefault("Event", "Unknown Event")).append("\n");
            header.append("White: ").append(headers.getOrDefault("White", "Unknown")).append("\n");
            header.append("Black: ").append(headers.getOrDefault("Black", "Unknown")).append("\n");
            header.append("Result: ").append(headers.getOrDefault("Result", "Unknown")).append("\n");

            var moves = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isBlank()) {
                    break;
                } else {
                    line = line.replaceAll("\\s+", " ");
                    line = line.replaceAll("\\d+\\.", "").trim();
                    line = line.replaceAll("(\\s*(1-0|0-1|1/2-1/2)\\s*)$", "");

                    String[] splitMoves = line.split("\\s+");
                    for (int i = 0; i < splitMoves.length; i += 2) {
                        if (i + 1 < splitMoves.length) {
                            if (!moves.isEmpty()) {
                                moves.append(",");
                            }
                            moves.append(splitMoves[i]).append(" ").append(splitMoves[i + 1]);
                        }
                    }
                }
            }

            Board board = new Board();
            for (var move : moves.toString().split(",")) {
                board.move(move);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

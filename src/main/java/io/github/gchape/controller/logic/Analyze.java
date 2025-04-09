package io.github.gchape.controller.logic;

import io.github.gchape.model.Model;
import io.github.gchape.model.entities.Board;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Analyze implements Runnable {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final String body;
    private final Map<String, String> headers = new HashMap<>();
    private final StringProperty textArea = Model.getInstance().textAreaProperty();
    private final int id;

    public Analyze(StringBuilder header, StringBuilder body) {
        this.body = body.toString()
                .replaceAll(";.*", "")
                .replaceAll("\\s+", " ")
                .replaceAll("[+#?!]", "")
                .replaceAll("\\d+\\.", "")
                .replaceAll("\\{[^}]*}", "")
                .trim();

        var pattern = Pattern.compile("\\[(\\w+)\\s+\"([^\"]*)\"]");
        var matcher = pattern.matcher(header);
        while (matcher.find()) {
            headers.put(matcher.group(1), matcher.group(2));
        }

        this.id = idCounter.incrementAndGet();
    }

    @Override
    public void run() {
        Board board = new Board();
        String[] moves = body.split(" ");

        textArea.set(textArea.get() + """
                {
                    "id": %d,
                    "Event": "%s",
                    "White": "%s",
                    "Black": "%s"
                },
                """.formatted(id, headers.get("Event"), headers.get("White"), headers.get("Black")));

        boolean valid = true;
        for (int i = 0; i < moves.length; i += 2) {
            String whiteMove = moves[i];
            String blackMove = i + 1 < moves.length ? moves[i + 1] : "";

            try {
                board.tryMove(whiteMove, true);
                board.tryMove(blackMove, false);
            } catch (Exception e) {
                writeInLog(e.getMessage());

                valid = false;
                break;
            }
        }

        boolean finalValid = valid;
        Platform.runLater(() -> {
            textArea.set(textArea.get() + """
                {
                    "id": %d,
                    "Valid": %s
                },
                """.formatted(id, finalValid ? "true" : "false"));
        });
    }

    private void writeInLog(String message) {
        try {
            Path path = Path.of("src/main/resources/%s.log".formatted(LocalDate.now()));
            if (!Files.exists(path))
                Files.createFile(path);

            try (var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())))) {
                writer.write(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

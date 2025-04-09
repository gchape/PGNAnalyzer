package io.github.gchape.controller.logic;

import io.github.gchape.model.Model;
import io.github.gchape.model.entities.Board;
import javafx.beans.property.StringProperty;

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
                }
                """.formatted(id, headers.get("Event"), headers.get("White"), headers.get("Black")));

        boolean valid = true;
        for (int i = 0; i < moves.length; i += 2) {
            String whiteMove = moves[i];
            String blackMove = i + 1 < moves.length ? moves[i + 1] : "";

            try {
                System.out.println(whiteMove + " " + blackMove);
                board.tryMove(whiteMove, true);
                board.tryMove(blackMove, false);
            } catch (Exception e) {
                e.printStackTrace();
                valid = false;
                break;
            }
        }

        textArea.set(textArea.get() + """
                {
                    "id": %d,
                    "Valid": %s
                }
                """.formatted(id, valid ? "true" : "false"));
    }
}

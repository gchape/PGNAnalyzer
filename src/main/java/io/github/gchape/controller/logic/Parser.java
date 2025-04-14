package io.github.gchape.controller.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class Parser {
    private final File file;
    private final List<String> moves;
    private final List<Map<String, String>> headers;

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern ANNOTATIONS_PATTERN = Pattern.compile("[+#?!]");
    private static final Pattern MOVE_NUMBERS_PATTERN = Pattern.compile("\\d+\\.");
    private static final Pattern COMMENT_PATTERN = Pattern.compile(";.*|\\{[^}]*}");

    public Parser(File file) {
        this.file = file;
        moves = new ArrayList<>();
        headers = new ArrayList<>();

        parse();
    }

    private void parse() {
        final List<StringBuilder> moves = new ArrayList<>();
        final List<StringBuilder> headers = new ArrayList<>();

        readLines(file, new StringBuilder(), new StringBuilder(), headers, moves);

        parseGameMoves(moves);
        parseGameHeaders(headers);
    }

    private void parseGameHeaders(List<StringBuilder> headers) {
        Pattern pattern = Pattern.compile("\\[(\\w+)\\s+\"([^\"]*)\"]");

        for (StringBuilder header : headers) {
            var matcher = pattern.matcher(header);
            var gameHeaders = new HashMap<String, String>();

            while (matcher.find()) {
                gameHeaders.put(matcher.group(1), matcher.group(2));
            }

            this.headers.add(gameHeaders);
        }
    }

    private void parseGameMoves(List<StringBuilder> moves) {
        for (StringBuilder move : moves) {
            var __ = move.toString();
            __ = COMMENT_PATTERN.matcher(__).replaceAll("");
            __ = MOVE_NUMBERS_PATTERN.matcher(__).replaceAll("");
            __ = ANNOTATIONS_PATTERN.matcher(__).replaceAll("");
            __ = WHITESPACE_PATTERN.matcher(__).replaceAll(" ").trim();

            this.moves.add(__);
        }
    }

    private void readLines(File file, StringBuilder header, StringBuilder body,
                           List<StringBuilder> headers, List<StringBuilder> moves) {
        try {
            Files.readAllLines(file.toPath()).forEach(line -> {
                if (line.contains("[")) {
                    header.append(line);
                } else if (!line.isBlank()) {
                    body.append(line).append(" ");
                } else {
                    if (!header.isEmpty()) {
                        headers.add(new StringBuilder(header));

                        header.setLength(0);
                    } else {
                        moves.add(new StringBuilder(body));

                        body.setLength(0);
                    }
                }
            });

            if (!body.isEmpty())
                moves.add(new StringBuilder(body));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<Game> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < headers.size();
            }

            @Override
            public Game next() {
                if (!hasNext()) throw new NoSuchElementException();
                return new Game(headers.get(i), moves.get(i++));
            }
        };
    }
}

package io.github.gchape.model.entities;

import io.github.gchape.model.Model;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class Printer {
    private static final Printer INSTANCE = new Printer();
    private final static StringProperty textInput = Model.getInstance().textInputProperty();

    private Printer() {
    }

    public static Printer getInstance() {
        return INSTANCE;
    }

    private static void append(String text) {
        Platform.runLater(() -> textInput.set(text));
    }

    public void appendHead(final Map<String, String> headers) {
        String[] args = new String[]{
                headers.get("Event"),
                headers.get("Round"),
                headers.get("White"),
                headers.get("Black"),
                headers.get("Result")
        };

        append("""
                {
                 Event: "%s",
                 White: "%s",
                 Black: "%s",
                 Round: "%s",
                 Result: "%s"
                },
                """.formatted(args[0], args[1], args[2], args[3], args[4]));
    }

    public void appendBody(final int id, final boolean isValid) {
        append("""
                {
                  Id: "%d",
                  Valid: "%s"
                }
                """.formatted(id, isValid));
    }
}

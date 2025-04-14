package io.github.gchape.exceptions;

public class InvalidPromotionException extends RuntimeException {
    public InvalidPromotionException(final String square) {
        super("Invalid promotion '" + square + "'. Pawn must reach rank " + square.charAt(0) + ".");
    }
}

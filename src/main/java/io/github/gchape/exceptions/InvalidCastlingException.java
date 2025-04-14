package io.github.gchape.exceptions;

public class InvalidCastlingException extends RuntimeException {
    public InvalidCastlingException(final String side, final String direction) {
        super(side + " cannot castle " + direction + ": either the king or rook has already moved.");
    }
}

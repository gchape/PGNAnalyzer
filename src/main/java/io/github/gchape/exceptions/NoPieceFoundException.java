package io.github.gchape.exceptions;

import io.github.gchape.model.entities.Piece;

public class NoPieceFoundException extends RuntimeException {
    public NoPieceFoundException(Piece piece, String target) {
        super("Can not find a valid " + piece + " to move to " + target);
    }
}

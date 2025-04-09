package io.github.gchape.model.entities;

public enum Piece {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;

    public static Piece of(char symbol) {
        return switch (symbol) {
            case 'Q' -> QUEEN;
            case 'K' -> KING;
            case 'B' -> BISHOP;
            case 'N' -> KNIGHT;
            case 'R' -> ROOK;
            default -> PAWN;
        };
    }
}

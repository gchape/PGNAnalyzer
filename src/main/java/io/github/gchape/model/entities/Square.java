package io.github.gchape.model.entities;

public record Square(int x, int y) {

    public Square(String chessNotation) {
        this(chessNotation.charAt(0) - 'a', Character.getNumericValue(chessNotation.charAt(1)));
    }

    public String toChessNotation() {
        return "" + (char) (x + 'a') + y;
    }

    public boolean isRookMoveTo(Square other) {
        return this.y == other.y || this.x == other.x;
    }

    public boolean isBishopMoveTo(Square other) {
        return Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isQueenMoveTo(Square other) {
        return this.y == other.y || this.x == other.x
                || Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isKnightMoveTo(Square other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
    }

    public boolean isKingMoveTo(Square other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return dy <= 1 && dx <= 1 && (dy + dx != 0);
    }

    public boolean isPawnMoveTo(Square other) {
        int dx = other.x - x;
        int dy = other.y - y;

        if (dx == 0 && Math.abs(dy) == 1) {
            return true;
        }

        if (dx == 0 && Math.abs(dy) == 2) {
            if (y == 1 || y == 6) {
                return true;
            }
        }

        return Math.abs(dx) == 1 && Math.abs(dy) == 1;
    }
}

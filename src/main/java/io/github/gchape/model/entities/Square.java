package io.github.gchape.model.entities;

public record Square(int x, int y) {

    public Square(String chessNotation) {
        this(chessNotation.charAt(0) - 'a', Character.getNumericValue(chessNotation.charAt(1)));
    }

    public String toChessNotation() {
        return "" + (char) (x + 'a') + y;
    }

    public boolean isRookMoveTo(final Square other) {
        return this.y == other.y || this.x == other.x;
    }

    public boolean isBishopMoveTo(final Square other) {
        return Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isQueenMoveTo(final Square other) {
        return this.y == other.y || this.x == other.x
                || Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isKnightMoveTo(final Square other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
    }

    public boolean isKingMoveTo(final Square other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return dy <= 1 && dx <= 1 && (dy + dx != 0);
    }

    public boolean isPawnMoveTo(final Square other, final boolean isWhite) {
        int dx = other.x - x;
        int dy = other.y - y;

        if (isWhite) {
            if (dx == 0 && dy == 1) return true;
            return dx == 0 && dy == 2 && y == 2;
        } else {
            if (dx == 0 && dy == -1) return true;
            return dx == 0 && dy == -2 && y == 7;
        }
    }

    public boolean isPawnCaptureTo(final Square other, final boolean isWhite) {
        int dx = other.x - x;
        int dy = other.y - y;

        if (isWhite) {
            return Math.abs(dx) == 1 && dy == 1;
        } else {
            return Math.abs(dx) == 1 && dy == -1;
        }
    }
}

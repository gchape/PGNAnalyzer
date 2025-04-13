package io.github.gchape.model.entities;

record Point(int y, int x) {

    public boolean isRookMoveTo(Point other) {
        return this.y == other.y || this.x == other.x;
    }

    public boolean isBishopMoveTo(Point other) {
        return Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isQueenMoveTo(Point other) {
        return this.y == other.y || this.x == other.x
                || Math.abs(other.y - y) == Math.abs(other.x - x);
    }

    public boolean isKnightMoveTo(Point other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
    }

    public boolean isKingMoveTo(Point other) {
        int dy = Math.abs(other.y - y);
        int dx = Math.abs(other.x - x);
        return dy <= 1 && dx <= 1 && (dy + dx != 0);
    }

    public boolean isPawnMoveTo(Point other, boolean isWhite, boolean isFirstMove) {
        int direction = isWhite ? -1 : 1;
        int dy = other.y - y;
        int dx = Math.abs(other.x - x);
        return (dx == 0 && (dy == direction || (isFirstMove && dy == 2 * direction)))
                || (dx == 1 && dy == direction);
    }
}

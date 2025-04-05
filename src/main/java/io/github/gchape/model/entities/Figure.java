package io.github.gchape.model.entities;

public enum Figure {
    KING('K') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };
        }
    },
    QUEEN('Q') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };
        }
    },
    ROOK('R') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}
            };
        }
    },
    BISHOP('B') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };
        }
    },
    KNIGHT('N') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                    {1, -2}, {1, 2}, {2, -1}, {2, 1}
            };
        }
    },
    PAWN('P') {
        @Override
        public int[][] moves() {
            return new int[][]{
                    {1, 0},
                    {2, 0},
            };
        }
    };

    private final char symbol;

    Figure(char symbol) {
        this.symbol = symbol;
    }

    public static Figure from(String symbol) {
        return switch (symbol) {
            case "Q" -> QUEEN;
            case "K" -> KING;
            case "B" -> BISHOP;
            case "N" -> KNIGHT;
            case "R" -> ROOK;
            default -> PAWN;
        };
    }

    public char getSymbol() {
        return symbol;
    }

    public abstract int[][] moves();
}

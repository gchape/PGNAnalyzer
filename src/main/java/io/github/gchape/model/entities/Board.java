package io.github.gchape.model.entities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private final Map<Figure, Set<String>> black = Map.of(
            Figure.QUEEN, new HashSet<>(Set.of("d8")),
            Figure.KING, new HashSet<>(Set.of("e8")),
            Figure.ROOK, new HashSet<>(Set.of("a8", "h8")),
            Figure.BISHOP, new HashSet<>(Set.of("c8", "f8")),
            Figure.KNIGHT, new HashSet<>(Set.of("b8", "g8")),
            Figure.PAWN, new HashSet<>(Set.of("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"))
    );

    private final Map<Figure, Set<String>> white = Map.of(
            Figure.QUEEN, new HashSet<>(Set.of("d1")),
            Figure.KING, new HashSet<>(Set.of("e1")),
            Figure.ROOK, new HashSet<>(Set.of("a1", "h1")),
            Figure.BISHOP, new HashSet<>(Set.of("c1", "f1")),
            Figure.KNIGHT, new HashSet<>(Set.of("b1", "g1")),
            Figure.PAWN, new HashSet<>(Set.of("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"))
    );

    public void move(String move) {
        var moves = move.split(" ");
        var whiteMove = moves[0];
        var blackMove = moves[1];

        if (whiteMove.equals("O-O") || whiteMove.equals("O-O-O")) {
            castle(whiteMove, true);
        } else {
            move(whiteMove, Figure.from(whiteMove.substring(0, 1)), true);
        }

        if (blackMove.equals("O-O") || blackMove.equals("O-O-O")) {
            castle(blackMove, false);
        } else {
            move(blackMove, Figure.from(blackMove.substring(0, 1)), false);
        }
    }

    private void move(String move, Figure figure, boolean isWhite) {
        String destination;
        if (figure == Figure.PAWN)
            destination = move.substring(0, 2);
        else
            destination = move.substring(1, 3);

        Set<String> currentPositions = isWhite ? white.get(figure) : black.get(figure);

        String start = findStartPosition(currentPositions, destination, figure, isWhite);
        if (start == null) {
            throw new IllegalStateException("The piece cannot move to the destination.");
        }

        if (isValidMove(figure, start, destination, isWhite)) {
            currentPositions.remove(start);
            currentPositions.add(destination);
        } else {
            throw new IllegalArgumentException("Invalid move for " + figure);
        }
    }

    private void castle(String move, boolean isWhite) {
        Set<String> kingPositions = isWhite ? white.get(Figure.KING) : black.get(Figure.KING);
        Set<String> rookPositions = isWhite ? white.get(Figure.ROOK) : black.get(Figure.ROOK);
        String kingStart = isWhite ? "e1" : "e8";
        String rookStart = move.equals("O-O") ? (isWhite ? "h1" : "h8") : (isWhite ? "a1" : "a8");
        String kingEnd = move.equals("O-O") ? (isWhite ? "g1" : "g8") : (isWhite ? "c1" : "c8");
        String rookEnd = move.equals("O-O") ? (isWhite ? "f1" : "f8") : (isWhite ? "d1" : "d8");

        if (kingPositions.contains(kingStart) && rookPositions.contains(rookStart)) {
            if (isValidCastling(kingStart, rookStart, kingEnd, rookEnd)) {
                kingPositions.remove(kingStart);
                kingPositions.add(kingEnd);
                rookPositions.remove(rookStart);
                rookPositions.add(rookEnd);
            } else {
                throw new IllegalArgumentException("Invalid castling move.");
            }
        } else {
            throw new IllegalArgumentException("Castling not possible.");
        }
    }

    private boolean isValidCastling(String kingStart, String rookStart, String kingEnd, String rookEnd) {
        return true; // simplified, can add logic later if needed
    }

    private String findStartPosition(Set<String> currentPositions, String destination, Figure figure, boolean isWhite) {
        for (String currentPosition : currentPositions) {
            if (isValidMove(figure, currentPosition, destination, isWhite)) {
                return currentPosition;
            }
        }
        return null;
    }

    private boolean isValidMove(Figure figure, String start, String end, boolean isWhite) {
        int[] startXY = squareToXy(start);
        int[] endXY = squareToXy(end);
        int startX = startXY[0];
        int startY = startXY[1];
        int endX = endXY[0];
        int endY = endXY[1];

        boolean occupiedByOwn = (isWhite && white.values().stream().anyMatch(s -> s.contains(end)))
                || (!isWhite && black.values().stream().anyMatch(s -> s.contains(end)));

        if (occupiedByOwn) {
            return false;
        }

        if (figure == Figure.PAWN) {
            int direction = isWhite ? -1 : 1;
            int dy = endY - startY;
            int dx = endX - startX;

            if (dx == 0 && dy == direction) {
                return true;
            }

            if (dx == 0 && dy == 2 * direction && (isWhite ? startY == 6 : startY == 1)) {
                return true;
            }

            boolean capture = Math.abs(dx) == 1 && dy == direction;
            if (capture) {
                return (isWhite && black.values().stream().anyMatch(s -> s.contains(end)))
                        || (!isWhite && white.values().stream().anyMatch(s -> s.contains(end)));
            }

            return false;
        }

        for (int[] direction : figure.moves()) {
            int dx = direction[1];
            int dy = direction[0];

            int x = startX + dx;
            int y = startY + dy;

            if (figure == Figure.KNIGHT || figure == Figure.KING) {
                if (x == endX && y == endY) {
                    return true;
                }
            } else {
                while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                    String square = xyToSquare(x, y);
                    if ((isWhite && white.values().stream().anyMatch(s -> s.contains(square)))
                            || (!isWhite && black.values().stream().anyMatch(s -> s.contains(square)))) {
                        break;
                    }

                    if (x == endX && y == endY) {
                        return true;
                    }

                    x += dx;
                    y += dy;
                }
            }
        }

        return false;
    }

    private String xyToSquare(int x, int y) {
        return (char) ('a' + x) + Integer.toString(8 - y);
    }

    private int[] squareToXy(String square) {
        return new int[]{square.charAt(0) - 'a', 8 - Character.getNumericValue(square.charAt(1))};
    }
}

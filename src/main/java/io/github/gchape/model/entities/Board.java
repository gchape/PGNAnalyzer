package io.github.gchape.model.entities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private final Map<Piece, Set<String>> blackPieces = Map.of(
            Piece.QUEEN, new HashSet<>(Set.of("d8")),
            Piece.KING, new HashSet<>(Set.of("e8")),
            Piece.ROOK, new HashSet<>(Set.of("a8", "h8")),
            Piece.BISHOP, new HashSet<>(Set.of("c8", "f8")),
            Piece.KNIGHT, new HashSet<>(Set.of("b8", "g8")),
            Piece.PAWN, new HashSet<>(Set.of("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"))
    );

    private final Map<Piece, Set<String>> whitePieces = Map.of(
            Piece.QUEEN, new HashSet<>(Set.of("d1")),
            Piece.KING, new HashSet<>(Set.of("e1")),
            Piece.ROOK, new HashSet<>(Set.of("a1", "h1")),
            Piece.BISHOP, new HashSet<>(Set.of("c1", "f1")),
            Piece.KNIGHT, new HashSet<>(Set.of("b1", "g1")),
            Piece.PAWN, new HashSet<>(Set.of("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"))
    );

    public void tryMove(String move, boolean isWhite) throws IllegalArgumentException, IllegalStateException {
        if (move.contains("x")) {
            char pieceType = move.charAt(0);
            String toCapture = move.substring(2);
            switch (Piece.of(pieceType)) {
                case PAWN -> handlePawnCapture(isWhite, toCapture);
                case KNIGHT -> handleKnightCapture(isWhite, toCapture);
                case BISHOP -> handleDiagonalCapture(isWhite, toCapture);
                case ROOK -> handleStraightCapture(isWhite, toCapture);
                case QUEEN -> handleQueenCapture(isWhite, toCapture);
                case KING -> handleKingCapture(isWhite, toCapture);
            }
        } else if (move.contains("=")) {
            handlePawnPromotion(move, isWhite);
        } else {

        }
    }

    private void handlePawnCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.PAWN)
                .forEach(from -> {
                    var toYX = toYX(toCapture);
                    var fromYX = toYX(from);
                    if (canPawnCapture(fromYX, toYX, isWhite)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.PAWN);
                    }
                });
    }

    private boolean canPawnCapture(int[] fromYX, int[] toYX, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        return (fromYX[0] + direction == toYX[0] &&
                Math.abs(fromYX[1] - toYX[1]) == 1);
    }


    private void handleKnightCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.KNIGHT)
                .forEach(from -> {
                    if (isValidKnightCapture(from, toCapture)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.KNIGHT);
                    }
                });
    }

    private void handleDiagonalCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.BISHOP)
                .forEach(from -> {
                    if (isValidDiagonalCapture(from, toCapture)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.BISHOP);
                    }
                });
    }

    private void handleStraightCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.ROOK)
                .forEach(from -> {
                    if (isValidStraightCapture(from, toCapture)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.ROOK);
                    }
                });
    }

    private void handleQueenCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.QUEEN)
                .forEach(from -> {
                    if (isValidQueenCapture(from, toCapture)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.QUEEN);
                    }
                });
    }

    private void handleKingCapture(boolean isWhite, String toCapture) {
        (isWhite ? whitePieces : blackPieces).get(Piece.KING)
                .forEach(from -> {
                    if (isValidKingCapture(from, toCapture)) {
                        captureAndMovePiece(from, toCapture, isWhite, Piece.KING);
                    }
                });
    }

    private void captureAndMovePiece(String from, String toCapture, boolean isWhite, Piece pieceType) {
        (isWhite ? blackPieces : whitePieces).values().forEach(value -> value.remove(toCapture));
        (isWhite ? whitePieces : blackPieces).get(pieceType).remove(from);
        (isWhite ? whitePieces : blackPieces).get(pieceType).add(toCapture);
    }

    private void handlePawnPromotion(String move, boolean isWhite) {
        String from = move.substring(0, move.indexOf("="));
        char to = move.charAt(move.indexOf("=") + 1);

        if (isWhite) {
            whitePieces.get(Piece.PAWN).remove(from);
            whitePieces.get(Piece.of(to)).add(from);
        } else {
            blackPieces.get(Piece.PAWN).remove(from);
            blackPieces.get(Piece.of(to)).add(from);
        }
    }

    private boolean isValidKingCapture(String from, String to) {
        var fromYX = toYX(from);
        var toYX = toYX(to);

        return Math.abs(fromYX[0] - toYX[0]) <= 1 && Math.abs(fromYX[1] - toYX[1]) <= 1;
    }

    private int[] toYX(String notation) {
        return new int[]{8 - notation.charAt(1), notation.charAt(0) - 'a'};
    }

    private boolean isValidKnightCapture(String from, String to) {
        var fromYX = toYX(from);
        var toYX = toYX(to);

        return (Math.abs(fromYX[0] - toYX[0]) == 2 && Math.abs(fromYX[1] - toYX[1]) == 1) ||
                (Math.abs(fromYX[0] - toYX[0]) == 1 && Math.abs(fromYX[1] - toYX[1]) == 2);
    }

    private boolean isValidDiagonalCapture(String from, String to) {
        var fromYX = toYX(from);
        var toYX = toYX(to);

        return Math.abs(fromYX[0] - toYX[0]) == Math.abs(fromYX[1] - toYX[1]);
    }

    private boolean isValidStraightCapture(String from, String to) {
        var fromYX = toYX(from);
        var toYX = toYX(to);

        return fromYX[0] == toYX[0] || fromYX[1] == toYX[1];
    }

    private boolean isValidQueenCapture(String from, String to) {
        return isValidStraightCapture(from, to) || isValidDiagonalCapture(from, to);
    }
}

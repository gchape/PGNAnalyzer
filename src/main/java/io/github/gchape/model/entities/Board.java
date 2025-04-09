package io.github.gchape.model.entities;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Board {
    private final Map<Piece, Set<String>> blackPieces = new EnumMap<>(Piece.class);
    private final Map<Piece, Set<String>> whitePieces = new EnumMap<>(Piece.class);
    private boolean whiteKingMoved = false;
    private boolean whiteKingRookMoved = false;
    private boolean whiteQueenRookMoved = false;
    private boolean blackKingMoved = false;
    private boolean blackKingRookMoved = false;
    private boolean blackQueenRookMoved = false;
    private String enPassantTarget = null;

    public Board() {
        initializePieces();
    }

    private void initializePieces() {
        blackPieces.put(Piece.QUEEN, new HashSet<>(Set.of("d8")));
        blackPieces.put(Piece.KING, new HashSet<>(Set.of("e8")));
        blackPieces.put(Piece.ROOK, new HashSet<>(Set.of("a8", "h8")));
        blackPieces.put(Piece.BISHOP, new HashSet<>(Set.of("c8", "f8")));
        blackPieces.put(Piece.KNIGHT, new HashSet<>(Set.of("b8", "g8")));
        blackPieces.put(Piece.PAWN, new HashSet<>(Set.of("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7")));

        whitePieces.put(Piece.QUEEN, new HashSet<>(Set.of("d1")));
        whitePieces.put(Piece.KING, new HashSet<>(Set.of("e1")));
        whitePieces.put(Piece.ROOK, new HashSet<>(Set.of("a1", "h1")));
        whitePieces.put(Piece.BISHOP, new HashSet<>(Set.of("c1", "f1")));
        whitePieces.put(Piece.KNIGHT, new HashSet<>(Set.of("b1", "g1")));
        whitePieces.put(Piece.PAWN, new HashSet<>(Set.of("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2")));
    }

    public void tryMove(String move, boolean isWhite) throws IllegalArgumentException, IllegalStateException {
        if (Pattern.matches("^(1-0|0-1|1/2-1/2)$", move)) return;

        if (move.equals("O-O") || move.equals("O-O-O")) {
            handleCastling(move, isWhite);
        } else if (move.contains("x")) {
            handleCapture(move, isWhite);
        } else if (move.contains("=")) {
            handlePawnPromotion(move, isWhite);
        } else {
            handleRegularMove(move, isWhite);
        }
    }

    private void handleCastling(String move, boolean isWhite) {
        if (isWhite) {
            if (move.equals("O-O") && canWhiteCastleKingSide()) {
                performMove("e1", "g1", Piece.KING, true);
                performMove("h1", "f1", Piece.ROOK, true);
                whiteKingMoved = true;
                whiteKingRookMoved = true;
            } else if (move.equals("O-O-O") && canWhiteCastleQueenSide()) {
                performMove("e1", "c1", Piece.KING, true);
                performMove("a1", "d1", Piece.ROOK, true);
                whiteKingMoved = true;
                whiteQueenRookMoved = true;
            } else {
                throw new IllegalStateException("Invalid castling move for White.");
            }
        } else {
            if (move.equals("O-O") && canBlackCastleKingSide()) {
                performMove("e8", "g8", Piece.KING, false);
                performMove("h8", "f8", Piece.ROOK, false);
                blackKingMoved = true;
                blackKingRookMoved = true;
            } else if (move.equals("O-O-O") && canBlackCastleQueenSide()) {
                performMove("e8", "c8", Piece.KING, false);
                performMove("a8", "d8", Piece.ROOK, false);
                blackKingMoved = true;
                blackQueenRookMoved = true;
            } else {
                throw new IllegalStateException("Invalid castling move for Black.");
            }
        }
    }

    private boolean canWhiteCastleKingSide() {
        return !whiteKingMoved && !whiteKingRookMoved && isPathClear("f1", "g1");
    }

    private boolean canWhiteCastleQueenSide() {
        return !whiteKingMoved && !whiteQueenRookMoved && isPathClear("b1", "c1", "d1");
    }

    private boolean canBlackCastleKingSide() {
        return !blackKingMoved && !blackKingRookMoved && isPathClear("f8", "g8");
    }

    private boolean canBlackCastleQueenSide() {
        return !blackKingMoved && !blackQueenRookMoved && isPathClear("b8", "c8", "d8");
    }

    private boolean isPathClear(String... squares) {
        for (String square : squares) {
            if (isOccupied(square)) {
                return false;
            }
        }
        return true;
    }

    private boolean isOccupied(String square) {
        return whitePieces.values().stream().anyMatch(set -> set.contains(square)) || blackPieces.values().stream().anyMatch(set -> set.contains(square));
    }

    private void handleCapture(String move, boolean isWhite) {
        char pieceType = move.charAt(0);
        String toCapture = move.substring(2);

        switch (Piece.of(pieceType)) {
            case KING -> handleKingCapture(isWhite, toCapture);
            case QUEEN -> handleQueenCapture(isWhite, toCapture);
            case KNIGHT -> handleKnightCapture(isWhite, toCapture);
            case ROOK -> handleStraightCapture(isWhite, toCapture);
            case PAWN -> handlePawnCapture(isWhite, pieceType + "", toCapture);
            case BISHOP -> handleDiagonalCapture(isWhite, toCapture);
            default -> throw new IllegalArgumentException("Invalid piece type");
        }
    }

    private void handlePawnCapture(boolean isWhite, String flag, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        var opponentPieces = isWhite ? blackPieces : whitePieces;

        for (String from : pieces.get(Piece.PAWN)
                .stream()
                .filter(position -> position.contains(flag))
                .collect(Collectors.toSet())) {
            var fromYX = toYX(from);
            var toYX = toYX(toCapture);

            if (canPawnCapture(fromYX, toYX, isWhite)) {
                if (toCapture.equals(enPassantTarget)) {
                    String actualTarget = toCapture.charAt(0) + (isWhite ? "5" : "4");
                    opponentPieces.get(Piece.PAWN).remove(actualTarget);
                } else {
                    opponentPieces.values().forEach(set -> set.remove(toCapture));
                }

                pieces.get(Piece.PAWN).remove(from);
                pieces.get(Piece.PAWN).add(toCapture);
                enPassantTarget = null;
                return;
            }
        }

        throw new IllegalStateException("No valid pawn capture found");
    }

    private void handleKnightCapture(boolean isWhite, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        for (String from : pieces.get(Piece.KNIGHT)) {
            if (isValidKnightCapture(from, toCapture)) {
                pieces.get(Piece.KNIGHT).remove(from);
                pieces.get(Piece.KNIGHT).add(toCapture);
                return;
            }
        }
        throw new IllegalStateException("Invalid knight capture");
    }

    private void handleDiagonalCapture(boolean isWhite, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        for (String from : pieces.get(Piece.BISHOP)) {
            if (isValidDiagonalCapture(from, toCapture)) {
                pieces.get(Piece.BISHOP).remove(from);
                pieces.get(Piece.BISHOP).add(toCapture);
                return;
            }
        }
        throw new IllegalStateException("Invalid bishop capture");
    }

    private void handleStraightCapture(boolean isWhite, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        for (String from : pieces.get(Piece.ROOK)) {
            if (isValidStraightCapture(from, toCapture)) {
                pieces.get(Piece.ROOK).remove(from);
                pieces.get(Piece.ROOK).add(toCapture);
                return;
            }
        }
        throw new IllegalStateException("Invalid rook capture");
    }

    private void handleQueenCapture(boolean isWhite, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        for (String from : pieces.get(Piece.QUEEN)) {
            if (isValidQueenCapture(from, toCapture)) {
                pieces.get(Piece.QUEEN).remove(from);
                pieces.get(Piece.QUEEN).add(toCapture);
                return;
            }
        }
        throw new IllegalStateException("Invalid queen capture");
    }

    private void handleKingCapture(boolean isWhite, String toCapture) {
        var pieces = isWhite ? whitePieces : blackPieces;
        for (String from : pieces.get(Piece.KING)) {
            if (isValidKingCapture(from, toCapture)) {
                pieces.get(Piece.KING).remove(from);
                pieces.get(Piece.KING).add(toCapture);
                return;
            }
        }
        throw new IllegalStateException("Invalid king capture");
    }

    private void handleRegularMove(String move, boolean isWhite) {
        Map<Piece, Set<String>> pieces = isWhite ? whitePieces : blackPieces;

        if (move.length() == 2) {
            int direction = isWhite ? -1 : 1;
            int[] toYX = toYX(move);

            for (String from : new HashSet<>(pieces.get(Piece.PAWN))) {
                int[] fromYX = toYX(from);

                if (fromYX[1] == toYX[1] && fromYX[0] + direction == toYX[0]) {
                    enPassantTarget = null;
                    performMove(from, move, Piece.PAWN, isWhite);
                    return;
                }

                if (fromYX[1] == toYX[1] && (isWhite && fromYX[0] == 6 && toYX[0] == 4) || (!isWhite && fromYX[1] == toYX[1] && fromYX[0] == 1 && toYX[0] == 3)) {
                    enPassantTarget = move;
                    performMove(from, move, Piece.PAWN, isWhite);
                    return;
                }
            }

            throw new IllegalStateException("Invalid pawn move");
        }

        char type = move.charAt(0);
        String to = move.substring(1);
        Piece piece = Piece.of(type);

        Set<String> positions = new HashSet<>(pieces.get(piece));
        if (to.length() > 2) {
            String local = to;
            positions = positions
                    .stream()
                    .filter(position -> position.contains(local.substring(0, 1)))
                    .collect(Collectors.toSet());

            to = to.substring(1);
        }

        for (String from : positions) {
            if (isLegalNonCaptureMove(piece, from, to)) {
                performMove(from, to, piece, isWhite);
                updateCastlingFlags(piece, from, isWhite);
                enPassantTarget = null;
                return;
            }
        }

        throw new IllegalStateException("No valid move found for " + move);
    }

    private void updateCastlingFlags(Piece piece, String from, boolean isWhite) {
        if (piece == Piece.KING) {
            if (isWhite) whiteKingMoved = true;
            else blackKingMoved = true;
        } else if (piece == Piece.ROOK) {
            if (isWhite) {
                if (from.equals("h1")) whiteKingRookMoved = true;
                if (from.equals("a1")) whiteQueenRookMoved = true;
            } else {
                if (from.equals("h8")) blackKingRookMoved = true;
                if (from.equals("a8")) blackQueenRookMoved = true;
            }
        }
    }

    private boolean isLegalNonCaptureMove(Piece piece, String from, String to) {
        return switch (piece) {
            case KNIGHT -> isValidKnightCapture(from, to);
            case BISHOP -> isValidDiagonalCapture(from, to);
            case ROOK -> isValidStraightCapture(from, to);
            case QUEEN -> isValidQueenCapture(from, to);
            case KING -> isValidKingCapture(from, to);
            default -> false;
        };
    }

    private void performMove(String from, String to, Piece piece, boolean isWhite) {
        Map<Piece, Set<String>> pieces = isWhite ? whitePieces : blackPieces;
        pieces.get(piece).remove(from);
        pieces.get(piece).add(to);
    }

    private boolean canPawnCapture(int[] fromYX, int[] toYX, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        return (fromYX[0] + direction == toYX[0] && Math.abs(fromYX[1] - toYX[1]) == 1);
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

    private int[] toYX(String position) {
        return new int[]{8 - Character.getNumericValue(position.charAt(1)), position.charAt(0) - 'a'};
    }

    private boolean isValidKnightCapture(String from, String to) {
        var fromYX = toYX(from);
        var toYX = toYX(to);

        return (Math.abs(fromYX[0] - toYX[0]) == 2 && Math.abs(fromYX[1] - toYX[1]) == 1) || (Math.abs(fromYX[0] - toYX[0]) == 1 && Math.abs(fromYX[1] - toYX[1]) == 2);
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

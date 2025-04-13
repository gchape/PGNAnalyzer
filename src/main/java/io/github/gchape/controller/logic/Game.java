package io.github.gchape.controller.logic;

import io.github.gchape.model.Model;
import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import io.github.gchape.model.entities.Square;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Game implements Runnable {
    private final Board board;
    private final String moves;
    private final Map<String, String> headers;
    private final Model model = Model.getInstance();

    private final static AtomicInteger id = new AtomicInteger(0);

    private boolean kingMoved;
    private boolean rookMoved;

    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;

        this.board = new Board();
    }

    @Override
    public void run() {
        printHeaders();

        var i = 0;
        for (var move : moves.split(" ")) {
            boolean isWhite = i % 2 == 0;

            if (move.equals("0-0")) {
                tryCastle(isWhite, true);
            } else if (move.equals("0-0-0")) {
                tryCastle(isWhite, false);
            } else if (move.contains("=") && move.contains("x")) {
                tryCaptureAndPromotion(isWhite, move);
            } else if (move.contains("=")) {
                tryPromotion(isWhite, move);
            } else if (move.contains("x")) {
                tryCapture(isWhite, move);
            } else {
                tryMove(isWhite, move);
            }
            i++;
        }
    }

    private void tryMove(boolean isWhite, String move) {

    }

    private void tryCapture(boolean isWhite, String move) {
        final String to = move.substring(move.indexOf('x') + 1, move.indexOf('x') + 3);
        final String disambiguation = move.indexOf('x') - 1 == 0 ? null : move.substring(1, move.indexOf('x'));

        switch (Piece.of(move.charAt(0))) {
            case KING -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                var startSquare = getPieceSquare(startPieces, Piece.KING, targetSquare, disambiguation);

                moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.KING);
            }
            case QUEEN -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                var startSquare = getPieceSquare(startPieces, Piece.QUEEN, targetSquare, disambiguation);

                moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.QUEEN);
            }
            case BISHOP -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                var startSquare = getPieceSquare(startPieces, Piece.BISHOP, targetSquare, disambiguation);

                moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.BISHOP);
            }
            case ROOK -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                var startSquare = getPieceSquare(startPieces, Piece.ROOK, targetSquare, disambiguation);

                moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.ROOK);
            }
            case KNIGHT -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                var startSquare = getPieceSquare(startPieces, Piece.KNIGHT, targetSquare, disambiguation);

                moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.KNIGHT);
            }
            case PAWN -> {
                var startPieces = getPiecesByColor(isWhite);
                var targetPieces = getPiecesByColor(!isWhite);

                var targetSquare = new Square(to);
                Square startSquare = getPieceSquare(startPieces, Piece.PAWN, targetSquare, move.substring(0, move.indexOf('x')));

                if (isEnPassant(startSquare, targetSquare, isWhite)) {
                    enPassantCapture(startPieces, targetPieces, startSquare, targetSquare);
                } else {
                    moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.PAWN);
                }
            }
        }
    }

    private void tryPromotion(boolean isWhite, String move) {
        String square = move.substring(0, 2);
        char promotedPieceSymbol = move.charAt(3);
        Piece promotedPiece;

        try {
            promotedPiece = Piece.of(promotedPieceSymbol);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid promotion piece: '" + promotedPieceSymbol +
                    "'. Must be one of Q, R, B, N.");
        }

        if (isWhite && square.charAt(1) != '8') {
            throw new IllegalArgumentException("Invalid promotion square '" + square +
                    "' for White. Pawn must reach rank 8.");
        } else if (!isWhite && square.charAt(1) != '1') {
            throw new IllegalArgumentException("Invalid promotion square '" + square +
                    "' for Black. Pawn must reach rank 1.");
        }

        Map<Piece, Set<String>> pieces = getPiecesByColor(isWhite);
        String pawnPosition = getPawnPositionForPromotion(isWhite, square);
        Set<String> pawns = pieces.get(Piece.PAWN);

        if (!pawns.contains(pawnPosition)) {
            throw new IllegalStateException("No " + (isWhite ? "White" : "Black") +
                    " pawn found at expected square '" + pawnPosition +
                    "' to promote to " + promotedPiece + ".");
        }

        pawns.remove(pawnPosition);
        pieces.get(promotedPiece).add(square);
    }

    private void tryCaptureAndPromotion(boolean isWhite, String move) {
        int x = move.indexOf('x');
        int equalIndex = move.indexOf('=');

        char from = move.charAt(0);
        String to = move.substring(x + 1, equalIndex);
        char promotedPieceSymbol = move.charAt(equalIndex + 1);

        Piece promotedPiece;
        try {
            promotedPiece = Piece.of(promotedPieceSymbol);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid promotion piece in move '" + move + "'. Must be one of Q, R, B, N.");
        }

        Square targetSquare = new Square(to);
        Map<Piece, Set<String>> startPieces = getPiecesByColor(isWhite);
        Map<Piece, Set<String>> targetPieces = getPiecesByColor(!isWhite);

        String fromRank = isWhite ? "7" : "2";
        String fromSquare = from + fromRank;

        if (!startPieces.get(Piece.PAWN).contains(fromSquare)) {
            throw new IllegalStateException("No " + (isWhite ? "White" : "Black") +
                    " pawn found at " + fromSquare + " to perform capture and promotion.");
        }

        for (Set<String> pieces : targetPieces.values()) {
            pieces.remove(targetSquare.toChessNotation());
        }

        startPieces.get(Piece.PAWN).remove(fromSquare);
        startPieces.get(promotedPiece).add(to);
    }

    private String getPawnPositionForPromotion(boolean isWhite, String square) {
        if (isWhite) {
            return square.charAt(0) + "7";
        } else {
            return square.charAt(0) + "2";
        }
    }

    private Map<Piece, Set<String>> getPiecesByColor(boolean isWhite) {
        return isWhite ? board.getWhitePieces() : board.getBlackPieces();
    }

    private Square getPieceSquare(Map<Piece, Set<String>> pieces, Piece piece, Square targetSquare, String disambiguation) {
        return pieces.get(piece)
                .stream()
                .map(Square::new)
                .filter(startSquare -> isValidMove(startSquare, targetSquare, piece))
                .filter(startSquare -> disambiguation == null || startSquare.toChessNotation().contains(disambiguation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find a valid " + piece +
                        " to move to " + targetSquare.toChessNotation() +
                        (disambiguation != null ? " with disambiguation '" + disambiguation + "'" : "") + "."));
    }

    private void moveAndCapturePiece(Map<Piece, Set<String>> startPieces, Map<Piece, Set<String>> targetPieces,
                                     Square startSquare, Square targetSquare, Piece piece) {
        startPieces.get(piece).remove(startSquare.toChessNotation());
        startPieces.get(piece).add(targetSquare.toChessNotation());

        for (Set<String> value : targetPieces.values()) {
            value.remove(targetSquare.toChessNotation());
        }
    }

    private boolean isEnPassant(Square startSquare, Square targetSquare, boolean isWhite) {
        int dx = targetSquare.x() - startSquare.x();
        int dy = targetSquare.y() - startSquare.y();

        if (Math.abs(dx) == 1 && dy == (isWhite ? -1 : 1)) {
            Square capturedSquare = new Square(targetSquare.x(), startSquare.y());

            if (isWhite) {
                return board.getBlackPieces().get(Piece.PAWN).contains(capturedSquare.toChessNotation());
            } else {
                return board.getWhitePieces().get(Piece.PAWN).contains(capturedSquare.toChessNotation());
            }
        }

        return false;
    }

    private void enPassantCapture(Map<Piece, Set<String>> startPieces, Map<Piece, Set<String>> targetPieces, Square startSquare, Square targetSquare) {
        Square capturedSquare = new Square(targetSquare.x(), startSquare.y());

        var opponentPieces = targetPieces.get(Piece.PAWN);
        opponentPieces.remove(capturedSquare.toChessNotation());

        moveAndCapturePiece(startPieces, targetPieces, startSquare, targetSquare, Piece.PAWN);
    }

    private boolean isValidMove(Square startSquare, Square targetSquare, Piece piece) {
        return switch (piece) {
            case KING -> startSquare.isKingMoveTo(targetSquare);
            case QUEEN -> startSquare.isQueenMoveTo(targetSquare);
            case BISHOP -> startSquare.isBishopMoveTo(targetSquare);
            case ROOK -> startSquare.isRookMoveTo(targetSquare);
            case KNIGHT -> startSquare.isKnightMoveTo(targetSquare);
            case PAWN -> startSquare.isPawnMoveTo(targetSquare);
        };
    }

    public void tryCastle(boolean isWhite, boolean kingSide) {
        String side = isWhite ? "White" : "Black";
        String direction = kingSide ? "KingSide" : "QueenSide";

        if (kingMoved || rookMoved) {
            throw new IllegalStateException(side + " cannot castle " + direction +
                    ": either the king or rook has already moved.");
        }

        if (!isVacant(isWhite, kingSide)) {
            throw new IllegalStateException(side + " cannot castle " + direction +
                    ": squares between king and rook are not vacant.");
        }

        Map<String, String[]> castlingSquares = Map.of(
                "WhiteKingSide", new String[]{"e1", "g1", "h1", "f1"},
                "WhiteQueenSide", new String[]{"e1", "c1", "a1", "d1"},
                "BlackKingSide", new String[]{"e8", "g8", "h8", "f8"},
                "BlackQueenSide", new String[]{"e8", "c8", "a8", "d8"}
        );

        String key = (side) + (direction);
        String[] squares = castlingSquares.get(key);

        Map<Piece, Set<String>> pieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        pieces.get(Piece.KING).remove(squares[0]);
        pieces.get(Piece.KING).add(squares[1]);
        pieces.get(Piece.ROOK).remove(squares[2]);
        pieces.get(Piece.ROOK).add(squares[3]);
    }

    private boolean isVacant(boolean isWhite, boolean kingSide) {
        Map<String, List<String>> vacantSquares = Map.of(
                "WhiteKingSide", List.of("f1", "g1"),
                "WhiteQueenSide", List.of("b1", "c1", "d1"),
                "BlackKingSide", List.of("f8", "g8"),
                "BlackQueenSide", List.of("b8", "c8", "d8")
        );

        String key = (isWhite ? "White" : "Black") + (kingSide ? "KingSide" : "QueenSide");
        List<String> requiredEmptySquares = vacantSquares.get(key);

        Set<String> allOccupiedSquares = new HashSet<>();
        board.getWhitePieces().values().forEach(allOccupiedSquares::addAll);
        board.getBlackPieces().values().forEach(allOccupiedSquares::addAll);

        for (String square : requiredEmptySquares) {
            if (allOccupiedSquares.contains(square)) {
                return false;
            }
        }

        return true;
    }

    private void printHeaders() {
        var event = headers.get("Event");
        var round = headers.get("Round");
        var white = headers.get("White");
        var black = headers.get("Black");
        var result = headers.get("Result");

        model.textInputProperty().set("""
                {
                Event: "%s",
                White: "%s",
                Black: "%s",
                Round: "%s",
                Result: "%s"
                },
                """.formatted(event, white, black, round, result));
    }
}

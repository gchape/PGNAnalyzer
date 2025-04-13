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
                .filter(startSquare -> {
                    if (disambiguation != null) {
                        return startSquare.toChessNotation().contains(disambiguation);
                    } else {
                        return true;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Can not find " + piece + " to capture piece at " + targetSquare.toChessNotation()));
    }

    private void moveAndCapturePiece(Map<Piece, Set<String>> startPieces, Map<Piece, Set<String>> targetPieces,
                                     Square startSquare, Square targetSquare, Piece piece) {
        startPieces.get(piece).remove(startSquare.toChessNotation());
        startPieces.get(piece).add(targetSquare.toChessNotation());

        for (Set<String> value : targetPieces.values()) {
            value.remove(targetSquare.toChessNotation());
        }
    }

    private boolean isValidMove(Square startSquare, Square targetSquare, Piece piece) {
        switch (piece) {
            case KING:
                return startSquare.isKingMoveTo(targetSquare);
            case QUEEN:
                return startSquare.isQueenMoveTo(targetSquare);
            case BISHOP:
                return startSquare.isBishopMoveTo(targetSquare);
            case ROOK:
                return startSquare.isRookMoveTo(targetSquare);
            case KNIGHT:
                return startSquare.isKnightMoveTo(targetSquare);
            default:
                throw new IllegalStateException("Unsupported piece type: " + piece);
        }
    }

    public void tryCastle(boolean isWhite, boolean kingSide) {
        if (kingMoved || rookMoved) {
            throw new IllegalStateException("Cannot castle! Either king or rook has moved!");
        }

        if (!isVacant(isWhite, kingSide)) {
            throw new IllegalStateException("Cannot castle! Squares between the king and rook are not vacant!");
        }

        Map<String, String[]> castlingSquares = Map.of(
                "WhiteKingSide", new String[]{"e1", "g1", "h1", "f1"},
                "WhiteQueenSide", new String[]{"e1", "c1", "a1", "d1"},
                "BlackKingSide", new String[]{"e8", "g8", "h8", "f8"},
                "BlackQueenSide", new String[]{"e8", "c8", "a8", "d8"}
        );

        String key = (isWhite ? "White" : "Black") + (kingSide ? "KingSide" : "QueenSide");
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

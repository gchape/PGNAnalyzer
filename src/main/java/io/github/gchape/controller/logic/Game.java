package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import io.github.gchape.model.entities.Printer;
import io.github.gchape.model.entities.Square;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Game implements Runnable {
    private final Board board;
    private final String moves;
    private final Map<String, String> headers;

    private final static AtomicInteger id = new AtomicInteger(0);

    private Square lastDoubleStepPawnSquare = null;

    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;

        this.board = new Board();
    }

    @Override
    public void run() {
        Printer.INSTANCE.appendHead(headers);

        boolean isValid = true;
        boolean isWhite = false;
        try {
            for (var move : moves.split(" ")) {
                if (move.matches("^(1-0|0-1|1/2-1/2|\\*)$")) return;

                isWhite = !isWhite;
                if (move.equals("O-O")) {
                    board.tryCastle(isWhite, true);
                } else if (move.equals("O-O-O")) {
                    board.tryCastle(isWhite, false);
                } else if (move.contains("=") && move.contains("x")) {
                    tryCaptureAndPromotion(isWhite, move);
                } else if (move.contains("=")) {
                    board.tryPromotion(isWhite, move);
                } else tryMove(isWhite, move.contains("x"), move);
            }
        } catch (Exception e) {
            isValid = false;

            throw new RuntimeException(e);
        } finally {
            Printer.INSTANCE.appendBody(id.incrementAndGet(), isValid);
        }
    }

    private void tryMove(final boolean isWhite, final boolean isCapture, final String move) {
        final var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        final var opponentPieces = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        var piece = Piece.of(move.charAt(0));

        if (isCapture) {
            var targetSquare = new Square(move.substring(move.indexOf('x') + 1));
            var disambiguation = move.indexOf('x') - 1 == 0 ? null : move.substring(1, move.indexOf('x'));

            if (piece == Piece.PAWN) {
                Square startSquare = board.findPiece(Piece.PAWN, targetSquare, move.substring(0, 1), isWhite, true);

                if (isEnPassant(startSquare, targetSquare, isWhite)) {
                    currentPieces.get(Piece.PAWN).remove(startSquare.toChessNotation());
                    currentPieces.get(Piece.PAWN).add(targetSquare.toChessNotation());

                    opponentPieces.get(Piece.PAWN).remove(lastDoubleStepPawnSquare.toChessNotation());
                } else {
                    capturePiece(currentPieces, opponentPieces, startSquare, targetSquare, Piece.PAWN);
                }
            } else {
                var startSquare = board.findPiece(piece, targetSquare, disambiguation, isWhite, true);

                capturePiece(currentPieces, opponentPieces, startSquare, targetSquare, piece);
            }
        } else {
            var targetSquare = new Square(move.substring(move.length() - 2));
            var disambiguation = move.length() > 3 ? move.substring(1, move.length() - 2) : null;
            var startSquare = board.findPiece(piece, targetSquare, disambiguation, isWhite, false);

            board.move(currentPieces, startSquare, targetSquare, isWhite, piece);

            lastDoubleStepPawnSquare = Math.abs(targetSquare.y() - startSquare.y()) == 2 ? targetSquare : null;
        }
    }

    private void tryCaptureAndPromotion(final boolean isWhite, final String move) {
        final int i = move.indexOf('x'); // bxa1=Q
        final int j = move.indexOf('=');

        tryMove(isWhite, true, move.substring(0, j));

        var pawnSquare = move.substring(i + 1, j);
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        currentPieces.get(Piece.PAWN).remove(pawnSquare);
        currentPieces.get(Piece.of(move.charAt(j + 1))).add(pawnSquare);
    }

    private void capturePiece(final Map<Piece, Set<String>> startPieces, final Map<Piece, Set<String>> targetPieces,
                              final Square startSquare, final Square targetSquare, final Piece piece) {
        startPieces.get(piece).remove(startSquare.toChessNotation());
        startPieces.get(piece).add(targetSquare.toChessNotation());

        targetPieces.values().forEach(squares -> squares.remove(targetSquare.toChessNotation()));
    }

    private boolean isEnPassant(final Square startSquare, final Square targetSquare, final boolean isWhite) {
        final int dx = targetSquare.x() - startSquare.x();
        final int dy = targetSquare.y() - startSquare.y();

        if (Math.abs(dx) == 1 && dy == (isWhite ? 1 : -1)) {
            final var capturedSquare = new Square(targetSquare.x(), startSquare.y());

            if (!capturedSquare.equals(lastDoubleStepPawnSquare)) {
                return false;
            }

            if (isWhite) {
                return board.getBlackPieces().get(Piece.PAWN).contains(capturedSquare.toChessNotation());
            } else {
                return board.getWhitePieces().get(Piece.PAWN).contains(capturedSquare.toChessNotation());
            }
        }

        return false;
    }
}

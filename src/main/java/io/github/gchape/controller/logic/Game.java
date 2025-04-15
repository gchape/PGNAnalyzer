package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import io.github.gchape.model.entities.Printer;
import io.github.gchape.model.entities.Square;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code Game} class represents a chess game and its logic.
 * It is responsible for processing chess moves, validating them,
 * updating the game state, and printing the game record.
 * It implements the {@link Runnable} interface to run the game moves in a separate thread.
 */
public class Game implements Runnable {
    private final Board board;
    private final String moves;
    private final Map<String, String> headers;

    private final static AtomicInteger id = new AtomicInteger(0);

    private Square lastDoubleStepPawnSquare = null;

    /**
     * Constructs a new {@code Game} instance with the provided headers and moves.
     *
     * @param headers A map of headers containing game metadata.
     * @param moves   A string of chess moves in algebraic notation.
     */
    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;

        this.board = new Board();
    }

    /**
     * Runs the game by processing each move in the provided move sequence.
     * It validates moves, handles castling, captures, promotions, and updates the game state.
     */
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

    /**
     * Tries to move a piece based on the move string.
     * It handles piece movement, captures, and checks for special cases like en passant.
     *
     * @param isWhite   Indicates if the move is for the white player.
     * @param isCapture Indicates if the move is a capture.
     * @param move      The chess move in algebraic notation.
     */
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

    /**
     * Handles pawn capture and promotion, typically for moves like "bxa1=Q".
     *
     * @param isWhite Indicates if the move is for the white player.
     * @param move    The move string in algebraic notation.
     */
    private void tryCaptureAndPromotion(final boolean isWhite, final String move) {
        final int i = move.indexOf('x'); // bxa1=Q
        final int j = move.indexOf('=');

        tryMove(isWhite, true, move.substring(0, j));

        var pawnSquare = move.substring(i + 1, j);
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        currentPieces.get(Piece.PAWN).remove(pawnSquare);
        currentPieces.get(Piece.of(move.charAt(j + 1))).add(pawnSquare);
    }

    /**
     * Captures an opponent's piece by updating the board and removing the piece from the opponent's set of pieces.
     *
     * @param startPieces    The current player's pieces.
     * @param targetPieces   The opponent's pieces.
     * @param startSquare    The starting square of the piece.
     * @param targetSquare   The target square of the piece.
     * @param piece          The type of the piece being moved.
     */
    private void capturePiece(final Map<Piece, Set<String>> startPieces, final Map<Piece, Set<String>> targetPieces,
                              final Square startSquare, final Square targetSquare, final Piece piece) {
        startPieces.get(piece).remove(startSquare.toChessNotation());
        startPieces.get(piece).add(targetSquare.toChessNotation());

        targetPieces.values().forEach(squares -> squares.remove(targetSquare.toChessNotation()));
    }

    /**
     * Checks if a move is an en passant capture.
     *
     * @param startSquare   The starting square of the pawn.
     * @param targetSquare  The target square of the pawn.
     * @param isWhite       Indicates if the move is for the white player.
     * @return {@code true} if the move is an en passant capture, {@code false} otherwise.
     */
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

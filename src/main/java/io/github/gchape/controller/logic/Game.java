package io.github.gchape.controller.logic;

import io.github.gchape.exceptions.InvalidCastlingException;
import io.github.gchape.exceptions.InvalidPromotionException;
import io.github.gchape.exceptions.NoPieceFoundException;
import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import io.github.gchape.model.entities.Printer;
import io.github.gchape.model.entities.Square;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Game implements Runnable {
    private final static AtomicInteger id = new AtomicInteger(0);

    private final Board board;
    private final String moves;
    private final Map<String, String> headers;
    private final Map<String, Boolean> castlingCheck;

    private Square lastDoubleStepPawnSquare = null;

    public Game(final Map<String, String> headers, final String moves) {
        this.moves = moves;
        this.headers = headers;

        this.board = new Board();
        castlingCheck = new HashMap<>(6);
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
                    tryCastle(isWhite, true);
                } else if (move.equals("O-O-O")) {
                    tryCastle(isWhite, false);
                } else if (move.contains("=") && move.contains("x")) {
                    tryCaptureAndPromotion(isWhite, move);
                } else if (move.contains("x")) {
                    tryCapture(isWhite, move);
                } else if (move.contains("=")) {
                    tryPromotion(isWhite, move);
                } else tryMove(isWhite, move);
            }
        } catch (Exception e) {
            isValid = false;

            throw new RuntimeException(e);
        } finally {
            Printer.INSTANCE.appendBody(id.incrementAndGet(), isValid);
        }
    }

    /**
     * Attempts to capture a piece during a move. If the move is a capture, it removes the captured piece
     * from the opponent's set of pieces and updates the current player's pieces.
     *
     * @param isWhite A boolean indicating whether the current player is white.
     * @param move    A string representing the move (e.g., "Nxe5").
     */
    private void tryCapture(final boolean isWhite, final String move) {
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        var opponentPieces = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        var targetSquare = new Square(move.substring(move.indexOf('x') + 1));
        var disambiguation = move.indexOf('x') - 1 == 0 ? null : move.substring(1, move.indexOf('x'));

        var piece = Piece.of(move.charAt(0));
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

            checkIfCastlePieceMoved(startSquare);
        }
    }

    /**
     * Checks if a piece involved in a move is part of castling. If the piece is a king or rook,
     * it marks it as having moved to prevent castling in future moves.
     *
     * @param startSquare The starting square of the piece that is moving.
     */
    private void checkIfCastlePieceMoved(final Square startSquare) {
        var squares = Set.of("a1", "e1", "h1", "a8", "e8", "h8");

        if (squares.contains(startSquare.toChessNotation())) {
            castlingCheck.put(startSquare.toChessNotation(), true);
        }
    }

    /**
     * Attempts to move a piece on the board. If the move is valid, it updates the piece's position.
     *
     * @param isWhite A boolean indicating whether the current player is white.
     * @param move    A string representing the move (e.g., "Nf3").
     */
    private void tryMove(final boolean isWhite, final String move) {
        var piece = Piece.of(move.charAt(0));
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        var targetSquare = new Square(move.substring(move.length() - 2));
        var disambiguation = move.length() > 3 ? move.substring(1, move.length() - 2) : null;
        var startSquare = board.findPiece(piece, targetSquare, disambiguation, isWhite, false);

        currentPieces.get(piece).remove(startSquare.toChessNotation());
        currentPieces.get(piece).add(targetSquare.toChessNotation());

        checkIfCastlePieceMoved(startSquare);
        lastDoubleStepPawnSquare = Math.abs(targetSquare.y() - startSquare.y()) == 2 ? targetSquare : null;
    }

    /**
     * Attempts to perform both a capture and a promotion in a single move (e.g., "bxa1=Q").
     * It first performs the capture, then promotes the pawn to the specified piece.
     *
     * @param isWhite A boolean indicating whether the current player is white.
     * @param move    A string representing the move (e.g., "bxa1=Q").
     */
    private void tryCaptureAndPromotion(final boolean isWhite, final String move) {
        final int i = move.indexOf('x'); // bxa1=Q
        final int j = move.indexOf('=');

        tryCapture(isWhite, move.substring(0, j));

        var pawnSquare = move.substring(i + 1, j);
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        currentPieces.get(Piece.PAWN).remove(pawnSquare);
        currentPieces.get(Piece.of(move.charAt(j + 1))).add(pawnSquare);
    }

    /**
     * Captures a piece by removing it from the target player's set of pieces and adding the captured
     * piece's position to the attacking player's set of pieces.
     *
     * @param startPieces  The set of pieces of the attacking player.
     * @param targetPieces The set of pieces of the defending player.
     * @param startSquare  The starting square of the piece being captured.
     * @param targetSquare The target square of the capture.
     * @param piece        The type of piece performing the capture.
     */
    private void capturePiece(final Map<Piece, Set<String>> startPieces, final Map<Piece, Set<String>> targetPieces,
                              final Square startSquare, final Square targetSquare, final Piece piece) {
        startPieces.get(piece).remove(startSquare.toChessNotation());
        startPieces.get(piece).add(targetSquare.toChessNotation());

        targetPieces.values().forEach(squares -> squares.remove(targetSquare.toChessNotation()));
    }

    /**
     * Checks if a pawn move qualifies as an "en passant" capture, where a pawn captures an opponent's
     * pawn that has just moved two squares forward from its starting position.
     *
     * @param startSquare  The starting square of the pawn making the move.
     * @param targetSquare The target square where the pawn is moving.
     * @param isWhite      A boolean indicating whether the current player is white.
     * @return True if the move is an en passant capture, false otherwise.
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

    /**
     * Attempts to perform castling for the given player and side (king-side or queen-side).
     *
     * @param isWhite  A boolean indicating whether the player is white.
     * @param kingSide A boolean indicating whether the castling is king-side (true) or queen-side (false).
     * @throws InvalidCastlingException If castling is not allowed based on the current game state.
     */
    public void tryCastle(final boolean isWhite, final boolean kingSide) {
        var side = isWhite ? "White" : "Black";
        var direction = kingSide ? "KingSide" : "QueenSide";

        var squares = getCastleType(side, direction);

        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        currentPieces.get(Piece.KING).remove(squares[0]);
        currentPieces.get(Piece.KING).add(squares[1]);
        currentPieces.get(Piece.ROOK).remove(squares[2]);
        currentPieces.get(Piece.ROOK).add(squares[3]);
    }

    /**
     * Retrieves the appropriate squares for castling based on the player's color and castling side (king-side or queen-side).
     *
     * @param side      The side (either "White" or "Black").
     * @param direction The direction (either "KingSide" or "QueenSide").
     * @return An array of strings representing the squares for castling.
     * @throws InvalidCastlingException If castling is not allowed for the given side and direction.
     */
    private String[] getCastleType(final String side, final String direction) {
        var castlingSquares = Map.of("WhiteKingSide", new String[]{"e1", "g1", "h1", "f1"},
                "WhiteQueenSide", new String[]{"e1", "c1", "a1", "d1"},
                "BlackKingSide", new String[]{"e8", "g8", "h8", "f8"},
                "BlackQueenSide", new String[]{"e8", "c8", "a8", "d8"});

        String[] squares = castlingSquares.get(side + direction);
        if (castlingCheck.containsKey(squares[0]) || castlingCheck.containsKey(squares[2]))
            throw new InvalidCastlingException(side, direction);

        return squares;
    }

    /**
     * Attempts to perform pawn promotion when a pawn reaches the last rank.
     *
     * @param isWhite A boolean indicating whether the player is white.
     * @param move    The move string containing the pawn promotion.
     * @throws InvalidPromotionException If the promotion is not valid based on the current position.
     * @throws NoPieceFoundException     If the pawn to promote cannot be found.
     */
    public void tryPromotion(final boolean isWhite, final String move) {
        var square = move.substring(0, 2);
        var piece = Piece.of(move.charAt(3));
        var currentPieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();

        var pawnPosition = isWhite ? square.charAt(0) + "7" : square.charAt(0) + "2";
        if (isWhite && square.charAt(1) != '8' || !isWhite && square.charAt(1) != '1') {
            throw new InvalidPromotionException(square);
        } else if (!currentPieces.get(Piece.PAWN).contains(pawnPosition)) {
            throw new NoPieceFoundException(Piece.PAWN, pawnPosition);
        }

        currentPieces.get(piece).add(square);
        currentPieces.get(Piece.PAWN).remove(pawnPosition);
    }
}

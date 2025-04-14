package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import io.github.gchape.model.entities.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TryCastleTest {
    private Board board;

    @BeforeEach
    public void setup() {
        board = new Board();
    }

    /**
     * Test for valid white king-side castling.
     * Ensures that the white king and rook can castle to the king-side.
     */
    @Test
    public void testValidWhiteKingSideCastling() {
        board.getWhitePieces().values().forEach(squares -> {
            squares.remove("f1");
            squares.remove("g1");
        });

        board.tryCastle(true, true);

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("g1"));
        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("f1"));
        assertFalse(board.getWhitePieces().get(Piece.KING).contains("e1"));
        assertFalse(board.getWhitePieces().get(Piece.ROOK).contains("h1"));
    }

    /**
     * Test for valid white queen-side castling.
     * Ensures that the white king and rook can castle to the queen-side.
     */
    @Test
    public void testValidWhiteQueenSideCastling() {
        board.getWhitePieces().values().forEach(squares -> {
            squares.remove("b1");
            squares.remove("c1");
            squares.remove("d1");
        });

        board.tryCastle(true, false);

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("c1"));
        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("d1"));
        assertFalse(board.getWhitePieces().get(Piece.KING).contains("e1"));
        assertFalse(board.getWhitePieces().get(Piece.ROOK).contains("a1"));
    }

    /**
     * Test for invalid white king-side castling when the white king has moved.
     * Ensures that castling is not allowed if the king has already moved.
     */
    @Test
    public void testInvalidWhiteKingSideCastlingKingMoved() {
        board.move(board.getWhitePieces(), new Square("e1"), new Square("e2"), true, Piece.KING);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(true, true));
    }

    /**
     * Test for invalid white king-side castling when the white rook has moved.
     * Ensures that castling is not allowed if the rook has already moved.
     */
    @Test
    public void testInvalidWhiteKingSideCastlingRookMoved() {
        board.move(board.getWhitePieces(), new Square("h1"), new Square("h2"), true, Piece.ROOK);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(true, true));
    }

    /**
     * Test for invalid white king-side castling when the squares between the king and rook are not vacant.
     * Ensures that castling is not allowed if the squares are blocked.
     */
    @Test
    public void testInvalidWhiteKingSideCastlingBlocked() {
        board.getWhitePieces().get(Piece.KNIGHT).add("f1"); // Place a piece on f1

        assertThrows(IllegalStateException.class, () -> board.tryCastle(true, true));
    }

    /**
     * Test for valid black king-side castling.
     * Ensures that the black king and rook can castle to the king-side.
     */
    @Test
    public void testValidBlackKingSideCastling() {
        board.getBlackPieces().values().forEach(squares -> {
            squares.remove("g8");
            squares.remove("f8");
        });

        board.tryCastle(false, true);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("g8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("f8"));
        assertFalse(board.getBlackPieces().get(Piece.KING).contains("e8"));
        assertFalse(board.getBlackPieces().get(Piece.ROOK).contains("h8"));
    }

    /**
     * Test for invalid black king-side castling when the black king has moved.
     * Ensures that castling is not allowed if the king has already moved.
     */
    @Test
    public void testInvalidBlackKingSideCastlingKingMoved() {
        board.move(board.getBlackPieces(), new Square("e8"), new Square("e7"), false, Piece.KING);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, true));
    }

    /**
     * Test for invalid black king-side castling when the black rook has moved.
     * Ensures that castling is not allowed if the rook has already moved.
     */
    @Test
    public void testInvalidBlackKingSideCastlingRookMoved() {
        board.move(board.getBlackPieces(), new Square("h8"), new Square("h7"), false, Piece.ROOK);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, true));
    }

    /**
     * Test for invalid black king-side castling when the squares between the king and rook are not vacant.
     * Ensures that castling is not allowed if the squares are blocked.
     */
    @Test
    public void testInvalidBlackKingSideCastlingBlocked() {
        board.getBlackPieces().get(Piece.KNIGHT).add("f8"); // Place a piece on f8

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, true));
    }

    /**
     * Test for valid black queen-side castling.
     * Ensures that the black king and rook can castle to the queen-side.
     */
    @Test
    public void testValidBlackQueenSideCastling() {
        board.getBlackPieces().values().forEach(squares -> {
            squares.remove("b8");
            squares.remove("c8");
            squares.remove("d8");
        });

        board.tryCastle(false, false);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("c8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("d8"));
        assertFalse(board.getBlackPieces().get(Piece.KING).contains("e8"));
        assertFalse(board.getBlackPieces().get(Piece.ROOK).contains("a8"));
    }

    /**
     * Test for invalid black queen-side castling when the black king has moved.
     * Ensures that castling is not allowed if the king has already moved.
     */
    @Test
    public void testInvalidBlackQueenSideCastlingKingMoved() {
        board.move(board.getBlackPieces(), new Square("e8"), new Square("e7"), false, Piece.KING);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, false));
    }

    /**
     * Test for invalid black queen-side castling when the black rook has moved.
     * Ensures that castling is not allowed if the rook has already moved.
     */
    @Test
    public void testInvalidBlackQueenSideCastlingRookMoved() {
        board.move(board.getBlackPieces(), new Square("a8"), new Square("a7"), false, Piece.ROOK);

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, false));
    }

    /**
     * Test for invalid black queen-side castling when the squares between the king and rook are not vacant.
     * Ensures that castling is not allowed if the squares are blocked.
     */
    @Test
    public void testInvalidBlackQueenSideCastlingBlocked() {
        board.getBlackPieces().get(Piece.KNIGHT).add("d8"); // Place a piece on d8

        assertThrows(IllegalStateException.class, () -> board.tryCastle(false, false));
    }
}

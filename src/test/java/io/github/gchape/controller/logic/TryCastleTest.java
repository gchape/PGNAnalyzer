package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TryCastleTest {
    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game(Map.of(), "");
    }

    private Board getBoardReflectively(Game game) {
        try {
            Field boardField = Game.class.getDeclaredField("board");
            boardField.setAccessible(true);

            return (Board) boardField.get(game);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing 'board' field reflectively", e);
        }
    }

    /**
     * Test for valid white king-side castling.
     * Ensures that the white king and rook can castle to the king-side.
     */
    @Test
    public void testValidWhiteKingSideCastling() {
        Board board = getBoardReflectively(game);
        board.getWhitePieces().values().forEach(squares -> {
            squares.remove("f1");
            squares.remove("g1");
        });

        game.tryCastle(true, true);

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
        Board board = getBoardReflectively(game);
        board.getWhitePieces().values().forEach(squares -> {
            squares.remove("b1");
            squares.remove("c1");
            squares.remove("d1");
        });

        game.tryCastle(true, false);

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("c1"));
        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("d1"));
        assertFalse(board.getWhitePieces().get(Piece.KING).contains("e1"));
        assertFalse(board.getWhitePieces().get(Piece.ROOK).contains("a1"));
    }

    /**
     * Test for valid black king-side castling.
     * Ensures that the black king and rook can castle to the king-side.
     */
    @Test
    public void testValidBlackKingSideCastling() {
        Board board = getBoardReflectively(game);
        board.getBlackPieces().values().forEach(squares -> {
            squares.remove("g8");
            squares.remove("f8");
        });

        game.tryCastle(false, true);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("g8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("f8"));
        assertFalse(board.getBlackPieces().get(Piece.KING).contains("e8"));
        assertFalse(board.getBlackPieces().get(Piece.ROOK).contains("h8"));
    }

    /**
     * Test for valid black queen-side castling.
     * Ensures that the black king and rook can castle to the queen-side.
     */
    @Test
    public void testValidBlackQueenSideCastling() {
        Board board = getBoardReflectively(game);
        board.getBlackPieces().values().forEach(squares -> {
            squares.remove("b8");
            squares.remove("c8");
            squares.remove("d8");
        });

        game.tryCastle(false, false);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("c8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("d8"));
        assertFalse(board.getBlackPieces().get(Piece.KING).contains("e8"));
        assertFalse(board.getBlackPieces().get(Piece.ROOK).contains("a8"));
    }
}

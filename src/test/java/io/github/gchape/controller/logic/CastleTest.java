package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class CastleTest {

    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        game = new Game(null, null);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(game);

        board.getWhitePieces().clear();
        board.getBlackPieces().clear();

        board.getWhitePieces().put(Piece.KING, new HashSet<>(Set.of("e1")));
        board.getWhitePieces().put(Piece.ROOK, new HashSet<>(Set.of("h1", "a1")));
        board.getBlackPieces().put(Piece.KING, new HashSet<>(Set.of("e8")));
        board.getBlackPieces().put(Piece.ROOK, new HashSet<>(Set.of("h8", "a8")));

        Field kingMovedField = Game.class.getDeclaredField("kingMoved");
        kingMovedField.setAccessible(true);
        kingMovedField.setBoolean(game, false);

        Field rookMovedField = Game.class.getDeclaredField("rookMoved");
        rookMovedField.setAccessible(true);
        rookMovedField.setBoolean(game, false);
    }

    /**
     * Test that white king-side castling works successfully.
     * The white king moves from e1 to g1, and the white rook moves from h1 to f1.
     */
    @Test
    void whiteKingSideCastle_shouldSucceed() throws Exception {
        game.tryCastle(true, true);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(game);

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("g1"));
        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("f1"));
        assertFalse(board.getWhitePieces().get(Piece.KING).contains("e1"));
        assertFalse(board.getWhitePieces().get(Piece.ROOK).contains("h1"));
    }

    /**
     * Test that white queen-side castling works successfully.
     * The white king moves from e1 to c1, and the white rook moves from a1 to d1.
     */
    @Test
    void whiteQueenSideCastle_shouldSucceed() throws Exception {
        game.tryCastle(true, false);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(game);

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("c1"));
        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("d1"));
        assertFalse(board.getWhitePieces().get(Piece.KING).contains("e1"));
        assertFalse(board.getWhitePieces().get(Piece.ROOK).contains("a1"));
    }

    /**
     * Test that black king-side castling works successfully.
     * The black king moves from e8 to g8, and the black rook moves from h8 to f8.
     */
    @Test
    void blackKingSideCastle_shouldSucceed() throws Exception {
        game.tryCastle(false, true);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(game);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("g8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("f8"));
    }

    /**
     * Test that black queen-side castling works successfully.
     * The black king moves from e8 to c8, and the black rook moves from a8 to d8.
     */
    @Test
    void blackQueenSideCastle_shouldSucceed() throws Exception {
        game.tryCastle(false, false);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(game);

        assertTrue(board.getBlackPieces().get(Piece.KING).contains("c8"));
        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("d8"));
    }

    /**
     * Test that castling fails if the white king has already moved.
     * An IllegalStateException is thrown with an appropriate error message.
     */
    @Test
    void castle_shouldFail_ifKingMoved() throws Exception {
        Field kingMovedField = Game.class.getDeclaredField("kingMoved");
        kingMovedField.setAccessible(true);
        kingMovedField.setBoolean(game, true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> game.tryCastle(true, true));
        assertEquals("Cannot castle! Either king or rook has moved!", ex.getMessage());
    }

    /**
     * Test that castling fails if the white rook has already moved.
     * An IllegalStateException is thrown with an appropriate error message.
     */
    @Test
    void castle_shouldFail_ifRookMoved() throws Exception {
        Field rookMovedField = Game.class.getDeclaredField("rookMoved");
        rookMovedField.setAccessible(true);
        rookMovedField.setBoolean(game, true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> game.tryCastle(false, false));
        assertEquals("Cannot castle! Either king or rook has moved!", ex.getMessage());
    }
}

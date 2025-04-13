package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class PromotionTest {

    private Game game;
    private Board board;
    private Method tryPromotion;

    /**
     * Sets up the test environment, initializing game and board objects.
     * This method is run before each test.
     *
     * @throws Exception if there are any issues during setup
     */
    @BeforeEach
    void setUp() throws Exception {
        game = new Game(null, null);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        board = (Board) boardField.get(game);

        for (Piece piece : Piece.values()) {
            board.getWhitePieces().putIfAbsent(piece, new HashSet<>());
            board.getBlackPieces().putIfAbsent(piece, new HashSet<>());
        }

        tryPromotion = Game.class.getDeclaredMethod("tryPromotion", boolean.class, String.class);
        tryPromotion.setAccessible(true);
    }

    /**
     * Test for a successful promotion of a white pawn to a Queen.
     *
     * @throws Exception if reflection or method invocation fails
     */
    @Test
    void whitePawnPromotion_shouldSucceed() throws Exception {
        board.getWhitePieces().get(Piece.PAWN).add("e7");

        tryPromotion.invoke(game, true, "e8=Q");

        assertTrue(board.getWhitePieces().get(Piece.QUEEN).contains("e8"));
        assertFalse(board.getWhitePieces().get(Piece.PAWN).contains("e7"));
    }

    /**
     * Test for a successful promotion of a black pawn to a Rook.
     *
     * @throws Exception if reflection or method invocation fails
     */
    @Test
    void blackPawnPromotion_shouldSucceed() throws Exception {
        board.getBlackPieces().get(Piece.PAWN).add("e2");

        tryPromotion.invoke(game, false, "e1=R");

        assertTrue(board.getBlackPieces().get(Piece.ROOK).contains("e1"));
        assertFalse(board.getBlackPieces().get(Piece.PAWN).contains("e2"));
    }

    /**
     * Test for a failed promotion attempt of a white pawn from an invalid square.
     * The white pawn is on e7 and cannot be promoted from e6.
     *
     * @throws Exception if reflection or method invocation fails
     */
    @Test
    void whitePawnPromotionWithInvalidSquare_shouldFail() throws Exception {
        board.getWhitePieces().get(Piece.PAWN).add("e7");

        Exception thrown = assertThrows(InvocationTargetException.class, () -> tryPromotion.invoke(game, true, "e6=Q"));

        Throwable cause = thrown.getCause();

        assertInstanceOf(IllegalArgumentException.class, cause);
        assertEquals("White pawn must reach the 8th rank for promotion", cause.getMessage());
    }

    /**
     * Test for a failed promotion attempt of a black pawn from an invalid square.
     * The black pawn is on e2 and cannot be promoted from e3.
     *
     * @throws Exception if reflection or method invocation fails
     */
    @Test
    void blackPawnPromotionWithInvalidSquare_shouldFail() throws Exception {
        board.getBlackPieces().get(Piece.PAWN).add("e2");

        Exception thrown = assertThrows(InvocationTargetException.class, () -> tryPromotion.invoke(game, false, "e3=R"));

        Throwable cause = thrown.getCause();

        assertInstanceOf(IllegalArgumentException.class, cause);
        assertEquals("Black pawn must reach the 1st rank for promotion", cause.getMessage());
    }

    /**
     * Test for a failed promotion attempt where the white pawn is on e7 but cannot be promoted.
     *
     * @throws Exception if reflection or method invocation fails
     */
    @Test
    void invalidPromotion_shouldFail() throws Exception {
        board.getWhitePieces().get(Piece.PAWN).add("e7");

        Exception thrown = assertThrows(InvocationTargetException.class, () -> tryPromotion.invoke(game, true, "e7=Q"));

        Throwable cause = thrown.getCause();

        System.out.println("Exception Type: " + cause.getClass().getName());
        System.out.println("Exception Message: " + cause.getMessage());

        assertInstanceOf(IllegalArgumentException.class, cause, "Expected IllegalArgumentException, but got " + cause.getClass().getName());
        assertEquals("White pawn must reach the 8th rank for promotion", cause.getMessage());
    }
}

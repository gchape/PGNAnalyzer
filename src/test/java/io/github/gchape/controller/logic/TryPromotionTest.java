package io.github.gchape.controller.logic;

import io.github.gchape.exceptions.InvalidPromotionException;
import io.github.gchape.exceptions.NoPieceFoundException;
import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TryPromotionTest {
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
     * Test for a valid white pawn promotion.
     * Ensures that a white pawn can be promoted to a queen when it reaches rank 8.
     */
    @Test
    public void testValidWhitePromotion() {
        Board board = getBoardReflectively(game);
        board.getWhitePieces().get(Piece.PAWN).add("a7");

        String move = "a8=Q";
        game.tryPromotion(true, move);

        assertFalse(board.getWhitePieces().get(Piece.PAWN).contains("a7"));
        assertTrue(board.getWhitePieces().get(Piece.QUEEN).contains("a8"));
    }

    /**
     * Test for a valid black pawn promotion.
     * Ensures that a black pawn can be promoted to a queen when it reaches rank 1.
     */
    @Test
    public void testValidBlackPromotion() {
        Board board = getBoardReflectively(game);
        board.getBlackPieces().get(Piece.PAWN).add("a2");
        String move = "a1=Q";

        game.tryPromotion(false, move);

        assertFalse(board.getBlackPieces().get(Piece.PAWN).contains("a2"));
        assertTrue(board.getBlackPieces().get(Piece.QUEEN).contains("a1"));
    }

    /**
     * Test for an invalid white promotion where the pawn does not reach rank 8.
     * Ensures that an exception is thrown if the white pawn is not on rank 7 or 8.
     */
    @Test
    public void testInvalidWhitePromotionRank() {
        String move = "a7=Q";
        assertThrows(InvalidPromotionException.class, () -> game.tryPromotion(true, move));
    }

    /**
     * Test for an invalid black promotion where the pawn does not reach rank 1.
     * Ensures that an exception is thrown if the black pawn is not on rank 2 or 1.
     */
    @Test
    public void testInvalidBlackPromotionRank() {
        String move = "a2=Q";
        assertThrows(InvalidPromotionException.class, () -> game.tryPromotion(false, move));
    }

    /**
     * Test for the scenario where there is no pawn at the expected position for promotion.
     * Ensures that an exception is thrown if there is no pawn at the starting square for promotion.
     */
    @Test
    public void testNoPawnAtExpectedPosition() {
        String move = "a8=Q";
        assertThrows(NoPieceFoundException.class, () -> game.tryPromotion(true, move));
    }

    /**
     * Test for an invalid promotion piece.
     * Ensures that an exception is thrown if the promoted piece is not a valid piece type (e.g., not a Queen, Rook, Bishop, or Knight).
     */
    @Test
    public void testInvalidPromotionPiece() {
        String move = "a7=R";
        assertThrows(InvalidPromotionException.class, () -> game.tryPromotion(true, move));
    }
}

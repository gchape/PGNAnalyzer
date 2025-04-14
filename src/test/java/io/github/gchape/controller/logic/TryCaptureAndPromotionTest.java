package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TryCaptureAndPromotionTest {
    private Game game;
    private Board board;

    @BeforeEach
    public void setup() throws Exception {
        game = new Game(null, null);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        board = (Board) boardField.get(game);

        board.getWhitePieces().clear();
        board.getBlackPieces().clear();

        board.getWhitePieces().put(Piece.ROOK, new HashSet<>());
        board.getBlackPieces().put(Piece.PAWN, new HashSet<>());
        board.getBlackPieces().put(Piece.QUEEN, new HashSet<>());
    }

    /**
     * Tests that a white pawn can capture a black piece on the last rank and promote to a queen.
     * Verifies that the pawn is removed from its original position and replaced with a queen at the target square.
     */
    @Test
    public void testWhitePawnCaptureAndPromotion() throws Exception {
        board.getWhitePieces().put(Piece.PAWN, new HashSet<>());
        board.getWhitePieces().put(Piece.QUEEN, new HashSet<>());
        board.getBlackPieces().put(Piece.ROOK, new HashSet<>());

        board.getWhitePieces().get(Piece.PAWN).add("g7");
        board.getBlackPieces().get(Piece.ROOK).add("h8");

        Method method = Game.class.getDeclaredMethod("tryCaptureAndPromotion", boolean.class, String.class);
        method.setAccessible(true);
        method.invoke(game, true, "gxh8=Q");

        Map<Piece, Set<String>> whitePieces = board.getWhitePieces();

        assertTrue(whitePieces.get(Piece.QUEEN).contains("h8"));
        assertFalse(whitePieces.get(Piece.PAWN).contains("g7"));
    }

    /**
     * Tests that a black pawn can capture a white piece on the first rank and promote to a queen.
     * Verifies that the pawn is removed from its original position and replaced with a queen at the target square.
     */
    @Test
    public void testBlackPawnCaptureAndPromotion() throws Exception {
        board.getBlackPieces().get(Piece.PAWN).add("b2");
        board.getWhitePieces().get(Piece.ROOK).add("a1");

        Method method = Game.class.getDeclaredMethod("tryCaptureAndPromotion", boolean.class, String.class);
        method.setAccessible(true);
        method.invoke(game, false, "bxa1=Q");

        Map<Piece, Set<String>> blackPieces = board.getBlackPieces();

        assertTrue(blackPieces.get(Piece.QUEEN).contains("a1"));
        assertFalse(blackPieces.get(Piece.PAWN).contains("b2"));
    }
}

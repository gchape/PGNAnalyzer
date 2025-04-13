package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CaptureAndPromotionTest {
    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        game = new Game(new HashMap<>(), "");
    }

    @Test
    void testCaptureAndPromotion_validMove() throws Exception {
        String move = "bxa1=Q";

        Map<Piece, Set<String>> whitePieces = new HashMap<>();
        Map<Piece, Set<String>> blackPieces = new HashMap<>();

        whitePieces.put(Piece.PAWN, new HashSet<>(List.of("b7")));
        whitePieces.put(Piece.QUEEN, new HashSet<>());
        blackPieces.put(Piece.PAWN, new HashSet<>(List.of("a1")));

        setBoardPieces(whitePieces, blackPieces);

        invokeTryCaptureAndPromotion(move);

        assertTrue(whitePieces.get(Piece.QUEEN).contains("a1"));
        assertFalse(whitePieces.get(Piece.PAWN).contains("b7"));
        assertFalse(blackPieces.get(Piece.PAWN).contains("a1"));
    }

    @Test
    void testCaptureAndPromotion_invalidPromotionPiece() throws Exception {
        String move = "bxa1=K";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> invokeTryCaptureAndPromotion(move));

        assertEquals("Invalid promotion piece in move 'bxa1=K'. Must be one of Q, R, B, N.", thrown.getMessage());
    }

    @Test
    void testCaptureAndPromotion_noPawnAtStart() throws Exception {
        String move = "bxa1=Q";

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            invokeTryCaptureAndPromotion(move);
        });

        assertTrue(thrown.getMessage().contains("No White pawn found at b7 to perform capture and promotion"));
    }

    @Test
    void testCaptureAndPromotion_captureWorks() throws Exception {
        String move = "bxa1=Q";

        Map<Piece, Set<String>> whitePieces = new HashMap<>();
        Map<Piece, Set<String>> blackPieces = new HashMap<>();

        whitePieces.put(Piece.PAWN, new HashSet<>(Arrays.asList("b7")));
        whitePieces.put(Piece.QUEEN, new HashSet<>());
        blackPieces.put(Piece.PAWN, new HashSet<>(Arrays.asList("a1")));

        setBoardPieces(whitePieces, blackPieces);

        invokeTryCaptureAndPromotion(move);

        assertFalse(blackPieces.get(Piece.PAWN).contains("a1"));
        assertTrue(whitePieces.get(Piece.QUEEN).contains("a1"));
    }

    @Test
    void testCaptureAndPromotion_invalidFormat() throws Exception {
        String move = "bxa1=Z";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            invokeTryCaptureAndPromotion(move);
        });

        assertEquals("Invalid promotion piece in move 'bxa1=Z'. Must be one of Q, R, B, N.", thrown.getMessage());
    }

    private void invokeTryCaptureAndPromotion(String move) throws Exception {
        Method method = Game.class.getDeclaredMethod("tryCaptureAndPromotion", boolean.class, String.class);
        method.setAccessible(true);
        method.invoke(game, true, move);
    }

    private void setBoardPieces(Map<Piece, Set<String>> whitePieces, Map<Piece, Set<String>> blackPieces) throws Exception {
        Field whitePiecesField = Game.class.getDeclaredField("board");
        whitePiecesField.setAccessible(true);

        Field whitePiecesInBoard = whitePiecesField.getType().getDeclaredField("whitePieces");
        whitePiecesInBoard.setAccessible(true);
        whitePiecesInBoard.set(whitePiecesField.get(game), whitePieces);

        Field blackPiecesInBoard = whitePiecesField.getType().getDeclaredField("blackPieces");
        blackPiecesInBoard.setAccessible(true);
        blackPiecesInBoard.set(whitePiecesField.get(game), blackPieces);
    }
}

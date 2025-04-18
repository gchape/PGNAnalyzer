package io.github.gchape.controller.logic;

import io.github.gchape.model.entities.Board;
import io.github.gchape.model.entities.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TryCaptureTest {
    private Game game;
    private Board board;
    private Method tryCapture;

    @BeforeEach
    void setUp() throws Exception {
        game = new Game(null, null);

        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        board = (Board) boardField.get(game);

        board.getWhitePieces().clear();
        board.getBlackPieces().clear();

        tryCapture = Game.class.getDeclaredMethod("tryCapture", boolean.class, String.class);
        tryCapture.setAccessible(true);
    }

    /**
     * Test that a white king can successfully capture a black pawn.
     * The white king moves from c1 to capture a black pawn on d2.
     */
    @Test
    void whiteKingCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.KING, new HashSet<>(Set.of("c1")));
        board.getBlackPieces().put(Piece.PAWN, new HashSet<>(Set.of("d2")));

        tryCapture.invoke(game, true, "Kxd2");

        assertTrue(board.getWhitePieces().get(Piece.KING).contains("d2"));
        assertFalse(board.getBlackPieces().get(Piece.PAWN).contains("d2"));
    }

    /**
     * Test that a white queen can successfully capture a black rook.
     * The white queen moves from d1 to capture a black rook on d8.
     */
    @Test
    void whiteQueenCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.QUEEN, new HashSet<>(Set.of("d1")));
        board.getBlackPieces().put(Piece.ROOK, new HashSet<>(Set.of("d8")));

        tryCapture.invoke(game, true, "Qxd8");

        assertTrue(board.getWhitePieces().get(Piece.QUEEN).contains("d8"));
        assertFalse(board.getBlackPieces().get(Piece.ROOK).contains("d8"));
    }

    /**
     * Test that a white rook can successfully capture a black knight.
     * The white rook moves from h1 to capture a black knight on h8.
     */
    @Test
    void whiteRookCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.ROOK, new HashSet<>(Set.of("h1")));
        board.getBlackPieces().put(Piece.KNIGHT, new HashSet<>(Set.of("h8")));

        tryCapture.invoke(game, true, "Rxh8");

        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("h8"));
        assertFalse(board.getBlackPieces().get(Piece.KNIGHT).contains("h8"));
    }

    /**
     * Test that a white bishop can successfully capture a black knight.
     * The white bishop moves from c1 to capture a black knight on f4.
     */
    @Test
    void whiteBishopCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.BISHOP, new HashSet<>(Set.of("c1")));
        board.getBlackPieces().put(Piece.KNIGHT, new HashSet<>(Set.of("f4")));

        tryCapture.invoke(game, true, "Bxf4");

        assertTrue(board.getWhitePieces().get(Piece.BISHOP).contains("f4"));
        assertFalse(board.getBlackPieces().get(Piece.KNIGHT).contains("f4"));
    }

    /**
     * Test that a white knight can successfully capture a black bishop.
     * The white knight moves from g1 to capture a black bishop on e2.
     */
    @Test
    void whiteKnightCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.KNIGHT, new HashSet<>(Set.of("g1")));
        board.getBlackPieces().put(Piece.BISHOP, new HashSet<>(Set.of("e2")));

        tryCapture.invoke(game, true, "Nxe2");

        assertTrue(board.getWhitePieces().get(Piece.KNIGHT).contains("e2"));
        assertFalse(board.getBlackPieces().get(Piece.BISHOP).contains("e2"));
    }

    /**
     * Test that a white rook can capture a black knight with disambiguation.
     * The rook has two possible starting positions: a1 and h1.
     * Disambiguation is provided using the "R1" notation to specify which rook moves.
     */
    @Test
    void whiteRookCaptureWithDisambiguation_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.ROOK, new HashSet<>(Set.of("a1", "h1")));
        board.getBlackPieces().put(Piece.KNIGHT, new HashSet<>(Set.of("a8", "h8")));

        tryCapture.invoke(game, true, "R1xa8");

        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("a8"));
        assertFalse(board.getBlackPieces().get(Piece.KNIGHT).contains("a8"));

        tryCapture.invoke(game, true, "R1xh8");

        assertTrue(board.getWhitePieces().get(Piece.ROOK).contains("h8"));
        assertFalse(board.getBlackPieces().get(Piece.KNIGHT).contains("h8"));
    }

    @Test
    void enPassantCapture_shouldSucceed() throws Exception {
        board.getWhitePieces().put(Piece.PAWN, new HashSet<>(Set.of("d5")));
        board.getBlackPieces().put(Piece.PAWN, new HashSet<>(Set.of("e7")));

        tryCapture.invoke(game, true, "dxe6");

        assertTrue(board.getWhitePieces().get(Piece.PAWN).contains("e6"));
        assertFalse(board.getBlackPieces().get(Piece.PAWN).contains("e5"));
    }
}

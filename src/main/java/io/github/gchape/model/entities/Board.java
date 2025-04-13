package io.github.gchape.model.entities;

import java.util.*;

public class Board {
    private final Map<Piece, Set<String>> blackPieces = new EnumMap<>(Piece.class);
    private final Map<Piece, Set<String>> whitePieces = new EnumMap<>(Piece.class);

    public Board() {
        whitePieces.put(Piece.KING, new HashSet<>(Set.of("e1")));
        whitePieces.put(Piece.QUEEN, new HashSet<>(Set.of("d1")));
        whitePieces.put(Piece.ROOK, new HashSet<>(Set.of("a1", "h1")));
        whitePieces.put(Piece.BISHOP, new HashSet<>(Set.of("c1", "f1")));
        whitePieces.put(Piece.KNIGHT, new HashSet<>(Set.of("b1", "g1")));
        whitePieces.put(Piece.PAWN, new HashSet<>(Set.of("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2")));

        blackPieces.put(Piece.KING, new HashSet<>(Set.of("e8")));
        blackPieces.put(Piece.QUEEN, new HashSet<>(Set.of("d8")));
        blackPieces.put(Piece.ROOK, new HashSet<>(Set.of("a8", "h8")));
        blackPieces.put(Piece.BISHOP, new HashSet<>(Set.of("c8", "f8")));
        blackPieces.put(Piece.KNIGHT, new HashSet<>(Set.of("b8", "g8")));
        blackPieces.put(Piece.PAWN, new HashSet<>(Set.of("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7")));
    }

    public Map<Piece, Set<String>> getBlackPieces() {
        return blackPieces;
    }

    public Map<Piece, Set<String>> getWhitePieces() {
        return whitePieces;
    }
}

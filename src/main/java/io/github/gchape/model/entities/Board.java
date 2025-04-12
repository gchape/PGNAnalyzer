package io.github.gchape.model.entities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private final Map<Piece, Set<String>> blackPieces = Map.of(
            Piece.QUEEN, new HashSet<>(Set.of("d8")),
            Piece.KING, new HashSet<>(Set.of("e8")),
            Piece.ROOK, new HashSet<>(Set.of("a8", "h8")),
            Piece.BISHOP, new HashSet<>(Set.of("c8", "f8")),
            Piece.KNIGHT, new HashSet<>(Set.of("b8", "g8")),
            Piece.PAWN, new HashSet<>(Set.of("a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"))
    );

    private final Map<Piece, Set<String>> whitePieces = Map.of(
            Piece.KING, new HashSet<>(Set.of("e1")),
            Piece.QUEEN, new HashSet<>(Set.of("d1")),
            Piece.ROOK, new HashSet<>(Set.of("a1", "h1")),
            Piece.BISHOP, new HashSet<>(Set.of("c1", "f1")),
            Piece.KNIGHT, new HashSet<>(Set.of("b1", "g1")),
            Piece.PAWN, new HashSet<>(Set.of("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"))
    );
}

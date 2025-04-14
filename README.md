# ♟️ PgnalyzeFX

`PgnalyzeFX` is a Java-based application that simulates a full chess game from a PGN (Portable Game Notation) file. It parses the moves, updates the board, and handles complex chess rules such as castling, en passant, pawn promotion, captures, and move disambiguation. The application is designed to provide a robust chess game simulation with easy-to-understand outputs, all within a user-friendly JavaFX interface.

---

## ✅ Features

- **Full PGN Parsing**:
  - Parse standard chess moves (e.g., `e4`, `Nf3`, `O-O`)
  - Support for special moves: castling (`O-O`, `O-O-O`), en passant, and pawn promotion (e.g., `e8=Q`, `bxa1=R`)
  - Capture notation (e.g., `Nxd5`, `exd5`), including disambiguation for same-type pieces (e.g., `Nbd2`, `R1a3`)
- **Game Simulation**:
  - Move pieces according to the rules of chess
  - Enforce legal move constraints (e.g., piece legality, board occupancy)
  - Handle game outcomes (`1-0`, `0-1`, `1/2-1/2`, `*`)
- **Thread-Safe Execution**: Built to run in a multi-threaded environment with `Runnable`
- **JavaFX Interface**: Clean and responsive user interface for displaying results and simulating games

---

## 🧠 Architecture Overview

### 1. **Game Logic (`Game.java`)**
- **Responsibility**: Handles the core logic for simulating a chess game.
- **Key Features**:
  - Implements `Runnable` to allow thread-safe operation
  - Processes each PGN move in sequence and updates the board state
  - Handles special chess rules such as castling, en passant, promotion, and captures
  - Validates moves using the `Board` class
  - Tracks the outcome of the game (e.g., checkmate, stalemate)

### 2. **Board (`Board.java`)**
- **Responsibility**: Represents the chessboard and the state of each piece.
- **Key Features**:
  - Stores board state as a map (`Map<Piece, Set<String>>`) for each color
  - Allows for piece movement and validation
  - Tracks special rules, such as castling (rook and king moved flags) and en passant (tracks the last double-step pawn move)
  - Manages piece promotion by removing pawns and adding promoted pieces

### 3. **Square (`Square.java`)**
- **Responsibility**: Represents a chess square using coordinates (x/y).
- **Key Features**:
  - Converts between chess notation (e.g., `e4`, `d2`) and internal coordinates
  - Supports move validation, piece placement, and comparisons

### 4. **Piece (`Piece.java`)**
- **Responsibility**: Enum representing the various chess pieces (PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING).
- **Key Features**:
  - Maps PGN character representation (e.g., `N` for Knight) to internal piece type
  - Used in conjunction with `Board` to determine valid moves for each piece

### 5. **Printer (`Printer.java`)**
- **Responsibility**: Outputs game results and diagnostic information.
- **Key Features**:
  - Singleton design pattern for centralized logging
  - Collects and prints game headers, outcomes, and results

---

## ♟️ Supported Chess Features

- **Castling**: Handles both kingside (`O-O`) and queenside (`O-O-O`) castling with validation for occupied squares and piece movement.
- **Captures**: Supports standard captures (e.g., `Nxf3`) and pawn captures (e.g., `exd5`).
- **Pawn Promotion**: Supports promotion with and without captures (e.g., `e8=Q`, `bxa1=R`).
- **Disambiguation**: Handles situations where multiple pieces of the same type can move to the same square (e.g., `Nbd2`, `R1a3`).
- **En Passant**: Detects and validates en passant captures (e.g., `exf6` when a pawn moves two squares forward).
- **Game Outcomes**: Handles draw and result declarations, including `1-0`, `0-1`, `1/2-1/2`, and `*` (ongoing game).

---

## 📁 Project Structure

```
PgnalyzeFX/
├── src/
|   |── io/github/gchape/
|   |   |── PgnalyzeFx.java
│   ├── io/github/gchape/controller/
│   │   └── logic/
│   │       └── Game.java
│   ├── io/github/gchape/model/
│   │   └── entities/
│   │       ├── Board.java
│   │       ├── Piece.java
│   │       ├── Square.java
│   │       └── Printer.java
```

---

## 🛠️ Architecture

- **`Game.java`**:
  - Parses PGN moves, validates them, updates the board state, and handles special cases (e.g., castling, en passant, promotions).
  
- **`Board.java`**:
  - Manages all chess pieces on the board.
  - Implements move validation, including piece-specific rules, castling, and en passant.
  - Tracks and updates the state of each square on the board.

- **`Square.java`**:
  - Provides utility methods for converting between chess notation (e.g., "e4") and coordinate-based representation.
  - Used for move validation, disambiguation, and comparing positions.

- **`Piece.java`**:
  - A simple enum that defines the six types of chess pieces (PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING).
  - Used to identify the type of piece in a given move or on a specific square.

- **`Printer.java`**:
  - Outputs formatted game results, logs headers (such as player names, event, etc.), and prints the outcome of the game.

- **`PgnalyzeFX.java`**:
  - Entry point
---

## 🚀 Usage

**Maven Build**:
   If you're using Maven, you can build and run the project directly from the command line:

   ```bash
   mvn clean package
   mvn javafx:run
   ```

---

## 📝 Example PGN

```text
[Event "Sample Game"]
[Site "Lichess"]
[Date "2023.12.01"]
[Round "?"]
[White "PlayerA"]
[Black "PlayerB"]
[Result "1-0"]

1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7
6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8 10. d4 Nbd7
11. c4 bxc4 12. Bxc4 Nb6 13. Bd3 exd4 14. Nxd4 c5
15. Nc6 Qc7 16. Nxe7+ Qxe7 17. Nc3 Bb7 18. Bg5 Qe5
19. f4 Qd4+ 20. Kh1 Rfe8 21. e5 Qf2 22. Rg1 Qg3
```

---

## 🔧 Dependencies

This project uses the following dependencies:

- **JavaFX**: For the graphical user interface.
- **JUnit**: For unit testing.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

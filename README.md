# 2048

422c 10/1 recitation attendance: a simple version of the game 2048, written in Java with Swing.

## Requirements

- Java 8 or newer (JDK, so you have `javac`)
- A computer with a display. The game opens a window, so it won't run on a server with no screen (for example over SSH).

## How to run

```
// after cloning the repo:
javac Game2048.java
java Game2048
```

## How to play

- Use the **arrow keys** or **W A S D** to slide every tile up, left, down or right.
- When two tiles with the same number touch, they merge into one tile worth their sum.
- After each move, a new 2 (sometimes a 4) appears in an empty spot.
- Make a **2048** tile to win. You can keep playing after that.
- The game ends when the board is full and no tiles can merge.
- Your score goes up by the value of each merged tile and is shown under the board.

Close the window to quit.

## How the code is organized

Everything is in `Game2048.java`:

| Method | What it does |
| --- | --- |
| `main` | Adds the first two tiles and opens the window |
| `slideBoard` | Moves the whole board in one direction |
| `slide` | Slides and merges a single row or column |
| `addTile` | Puts a 2 or 4 in a random empty cell |
| `canMove` | Checks whether any move is left |
| `has2048` | Checks for a winning tile |
| `paintComponent` | Draws the board and score |

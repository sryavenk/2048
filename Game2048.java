import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

// 2048
public class Game2048 extends JPanel {
    static final int SIZE = 4;
    static final int TILE = 100;
    static final int GAP = 10;

    static int[][] board = new int[SIZE][SIZE];
    static int score = 0;
    static boolean won = false;
    static Random rand = new Random();

    public static void main(String[] args) {
        addTile();
        addTile();

        JFrame frame = new JFrame("2048");
        Game2048 panel = new Game2048();
        frame.add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    Game2048() {
        int side = SIZE * TILE + (SIZE + 1) * GAP;
        setPreferredSize(new Dimension(side, side + 40));
        setBackground(new Color(187, 173, 160));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!canMove()) return;
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:    case KeyEvent.VK_W: moved = slideBoard(-1, 0); break;
                    case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: moved = slideBoard(1, 0); break;
                    case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: moved = slideBoard(0, -1); break;
                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: moved = slideBoard(0, 1); break;
                }
                if (moved) {
                    addTile();
                    if (has2048()) won = true;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int x = GAP + c * (TILE + GAP);
                int y = GAP + r * (TILE + GAP);
                int v = board[r][c];

                g2.setColor(tileColor(v));
                g2.fillRoundRect(x, y, TILE, TILE, 12, 12);

                if (v != 0) {
                    g2.setColor(v <= 4 ? new Color(119, 110, 101) : Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, v < 1000 ? 36 : 28));
                    String s = String.valueOf(v);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(s, x + (TILE - fm.stringWidth(s)) / 2,
                            y + (TILE + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        }

        String status = "Score: " + score;
        if (!canMove()) status += "   Game over!";
        else if (won) status += "   You made 2048!";
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(status, GAP, getHeight() - 14);
    }

    static Color tileColor(int v) {
        switch (v) {
            case 0:    return new Color(205, 193, 180);
            case 2:    return new Color(238, 228, 218);
            case 4:    return new Color(237, 224, 200);
            case 8:    return new Color(242, 177, 121);
            case 16:   return new Color(245, 149, 99);
            case 32:   return new Color(246, 124, 95);
            case 64:   return new Color(246, 94, 59);
            case 128:  return new Color(237, 207, 114);
            case 256:  return new Color(237, 204, 97);
            case 512:  return new Color(237, 200, 80);
            case 1024: return new Color(237, 197, 63);
            default:   return new Color(237, 194, 46);
        }
    }

    // Slide every row/column toward (dr, dc). Returns true if anything changed.
    static boolean slideBoard(int dr, int dc) {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            // Read the line starting from the edge we're sliding toward.
            int[] line = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {
                int[] pos = cell(i, j, dr, dc);
                line[j] = board[pos[0]][pos[1]];
            }

            int[] merged = slide(line);

            for (int j = 0; j < SIZE; j++) {
                int[] pos = cell(i, j, dr, dc);
                if (board[pos[0]][pos[1]] != merged[j]) moved = true;
                board[pos[0]][pos[1]] = merged[j];
            }
        }
        return moved;
    }

    // The j-th cell of line i, counting from the edge in direction (dr, dc).
    static int[] cell(int i, int j, int dr, int dc) {
        if (dr == -1) return new int[]{j, i};            // up
        if (dr == 1)  return new int[]{SIZE - 1 - j, i}; // down
        if (dc == -1) return new int[]{i, j};            // left
        return new int[]{i, SIZE - 1 - j};               // right
    }

    // Push tiles to the front of the line and merge equal neighbors once.
    static int[] slide(int[] line) {
        int[] result = new int[SIZE];
        int k = 0;
        int last = 0; // last tile placed that can still merge
        for (int v : line) {
            if (v == 0) continue;
            if (v == last) {
                result[k - 1] = v * 2;
                score += v * 2;
                last = 0;
            } else {
                result[k++] = v;
                last = v;
            }
        }
        return result;
    }

    // Put a 2 (90%) or 4 (10%) in a random empty cell.
    static void addTile() {
        int empty = 0;
        for (int[] row : board)
            for (int v : row)
                if (v == 0) empty++;
        if (empty == 0) return;

        int pick = rand.nextInt(empty);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0 && pick-- == 0) {
                    board[r][c] = rand.nextInt(10) == 0 ? 4 : 2;
                    return;
                }
            }
        }
    }

    static boolean canMove() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) return true;
                if (c + 1 < SIZE && board[r][c] == board[r][c + 1]) return true;
                if (r + 1 < SIZE && board[r][c] == board[r + 1][c]) return true;
            }
        }
        return false;
    }

    static boolean has2048() {
        for (int[] row : board)
            for (int v : row)
                if (v >= 2048) return true;
        return false;
    }
}

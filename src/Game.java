import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

public class Game extends JPanel {

    private Board cG;
    private JLabel status;
    private Piece selectedPiece;
    private ArrayList<Point> validMoves;
    private boolean gameOver;
    private boolean isRedTurn;

    public static final int BOARD_SIZE = 8;
    public static final int TILE_SIZE = 80;

    public Game(JLabel statusInit) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);

        cG = new Board (TILE_SIZE, BOARD_SIZE, BOARD_SIZE);
        status = statusInit;
        selectedPiece = null;
        validMoves = new ArrayList<>();
        isRedTurn = true;

        cG.newBoard();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
               int col = e.getX() / TILE_SIZE;
               int row = e.getY() / TILE_SIZE;

               cG.handleMoves(row, col);
               updateStatus();
               repaint();
            }
        });

    }
    private void updateStatus() {
        if (cG.isRedTurn()) {
            status.setText("Red's Turn");
        } else {
            status.setText("White's Turn");
        }

        int winner = cG.checkWinner();
        if (winner == 1) {
            status.setText("Red Wins!");

        } else if (winner == 2) {
            status.setText("White Wins!");

        }
    }

    public void reset() {
        cG.newBoard();
        status.setText("Red's Turn");
        cG.isRedTurn = true;
        cG.gameOver = false;
        cG.selectedPiece = null;
        cG.validMoves.clear();
        repaint();
        requestFocusInWindow();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        cG.paintComponent(g);
    }

    public Dimension getPreferredSize() {
        return new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
    }

    public void save() {
        cG.save();
    }
    public void load() {
        cG.load();
        updateStatus();
        repaint();
    }

    public void undo() {
        if (cG.undo()) {
            updateStatus();
            repaint();
        }
    }
}
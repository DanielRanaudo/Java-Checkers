

import java.awt.*;

import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static java.awt.Color.WHITE;

public class Piece {
    private boolean isRed;
    private boolean isKing = false;
    private int row;
    private int col;

    //initiates object
    public Piece(boolean isRed, int col, int row) {
        this.isRed = isRed;
        this.isKing = false;
        this.row = row;
        this.col = col;
    }

    public void drawPiece(Graphics g, int tileSize, int col, int row) {
        if (isRed) {
            g.setColor(RED);
        } else {
            g.setColor(WHITE);
        }
        g.fillOval(col * tileSize, row * tileSize, tileSize, tileSize);
        //indicator for king
        if (isKing) {
            g.setColor(YELLOW);
            g.fillRect(col * tileSize, row * tileSize, tileSize/8, tileSize/8);
        }
    }

    public boolean isRed() {
        return isRed;
    }
    public boolean isKing() {
        return isKing;
    }
    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public void movePiece(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

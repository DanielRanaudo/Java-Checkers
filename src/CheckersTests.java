
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class CheckersTests {


    @Test
    public void testInitialBoardSetup() {
        Board cG = new Board(80, 8, 8);
        cG.newBoard();
        int redCount = 0;
        int whiteCount = 0;
        int emptyCount = 0;
        // Test red pieces in correct starting positions
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = cG.board[row][col];
                if (p == null) {
                    emptyCount++;
                } else if (p.isRed()) {
                    redCount++;
                } else {
                    whiteCount++;
                }
            }
        }
        assertEquals(12, redCount);
        assertEquals(12, whiteCount);
        assertEquals(40, emptyCount);
    }

    @Test
    public void testGameOver() {
        Board cG = new Board(80, 8, 8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = cG.board[row][col];
                if (p != null && !p.isRed()) {
                    //removes piece
                    cG.board[row][col] = null;
                }
            }
        }
        assertEquals(1, cG.checkWinner());
    }

    @Test
    public void testMoveValid() {
        Board cG = new Board(80, 8, 8);
        Piece p = cG.board[2][2];
        cG.getValidMoves(p);
        cG.movePiece(2, 2, 3, 1);
        assertNull(cG.board[2][2]);
        assertSame(cG.board[3][1], p);
    }

    @Test
    public void testMoveInvalid() {
        Board cG = new Board(80, 8, 8);
        Piece p = cG.board[2][2];
        assertFalse(cG.movePiece(2, 2, 2, 3)); //horizontal movement
        assertFalse(cG.movePiece(2, 2, 3, 2)); // vertical movement
        assertFalse(cG.movePiece(2, 2, 1, 2));// backwards movement
        assertFalse(cG.movePiece(0, 0, 1, -1));
    }

    @Test
    public void kingPromotion() {
        Board cG = new Board(80, 8, 8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cG.board[row][col] = null;
            }
        }
        Piece p = new Piece (true, 6, 6);
        cG.board[6][6] = p;
        cG.movePiece(6, 6, 7, 7);
        assertTrue(p.isKing());
    }

    @Test
    public void testJump() {
        Board cG = new Board(80, 8, 8);


        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cG.board[row][col] = null;
            }
        }
        Piece redPiece = new Piece (true, 5, 5);
        Piece whitePiece = new Piece (false, 6, 6);
        cG.board[5][5] = redPiece;
        cG.board[6][6] = whitePiece;
        cG.movePiece(5, 5, 7, 7);
        assertNull(cG.board[6][6]);
        assertEquals(cG.board[7][7], redPiece);
        assertNull(cG.board[5][5]);

    }

    @Test
    public void testKingJumpAndMovement() {
        Board cG = new Board(80, 8, 8);
        //clear entire thing
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cG.board[row][col] = null;
            }
        }

        //set up new
        Piece redPiece = new Piece (true, 6, 6);
        cG.board[6][6] = redPiece;
        cG.movePiece(6, 6, 7, 7);
        Piece whitePiece = new Piece (false, 6, 6);
        cG.board[6][6] = whitePiece;
        assertTrue(redPiece.isKing());
        cG.movePiece(7, 7, 5, 5);
        assertNull(cG.board[6][6]);
    }

    @Test
    public void testIO() {
        Board cG = new Board(80, 8, 8);
        cG.newBoard();
        cG.movePiece(2, 2, 3, 3);

        cG.save();
        Board cG2 = new Board(80, 8, 8);
        cG2.load();

        assertNotNull(cG2.board[3][3]);
    }

    @Test
    public void testChainJumps() {
        Board cG = new Board(80, 8, 8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cG.board[row][col] = null;
            }
        }

        Piece redPiece = new Piece (true, 3, 3);
        cG.board[3][3] = redPiece;
        Piece whitePiece = new Piece (false, 4, 4);
        cG.board[4][4] = whitePiece;
        Piece whitePiece2 = new Piece (false, 6, 6);
        cG.board[6][6] = whitePiece2;
        //for this one we will use handle moves to ensure that the turn can switch

        ArrayList<Point> moves = cG.getValidMoves(cG.board[3][3]);
        if (moves.size() == 1 && moves.contains(new Point(5, 5))) {
            cG.movePiece(3, 3, 5, 5);
        }
        //check it got removed
        assertNull(cG.board[4][4]);
        assertNull(cG.board[3][3]); //redPiece moved

        moves = cG.getValidMoves(cG.board[5][5]);
        if (moves.size() == 1 && moves.contains(new Point(7, 7))) {
            cG.movePiece(5, 5, 7, 7);
        }
        assertNull(cG.board[6][6]);
        assertNotNull(cG.board[7][7]);
    }

    @Test
    public void testUndo() {
        Board cG = new Board(80, 8, 8);
        Map<Point, Piece> ogLocations = cG.pieceLocations();
        cG.movePiece(2, 2, 3, 3);
        Map<Point, Piece> beforeUndoLocations = cG.pieceLocations();
        assertNotSame(ogLocations, beforeUndoLocations);
        cG.undo();
        assertEquals(ogLocations, cG.pieceLocations());
    }

    @Test
    public void testForceJump() {
        // Test that jumps are forced when available
        Board cG = new Board(80, 8, 8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cG.board[row][col] = null;
            }
        }
        Piece redPiece = new Piece (true, 3, 3);
        cG.board[3][3] = redPiece;

        Piece otherRed = new Piece (true, 1, 1);
        cG.board[1][1] = otherRed;
        Piece whitePiece = new Piece (false, 4, 4);
        cG.board[4][4] = whitePiece;

        ArrayList<Point> moves = cG.getValidMoves(redPiece);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Point(5, 5)));
    }
}


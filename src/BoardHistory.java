public class BoardHistory {
    Piece[][] board;
    boolean isRedTurn;

    BoardHistory(Piece[][] board, boolean isRedTurn) {
        this.board = new Piece[board.length][board[0].length];
        //makes a deep copy of the board, with all the pieces and their data
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != null) {
                    Piece piece = board[i][j];
                    this.board[i][j] = new Piece(piece.isRed(), piece.getCol(), piece.getRow());
                    if (piece.isKing()) {
                        this.board[i][j].setKing(true);
                    }
                }
            }
        }
        this.isRedTurn = isRedTurn;
    }
}

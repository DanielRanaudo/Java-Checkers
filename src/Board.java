import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Board extends JPanel {
    private int tileSize;
    private final int width;
    private final int height;
    Piece[][] board;
    boolean isRedTurn = true;
    boolean gameOver = false;
    Piece selectedPiece = null;
    ArrayList<Point> validMoves = new ArrayList<>();
    private String filepath = "file.txt";
    private ArrayList<BoardHistory> history = new ArrayList<>();

    public Board (int tileSize, int width, int height) {
        this.tileSize = tileSize;
        this.board = new Piece[height][width];
        this.width = width;
        this.height = height;
        load();
    }

    //draws the entire board, if it's a new game draws the pieces in their needed places.
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        boolean likeFirstRow = true;
        int currentRow = 0;
        int currentCol = 0;
        for (int i = 0; i < width * height; i ++) {
            //checks if we are in a new row
            if (i % 8 == 0 && i != 0) {
                likeFirstRow = !likeFirstRow;
                currentRow++;
                currentCol = 0;
            }
            if (likeFirstRow) {
                if (i % 2 == 0) {
                    g.setColor(Color.BLACK);
                    g.fillRect(currentCol * tileSize, currentRow * tileSize, tileSize, tileSize);

                } else {
                    g.setColor(Color.RED);
                    g.fillRect(currentCol * tileSize, currentRow * tileSize, tileSize, tileSize);
                }

            } else {
                if (i % 2 != 0) {
                    g.setColor(Color.BLACK);
                    g.fillRect(currentCol * tileSize, currentRow * tileSize, tileSize, tileSize);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(currentCol * tileSize, currentRow * tileSize, tileSize, tileSize);
                }
            }
            currentCol++;
        }
        //draw the pieces
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (board[row][col] != null) {
                    board[row][col].drawPiece(g, tileSize, col, row);
                }
            }
        }
    }

    public void newBoard() {
        board = new Piece[height][width];
        for (int row = 0; row < height; row ++) {
            for (int col = 0; col < width; col ++) {
                if ((row + col) % 2 == 0) {
                    if (row < 3) {
                        board[row][col] = new Piece(true, col, row);
                    } else if (row > 4) {
                        board[row][col] = new Piece(false, col, row);
                    }
                }
            }
        }
        isRedTurn = true;
        gameOver = false;
        selectedPiece = null;
        validMoves.clear();
        history.clear();
        history.add(new BoardHistory(board, isRedTurn));
        repaint();
    }

    //returns a Map with the coordinates and its piece
    public Map<Point, Piece> pieceLocations() {
        Map<Point, Piece> locations = new HashMap<>();
        for (int row = 0; row < height; row ++) {
            for (int col = 0; col < width; col++) {
                if (board[row][col] != null) {
                    Piece p = board[row][col];
                    Point coords = new Point(col, row);
                    locations.put(coords, p);
                }
            }
        }
        return locations;
    }

    public ArrayList<Point> getValidMoves(Piece p) {
        ArrayList<Point> moves = new ArrayList<>();
        if (p == null) {
            return moves;
        }

        int row = p.getRow();
        int col = p.getCol();
        boolean isKing = p.isKing();
        boolean isRed = p.isRed();
        int dir = 0;
        if (isRed) {
            dir = 1;
        } else {
            dir = -1;
        }

        //we want to make sure that jumps are the only available ones, so check for jumps and return if as only
        //possible move if there is
        ArrayList<Point> validJumps = new ArrayList<>();
        checkJump(validJumps, p, row + (2 * dir), col - 2);
        checkJump(validJumps, p, row + (2 * dir), col + 2);
        if (isKing) {
            checkJump(validJumps, p, row - (2 * dir), col - 2);
            checkJump(validJumps, p, row - (2 * dir), col + 2);
        }
        if (!validJumps.isEmpty()) {
            return validJumps;
        }

        //moves down or up a row depending, checks for left and right
        checkDiagonal(moves, row + dir, col -1);
        checkDiagonal(moves, row + dir, col + 1);

        if (isKing) {
            checkDiagonal(moves, row - dir, col - 1);
            checkDiagonal(moves, row - dir, col + 1);
        }
        return moves;
    }

    public void checkDiagonal(ArrayList<Point> moves, int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width && board[row][col] == null && (row + col) % 2 == 0) {
            moves.add(new Point(col, row));
        }
    }

    public boolean checkJump(ArrayList<Point> moves, Piece p, int row, int col) {
        int currRow = p.getRow();
        int currCol = p.getCol();
        //out of bounds
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        }
        //piece where it wants to jump
        if (board[row][col] != null) {
            return false;
        }

        //if theres a piece between where the checked piece wants to land then its possible
        int jumpedRow = (currRow + row) / 2;
        int jumpedCol = (currCol + col) / 2;
        //checks if jumped row exists
        if (jumpedRow >= 0 && jumpedRow < height && jumpedCol >= 0 && jumpedCol < width) {
            Piece jumpedPiece = board[jumpedRow][jumpedCol];
            if (jumpedPiece != null && jumpedPiece.isRed() != p.isRed()) {
                moves.add(new Point(col, row));
                return true;
            }
        }
        return false;
    }

    public boolean movePiece(int currRow, int currCol, int newRow, int newCol) {
        if (currRow < 0 || currRow >= height || currCol < 0 || currCol >= width || newRow < 0 ||
           newRow >= height || newCol < 0 || newCol >= width) {
            return false;
        }

        Piece p = board[currRow][currCol];
        if (p == null) {
            return false;
        }
        ArrayList<Point> moves = getValidMoves(p);
        Point newPlace = new Point(newCol, newRow);
        if (moves.contains(newPlace)) {
            if (Math.abs(newRow - currRow) == 2) {
                //find the eaten piece
                int ateRow = (newRow + currRow) / 2;
                int ateCol = (newCol + currCol) / 2;
                board[ateRow][ateCol] = null;
            }
            p.movePiece(newRow, newCol);
            board[currRow][currCol] = null;
            board[newRow][newCol] = p;

            if (!p.isKing() && ((p.isRed() && newRow == height - 1) || (!p.isRed() && newRow == 0))) {
                p.setKing(true);
            }
            return true;
        }
        return false;
    }

    public boolean canChainJump(Piece p) {
        ArrayList<Point> moves = getValidMoves(p);
        for (Point point : moves) {
            if (Math.abs(point.x - p.getCol()) == 2) {
                return true;
            }
        }
        return false;
    }

    public int getBoardSize() {
        return width;
    }

    public int checkWinner() {
        boolean noRed = true;
        boolean noWhite = true;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    Piece p = board[i][j];
                    if (p.isRed()) {
                        noRed = false;
                    } else {
                        noWhite = false;
                    }
                }
            }
        }
        if (noRed) return 2;
        if (noWhite) return 1;
        return 0;
    }

    public Piece getPiece(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return null;
        }
        return board[row][col];
    }

    public ArrayList<Piece> anyJumpsOnBoard () {
        Map<Point, Piece> locations = pieceLocations();
        ArrayList<Piece> piecesWithJumps = new ArrayList<>();
        for (Point p : locations.keySet()) {
            Piece piece = locations.get(p);
            if (piece.isRed() == isRedTurn) {
                ArrayList<Point> moves = getValidMoves(piece);
                for (Point move : moves) {
                    if (Math.abs (move.x - piece.getCol()) == 2) {
                        piecesWithJumps.add(piece);
                        break;
                    }
                }
            }
        }
        return piecesWithJumps;
    }

    public void filterJumps() {
        ArrayList<Point> jumps = new ArrayList<>();
        int ogCol = selectedPiece.getCol();
        for (Point p : validMoves) {
            if (Math.abs(p.x - ogCol) == 2) {
                jumps.add(p);
            }
        }
        validMoves = jumps;
    }

    public boolean isRedTurn() {
        return isRedTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void handleMoves(int row, int col) {
        if (gameOver) {
            return;
        }

        Point newP = new Point(col, row);
        ArrayList<Piece> piecesWithJumps = anyJumpsOnBoard();

        //first click
        if (selectedPiece == null || validMoves.isEmpty()) {
            selectedPiece = getPiece(row, col);
            if (selectedPiece != null && selectedPiece.isRed() == isRedTurn) {
                validMoves = getValidMoves(selectedPiece);
                if (!anyJumpsOnBoard().isEmpty()) {
                    filterJumps();
                }
            } else {
                selectedPiece = null;
                validMoves.clear();
            }
            return;
        }

        //catches the second click
        if (validMoves.contains(newP)) {
            executeMoves(row, col);
            //save();
            history.add(new BoardHistory(board, isRedTurn));
        } else {
            //if click is invalid reset
            selectedPiece = null;
            validMoves.clear();

        }

    }

    public void executeMoves(int row, int col) {
        Point newP = new Point(col, row);
        if (!validMoves.contains(newP)) return;

        boolean isJump = Math.abs(newP.x - selectedPiece.getCol()) == 2;
        boolean moved = movePiece(selectedPiece.getRow(), selectedPiece.getCol(), row, col);

        if (moved) {
            if (isJump && canChainJump(getPiece(row, col))) {
                selectedPiece = getPiece(row, col);
                validMoves = getValidMoves(selectedPiece);
                filterJumps();
            } else {
                isRedTurn = !isRedTurn;
                selectedPiece = null;
                validMoves.clear();
            }
            gameOver = checkWinner() != 0;
        }
    }

    public boolean undo(){
        if (history.size() <= 1) {
            return false;
        }

        history.remove(history.size() - 1);

        BoardHistory previous = history.get(history.size() - 1);



        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (previous.board[i][j] != null) {
                    Piece p = previous.board[i][j];
                    board[i][j] = new Piece(p.isRed(), p.getCol(), p.getRow());
                    if (p.isKing()) {
                        this.board[i][j].setKing(true);
                    }
                } else {
                    this.board[i][j] = null;
                }
            }
        }
        this.isRedTurn = previous.isRedTurn;
        this.selectedPiece = null;
        this.validMoves.clear();
        repaint();
        return true;
    }

    public boolean tryLoad() {
        File file = new File(filepath);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filepath));){
            String currentLine = br.readLine();
            if (currentLine == null) {
                return false;
            }
            board = new Piece[height][width];
            selectedPiece = null;
            validMoves.clear();
            isRedTurn = currentLine.equals("Red");
            while ((currentLine = br.readLine()) != null) {
                String[] tokens = currentLine.split(",");
                if (tokens.length == 4) {
                    int row = Integer.parseInt(tokens[0]);
                    int col = Integer.parseInt(tokens[1]);
                    boolean isKing = tokens[2].equals("King");
                    boolean isRed = tokens[3].equals("Red");

                    Piece p = new Piece(isRed, col, row);
                    if (isKing) {
                        p.setKing(true);
                    }
                    board[row][col] = p;
                }
            }
            return true;
        }  catch (FileNotFoundException e) {
            newBoard();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void load() {
        if (!tryLoad()) {
            newBoard();
        }
        repaint();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))){
            String turn = isRedTurn ? "Red" : "White";
            bw.write(turn);
            bw.newLine();

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (board[row][col] != null) {
                        Piece p = board[row][col];
                        String color = p.isRed() ? "Red" : "White";
                        String king = p.isKing() ? "King" : "Normal";
                        bw.write(row + "," + col + "," + king + "," + color);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

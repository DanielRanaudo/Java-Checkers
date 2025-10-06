import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        Runnable game = new CheckersGame();
        SwingUtilities.invokeLater(game);
    }
}

import java.awt.*;
import javax.swing.*;

public class CheckersGame implements Runnable {
    public void run() {

        final JFrame frame = new JFrame("Checkers");
        frame.setLocation(300,300);

        //status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Red's Turn");
        status_panel.add(status);

        //Game board
        final Game board = new Game(status);
        frame.add(board, BorderLayout.CENTER);

        //Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> board.reset());
        control_panel.add(reset);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            board.save();
            JOptionPane.showMessageDialog(frame, "Game Saved");
        });

        JButton load = new JButton("Load");
        load.addActionListener(e -> {
            board.load();
            JOptionPane.showMessageDialog(frame, "Game Loaded");
        });
        control_panel.add(load);
        control_panel.add(save);

        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {
            board.undo();
        });
        control_panel.add(undo);

        JButton instructions = new JButton("Rules");
        instructions.addActionListener(e -> {
            String instructionsText = """
                    Checkers Rules\s
                    
                    Red Moves First\s
            
                    You can only move diagonally and towards the side of the other color. if
                    the piece is a king, then you can move towards your side as well\s
                    
                    To become a king, your piece must reach the end of the other side of the board \s
                    
                    To eat a piece, it must be in front of you and there must be no pieces behind it \s
                    
                    If you eat all the opponents pieces, you win!""";
            JOptionPane.showMessageDialog(frame, instructionsText);
        });
        control_panel.add(instructions);
    }
}
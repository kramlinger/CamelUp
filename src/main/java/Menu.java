import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.List;
import java.util.ArrayList;

public class Menu {

    private static final int PLAYER_COUNT = 4;
    private static final String[] DEFAULT_NAMES = {
            "Alice", "Bob", "Charlie", "Diana", "Eve"
    };

    public void createMenu() {
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(20, 20)); // extra padding

        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        playerPanel.setLayout(new GridLayout(PLAYER_COUNT, 1, 10, 10)); // bigger padding between rows

        JRadioButton[] toggles = new JRadioButton[PLAYER_COUNT];
        JTextField[] nameFields = new JTextField[PLAYER_COUNT];
        JComboBox<String>[] typeDropdowns = new JComboBox[PLAYER_COUNT];

        for (int i = 0; i < PLAYER_COUNT; i++) {

            JPanel row = new JPanel();
            row.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

            // 1a - Toggle
            toggles[i] = new JRadioButton();
            boolean enabled = i < 4; // first 4 toggled on
            toggles[i].setSelected(enabled);

            // 1b - Name Field
            nameFields[i] = new JTextField(10);
            nameFields[i].setText(DEFAULT_NAMES[i]);
            nameFields[i].setEnabled(enabled);

            // 1c - Dropdown
            if (i == 0) {
                typeDropdowns[i] = new JComboBox<>(new String[]{"Human"});
                typeDropdowns[i].setEnabled(false);
            } else {
                typeDropdowns[i] = new JComboBox<>(new String[]{"Human", "Random", "Greedy", "Neural Network"});

                int idx = i;
                /*
                toggles[i].addActionListener(e -> {
                    boolean isSelected = toggles[idx].isSelected();
                    nameFields[idx].setEnabled(isSelected);
                    typeDropdowns[idx].setEnabled(isSelected);
                });
                 */
                typeDropdowns[i].setEnabled(enabled);
            }


            row.add(toggles[i]);
            row.add(new JLabel("Name:"));
            row.add(nameFields[i]);
            row.add(new JLabel("Player:"));
            row.add(typeDropdowns[i]);

            playerPanel.add(row);
        }

        // Start Button
        JButton startButton = new JButton("Start Race");
        startButton.addActionListener(e -> {
            List<Player> activePlayers = new ArrayList<>();
            int playerId = 0;

            for (int i = 0; i < PLAYER_COUNT; i++) {
                if (toggles[i].isSelected()) {
                    String name = nameFields[i].getText();
                    String type = (String) typeDropdowns[i].getSelectedItem();

                    Player player;
                    switch (type) {
                        case "Human" -> player = new HumanPlayer(playerId, name);
                        case "Random" -> player = new RandomPlayer(playerId, name);
                        case "Greedy" -> player = new GreedyPlayer(playerId, name);
                        case "Neural Network" -> player = new NNPlayer(playerId, name);
                        default -> throw new IllegalStateException("Unexpected player type: " + type);
                    }

                    activePlayers.add(player);
                    playerId++;
                }
            }

            Player[] players = activePlayers.toArray(new Player[0]);

            // Create Game and Board
            Game game = new Game(players);
            Board board = new Board(game);

            game.start();
            board.showBoard();

            // Close the menu window
            frame.dispose();
        });


        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(startButton);

        frame.add(playerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu().createMenu());
    }
}

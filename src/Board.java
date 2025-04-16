import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Board {
    private JPanel gridPanel;
    private JLabel turnLabel;
    private final DefaultTableModel tableModel;
    private final Game game;  // Reference to the Game class to get camel positions
    private static final int ROWS = 5;
    private static final int COLS = 18; // Set to 18 columns as requested

    private JPanel buttonPanel;
    private boolean raceListenerRegistered = false;

    private JFrame gameFrame;


    public Board(Game game) {
        this.game = game; // Store the game object
        this.tableModel = new DefaultTableModel(new String[] {
                "Player",
                "Money",
                "Lap Bets"
        }, 0); // Initialize table model

        raceEndHandler();
    }

    public void showBoard() {
        gameFrame = new JFrame("Camel Race Board");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(830, 600);
        gameFrame.setLocationRelativeTo(null);

        // Main panel with GridBagLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // 1. New Game Button (Top-left)
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            game.reset();

            fillGrid();
            updateTurnLabel();
            updateTable();
            enableButtonPanel();
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        mainPanel.add(newGameButton, gbc);

        // 2. "Public Information" Header
        JLabel publicInfoLabel = new JLabel("Public Information");
        publicInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        mainPanel.add(publicInfoLabel, gbc);

        // 3. JTable for Public Information
        Player[] players = game.getPlayers(); // Access the players from the Game object

        // Populate the table with player data
        for (Player player : players) {
            tableModel.addRow(new Object[]{
                    player.getPlayerID() + 1,
                    player.getMoney(),
                    player.lapBets()
            });
        }

        // Create JTable with table model
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        tableScrollPane.setPreferredSize(new Dimension(700, 150));

        // Add table to mainPanel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        mainPanel.add(tableScrollPane, gbc);

        // 4. "Race" Header
        JLabel raceLabel = new JLabel("Race");
        raceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        mainPanel.add(raceLabel, gbc);

        // 5. JPanel Grid for Race
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.4; // Takes a larger space
        gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        gridPanel.setPreferredSize(new Dimension(600, 200));
        fillGrid();
        mainPanel.add(gridPanel, gbc);

        // 6. Dynamic JLabel (Status/Message)
        turnLabel = new JLabel("It's Player " + (game.playerTurn + 1) + "'s turn");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(turnLabel, gbc);

        // 7. Buttons at the bottom
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Bet Lap
        List<Camel> camels = game.freeLapCardCamels();
        DefaultComboBoxModel<Camel> camelModel = new DefaultComboBoxModel<>(camels.toArray(new Camel[0]));
        JComboBox<Camel> camelDropdown = new JComboBox<>(camelModel);

        JButton betLegButton = new JButton("Bet on first");
        betLegButton.addActionListener(e -> {
            Camel selectedCamel = (Camel) camelDropdown.getSelectedItem();
            game.takeLapCard(selectedCamel);

            updateTurnLabel();
            updateTable();

            updateCamelDropdown(game.freeLapCardCamels(), camelModel, camelDropdown);
        });

        // Bet Race

        List<Camel> playerCamelCards = game.getRaceCards().stream()
                .map(RaceCards::camel) // extract the camel from each RaceCards
                .toList();
        DefaultComboBoxModel<Camel> raceCamelModel = new DefaultComboBoxModel<>(playerCamelCards.toArray(new Camel[0]));
        JComboBox<Camel> raceCamelDropdown = new JComboBox<>(raceCamelModel);


        JButton betWinner = new JButton("Bet on first");
        JButton betLoser = new JButton("Bet on last");

        betWinner.addActionListener(e -> {
            Camel selectedCamel = (Camel) raceCamelDropdown.getSelectedItem();
            game.takeWinnerRaceCard(selectedCamel);

            fillGrid();
            updateTurnLabel();
            updateTable();

            List<Camel> camelCards = game.getRaceCards().stream()
                    .map(RaceCards::camel) // extract the camel from each RaceCards
                    .toList();
            updateCamelDropdown(camelCards, raceCamelModel, raceCamelDropdown);
            updateButton(betWinner, camelCards);
            updateButton(betLoser, camelCards);
        });
        betLoser.addActionListener(e -> {
            Camel selectedCamel = (Camel) raceCamelDropdown.getSelectedItem();
            game.takeLoserRaceCard(selectedCamel);

            fillGrid();
            updateTurnLabel();
            updateTable();

            List<Camel> camelCards = game.getRaceCards().stream()
                    .map(RaceCards::camel) // extract the camel from each RaceCards
                    .toList();
            updateCamelDropdown(camelCards, raceCamelModel, raceCamelDropdown);
            updateButton(betWinner, camelCards);
            updateButton(betLoser, camelCards);
        });


        // Advance
        JButton advanceCamelButton = new JButton("Move a camel");
        advanceCamelButton.addActionListener(e -> {
            game.advanceCamel();

            fillGrid();
            updateTurnLabel();
            updateTable();
            updateCamelDropdown(game.getRaceCards().stream()
                    .map(RaceCards::camel) // extract the camel from each RaceCards
                    .toList(), raceCamelModel, raceCamelDropdown);
        });

        // Organize display
        buttonPanel.add(advanceCamelButton);

        JPanel comboGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 5px horizontal gap
        comboGroup.add(betLegButton);
        comboGroup.add(camelDropdown);

        // Then add to your main panel
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0))); // 30px spacing before next button
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(new JLabel("Leg:"));
        buttonPanel.add(comboGroup);

        JPanel raceComboGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 5px horizontal gap
        raceComboGroup.add(betWinner);
        raceComboGroup.add(betLoser);
        raceComboGroup.add(raceCamelDropdown);

        // Then add to your main panel
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0))); // 30px spacing before next button
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(new JLabel("Race:"));
        buttonPanel.add(raceComboGroup);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        gameFrame.add(mainPanel);
        gameFrame.setVisible(true);

    }

    private void updateCamelDropdown(List<Camel> newCamels,
                                     DefaultComboBoxModel<Camel> model,
                                     JComboBox<Camel> camelDropdown) {
        //Camel selectedCamel = (Camel) camelDropdown.getSelectedItem();
        model.removeAllElements();
        for (Camel c : newCamels) {
            model.addElement(c);
        }

        camelDropdown.setSelectedItem(game.leadingCamel()); // reselect if it's still present

        // account for empty lists
        if (camelDropdown.isEnabled()) {
            boolean isEmpty = newCamels.isEmpty();
            camelDropdown.setEnabled(!isEmpty);
        }
    }

    private void updateButton(JButton button, List<Camel>  newCamels) {
        // account for empty lists
        boolean isEmpty = newCamels.isEmpty();
        button.setEnabled(!isEmpty);
    }



    // Update the turn label dynamically
    private void updateTurnLabel() {
        turnLabel.setText("It's Player " + (game.playerTurn + 1) + "'s turn");
    }

    // Method to fill the grid based on camel positions (Now using camel names)
    private void fillGrid() {
        gridPanel.removeAll(); // Clear the previous grid content

        // Loop through each camel and set the corresponding grid cell with camel name
        for (int i = 0; i < ROWS * COLS; i++) {
            JPanel gridCell = new JPanel();
            // Set the border of the grid cells
            if (i % COLS == 0) {
                // Add thick vertical border between the first and second column
                gridCell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLACK));
            } else if (i % COLS == COLS - 2) {
                // Add thick vertical border between the second-to-last and last column
                gridCell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLACK));
            } else {
                // Light vertical lines between all other columns
                gridCell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
            }

            // Add the grid cell to the panel
            gridPanel.add(gridCell);
        }

        // Loop through each camel and display its name in the corresponding grid cell
        for (Camel camel : game.getCamels()) {
            int camelX = camel.getX();
            int camelY = camel.getY();

            // Calculate the index of the camel's position in the grid
            int index = camelY * COLS + camelX;

            // Retrieve the corresponding grid cell
            JPanel gridCell = (JPanel) gridPanel.getComponent(index);
            gridCell.setBackground(getCamelColor(camel.getName()));
            gridCell.setLayout(new GridBagLayout()); // Ensures perfect centering

            if (camel.getHasMoved()) {
                // Create label with HTML text
                JLabel cellLabel = new JLabel("<html><div style='text-align:center;'>moved</div></html>");
                cellLabel.setFont(new Font("Arial", Font.PLAIN, 8));
                cellLabel.setHorizontalAlignment(SwingConstants.CENTER); // Horizontal centering

                // Add label to gridCell with centered constraints
                gridCell.add(cellLabel, new GridBagConstraints());
            }

        }

        // Force grid to refresh after updates
        gridPanel.revalidate();
        gridPanel.repaint(); // Ensure the changes are rendered
    }

    // Method to get the color associated with each camel name
    private Color getCamelColor(String camelName) {
        return switch (camelName) {
            case "Blue" -> Color.BLUE;
            case "Yellow" -> Color.YELLOW;
            case "Green" -> Color.GREEN;
            case "Orange" -> Color.ORANGE;
            case "White" -> Color.WHITE;
            default -> Color.GRAY; // Default color if something goes wrong
        };
    }

    // Method to advance the camel by 1 column (update both the Board and Game)

    // Method to get all camels in a given column
    private List<Camel> getCamelsInColumn(int col) {
        List<Camel> camelsInColumn = new ArrayList<>();
        for (Camel camel : game.getCamels()) {
            if (camel.getX() == col) {
                camelsInColumn.add(camel);
            }
        }
        return camelsInColumn;
    }

    private void updateTable() {
        Player[] players = game.getPlayers();

        for (int i = 0; i < players.length; i++) {
            tableModel.setValueAt(players[i].getMoney(), i, 1); // Update the Money column
            tableModel.setValueAt(players[i].lapBets(), i, 2); // Update the Bets column
        }
    }

    private void raceEndHandler() {

        if (raceListenerRegistered) return;

        game.addPropertyChangeListener(evt -> {
            if ("raceFinished".equals(evt.getPropertyName()) && (Boolean) evt.getNewValue()) {
                disableButtonPanel();
                showResultsWindow();
            }
        });

        raceListenerRegistered = true;
    }

    private void disableButtonPanel() {
        disableComponentTree(buttonPanel);
    }

    private void enableButtonPanel() {
        enableComponentTree(buttonPanel);
    }

    private void showResultsWindow() {
        // Create the new results window
        JFrame resultFrame = new JFrame("Race Results");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());
        resultFrame.setSize(300, 200);
        resultFrame.setResizable(false);

        // Match look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(resultFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create result label
        JLabel resultsLabel = new JLabel("<html><div style='text-align: center;'>" + game.getResults().replace("\n", "<br>") + "</div></html>");
        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultsLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // OK button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> resultFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        // Add components to frame
        resultFrame.add(resultsLabel, BorderLayout.CENTER);
        resultFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Center relative to gameFrame
        resultFrame.setLocationRelativeTo(gameFrame);
        resultFrame.setVisible(true);
    }

    private void disableComponentTree(Component comp) {
        comp.setEnabled(false);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                disableComponentTree(child);
            }
        }
    }
    private void enableComponentTree(Component comp) {
        comp.setEnabled(true);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                enableComponentTree(child);
            }
        }
    }
}


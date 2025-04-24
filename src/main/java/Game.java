import java.util.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.stream.Collectors;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Game {
    private final Camel[] camels;
    private final Player[] players;
    private static final int ROWS = 5;
    private static final int COLS = 18;
    public int playerTurn = 0;
    private boolean raceFinished = false;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final GameState gameState; // numeric summary of the current game state
    private final List<LapCards> freeLapCards;
    private final List<RaceCards> winnerCamelCards = new ArrayList<>();
    private final List<RaceCards> loserCamelCards = new ArrayList<>();

    private boolean headless = false;
    private String uniqueFileName;
    private int currentPlayerAction;

    public Game(Player[] players) {
        this.players = players;

        camels = new Camel[5];

        // Initialize the camels with their names and initial x-coordinate
        camels[0] = new Camel("Blue", 0, -1); // Temporary y-coordinate, will be shuffled
        camels[1] = new Camel("Yellow", 0, -1);
        camels[2] = new Camel("Green", 0, -1);
        camels[3] = new Camel("Orange", 0, -1);
        camels[4] = new Camel("White", 0, -1);

        // Shuffle the y-coordinates for the camels
        shuffleCamelPositions();

        // Set players
        for (Player p : players) {
            p.addRaceCards(camels); // Presumably bad practice, but it works...
        }

        // Set LapCards
        freeLapCards = new ArrayList<>();
        for (Camel c : camels) {
            freeLapCards.add(new LapCards(c, 5));
            freeLapCards.add(new LapCards(c, 3));
            freeLapCards.add(new LapCards(c, 2));
        }

        // Set up GameState
        Player currentPlayer = players[playerTurn];
        gameState = new GameState(this, new int[24 + players.length * 2]);
        gameState.updateGameState();

    }

    private void setHeadless (boolean headless) { this.headless = headless; }
    public boolean getHeadless () { return headless; }

    // Start game
    public void start() {
        boolean usesNeuralNet = false;

        // First, we need to populate the AI Players game fields.
        for (Player player : players) {
            if (player instanceof AIPlayer ai) {
                ai.setGame(this);
            }
            if (player instanceof NNPlayer) {
                usesNeuralNet = true;
            }
        }

        // Check if neural net is used
        if (usesNeuralNet) {
            PythonServerManager.start();
            PythonServerManager.waitForServer("http://localhost:5050/ping");
        }

        // check if this is a headless game
        boolean headless = true;
        for (Player p : players) {
            if (!(p instanceof AIPlayer)) {
                headless = false;
            }
        }
        this.headless = headless;

        if (this.headless) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String uniqueFileName = "data_" + timeStamp + ".txt";

            this.uniqueFileName = uniqueFileName;

            Writer.beginTxt(this);
        }


        handleTurn(); // call this manually or from UI
    }

    public void handleTurn() {

        if (!raceFinished) {

            Player currentPlayer = players[playerTurn];

            if (!(currentPlayer instanceof HumanPlayer)) {
                takeTurn(currentPlayer);
            }
        }
    }

    // Getters
    public Camel[] getCamels() {
        return camels;
    }

    public String getUniqueFileName() {return this.uniqueFileName; }

    public GameState getGameState() {return this.gameState; }
    public int getCols() { return COLS; }
    public List<RaceCards> getWinnerCamelCards() { return winnerCamelCards; }
    public List<RaceCards> getLoserCamelCards() { return loserCamelCards; }
    public List<LapCards> getFreeLapCards() { return freeLapCards; }
    public Player getCurrentPlayer() { return players[playerTurn]; }

    public int getNumberPlayers() {return players.length; }


    // Used methods
    public List<Camel> freeLapCards() {

        List<Camel> filteredCamels = new ArrayList<>();

        for (Camel c : camels) {
            boolean camelPresent = freeLapCards.stream()
                    .anyMatch(card -> card.camel().equals(c));
            if (camelPresent) {
                filteredCamels.add(c);
            }

        }
        return filteredCamels;
    }

    public List<Camel> freeLapCardCamels() {

        List<Camel> freeLapCardCamels = new ArrayList<>();
        for (Camel c : camels) {
            boolean camelPresent = freeLapCards.stream()
                    .anyMatch(card -> card.camel().equals(c));

            if (camelPresent) {
                freeLapCardCamels.add(c);
            }
        }

        return freeLapCardCamels;
    }

    public void takeWinnerRaceCard(Camel c) {
        Player currentPlayer = players[playerTurn];
        winnerCamelCards.add(currentPlayer.setRaceCard(c));

        turnFinished();
    }

    public void takeLoserRaceCard(Camel c) {
        Player currentPlayer = players[playerTurn];
        loserCamelCards.add(currentPlayer.setRaceCard(c));
        turnFinished();
    }

    public LapCards getHighestValueCard(Camel c) {

        // Filter cards that have the same Camel as c
        List<LapCards> filteredCards = freeLapCards.stream()
                .filter(card -> card.camel().equals(c))  // Keep cards that match the camel
                .toList();  // Collect into a new list

        LapCards highestValueCard = new LapCards(c, -99);

        // Find the LapCard with the largest value among the filtered cards
        if (!filteredCards.isEmpty()) {
            highestValueCard = filteredCards.get(0);  // Start with the first card

            for (LapCards card : filteredCards) {
                if (card.value() > highestValueCard.value()) {
                    highestValueCard = card;  // Update if we find a card with a larger value
                }
            }
        }

        return highestValueCard;

    }

    public void takeLapCard(Camel c) {

        LapCards highestValueCard = getHighestValueCard(c);

        // Remove the card from the list after using it
        freeLapCards.remove(highestValueCard);

        // Add it to players inventory
        Player currentPlayer = players[playerTurn];
        currentPlayer.addLapCard(highestValueCard);

        turnFinished();
    }

    public void advanceCamel() {

        // update Player
        Player currentPlayer = players[playerTurn];
        currentPlayer.addPyramidCard();

        // select Camel
        List<Camel> unMovedCamels = new ArrayList<>();

        // Collect camels that have hasMoved == true
        for (Camel camel : camels) {
            if (!camel.getHasMoved()) {
                unMovedCamels.add(camel);
            }
        }
        Random random = new Random();

        // System.out.printf("CamelIndex %d%n", unMovedCamels.size());

        if (unMovedCamels.isEmpty()) {
            System.out.println("No camels available to move.");
            return; // Exit the method early -- should not happen
        }

        Camel camel = unMovedCamels.get(random.nextInt(unMovedCamels.size()));
        //System.out.printf("Camel %s%n", camel.getName());

        // select stepSize
        int stepSize = random.nextInt(3) + 1;
        if (camel.getX() + stepSize > COLS - 1) {
            stepSize = COLS - camel.getX() - 1;
        }

        // System.out.printf("stepSize %d%n", stepSize);

        if (camel.getX() <= COLS - 1) {

            // Now, advance selected camel
            camel.advanceCamel(camels, stepSize);

        } // do not advance if already at end.

        // Finish turn
        turnFinished();
    }

    /*
    private boolean checkIfTileIsFree(int x, int y) {
        boolean isFree = y <= ROWS - 1; // tile not free because it is the end of board
        for (Camel c : camels) {
            if (c.getX() == x && c.getY() == y) {
                isFree = false;
                break;
            }
        }
        return isFree;
    }
    */


    private void shuffleCamelPositions() {
        // Shuffle the positions for the y-coordinate
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);

        // Assign shuffled positions to the camels
        for (int i = 0; i < camels.length; i++) {
            camels[i].setPosition(0, positions.get(i)); // Place at column 0 with a shuffled y
        }
    }

    public void reset() {

        this.raceFinished = false;

        // Reset camels to their initial positions, but shuffle the y-coordinates
        for (Camel c : camels) {
            c.setPosition(0, -1);
            c.resetHasMoved();
        }
        shuffleCamelPositions();

        // Reset Players
        playerTurn = 0;
        for (Player p : players) {
            p.resetMoney();
            this.freeLapCards.addAll(p.returnPlayerLapCards());
            for (RaceCards card : winnerCamelCards) {
                if (card.player() == p) {
                    p.returnRaceCards(card);
                }
            }
            for (RaceCards card : loserCamelCards) {
                if (card.player() == p) {
                    p.returnRaceCards(card);
                }
            }
        }

        winnerCamelCards.clear();
        loserCamelCards.clear();


    }

    /*
    public void printPositions() {
        for (Camel c : camels) {
            System.out.printf("Camel: %s, X: %d, Y: %d%n", c.getName(), c.getX(), c.getY());
        }
    }
    */


    public static boolean hasCamelFinished(Camel c) { return c.getX() >= COLS - 1; }

    private void turnFinished() {

        // Check if Lap is finished
        // List<Camel> unMovedCamels = new ArrayList<>();

        boolean areThereCamelsToMove = false;
        for (Camel camel : camels) {
            areThereCamelsToMove = areThereCamelsToMove || !camel.getHasMoved();
        }

        if (!areThereCamelsToMove) {
            // reset camels
            for (Camel camel : camels) {
                camel.resetHasMoved();
            }
            lapFinished();
        }

        // Check if race is finished. Can only occur after turn is finished
        for (Camel c : camels) {
            if (hasCamelFinished(c)) { // A camel has crossed the finishing line
                // System.out.printf("Race is finished! ");
                finishRace();
            }
        }
        /*
        System.out.print("Money: ");
        for (Player p : players) {
            System.out.printf("%d,", p.getMoney());
        }
        System.out.printf("%n");
         */


        // It's next Player's turn
        playerTurn = (playerTurn + 1) % players.length;
        handleTurn();

    }

    public String getResults() {
        String camelWinner = "";
        for (Camel c : camels) {
            if (c.camelRanking(camels) == 1) {
                camelWinner = "The " + c.getName().toLowerCase() + " camel won the race. ";
            }
        }

        String playerWinner = "";
        int winnerMoney = Integer.MIN_VALUE;
        for (Player p : players) {
            if (p.getMoney() > winnerMoney) {
                winnerMoney = p.getMoney();
            }
        }
        // System.out.printf("winnerMoney %d%n", winnerMoney);

        ArrayList<Player> winners = new ArrayList<>();
        for (Player p : players) {
            if (p.getMoney() == winnerMoney) { winners.add(p); }
        }
        if (winners.size() == 1) {
            playerWinner = winners.get(0).getName() + " won the game! ";
        } else if (winners.size() > 1) {
            playerWinner = winners.stream()
                    .map(player -> player.getName())
                    .collect(Collectors.joining(", "));
            playerWinner = "Players " + playerWinner + " are tied for first place! ";
        }
        return camelWinner + playerWinner;
    }

    private void finishRace() {

        if (!this.raceFinished) {
            // Invokes the end of lap
            lapFinished();

            // Compute race betting results
            Camel winningCamel = null;
            Camel loserCamel = null;
            for ( Camel c : camels ) {
                if (c.camelRanking(camels) == 1) { winningCamel = c; }
                if (c.camelRanking(camels) == camels.length) { loserCamel = c; }
            }
            int[] value = {8, 5, 3, 2, 1}; // Maximum: 5 players, so length of value is five
            int valueCounter = 0;
            for (RaceCards card : winnerCamelCards) {
                if (card.camel() != winningCamel) {
                    card.player().addMoney(-1);
                } else {
                    // System.out.printf("Add price money: %d%n", value[valueCounter]);
                    card.player().addMoney(value[valueCounter]);
                    valueCounter += 1;
                }
            }
            valueCounter = 0;
            for (RaceCards card : loserCamelCards) {
                if (card.camel() != loserCamel) {
                    card.player().addMoney(-1);
                } else {
                    card.player().addMoney(value[valueCounter]);
                    valueCounter += 1;
                }
            }

            // set game state
            this.raceFinished = true;

            System.out.print(getResults());
            System.out.println();

            // Notify listeners if the value has changed
            support.firePropertyChange("raceFinished", false, true);

            // Finish writing the Log
            if (this.headless) {
                Writer.writeResult(this);
            }

        }
    }

    private void lapFinished() {
        //System.out.printf("Lap finished!%n");

        // calculate lap results
        Camel firstCamel = camels[0];
        Camel secondCamel = camels[0];
        for (Camel c : camels) {
            if (c.camelRanking(camels) == 1) {
                firstCamel = c;
            }
            if (c.camelRanking(camels) == 2) {
                secondCamel = c;
            }
        }

        for (Player p : players) {
            p.calculateLapResults(firstCamel, secondCamel);
            this.freeLapCards.addAll(p.returnPlayerLapCards());
        }

        // Print lap results
        /*
        for (Player p : players) {
            System.out.printf("Player/ Money: %s/ %d%n", p.getName(), p.getMoney());
        }
         */
    }

    public Player[] getPlayers() { return players; }

    public Camel leadingCamel() {
        for (Camel c : camels) {
            if (c.camelRanking(camels) == 1) {
                return c;
            }
        }
        return null; // should not happen
    }

    public List<RaceCards> getRaceCards() {
        return players[playerTurn].getRaceCards();
    }

    public boolean isRaceFinished() {
        return raceFinished;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void takeTurn(Player player) {
        player.takeTurn(); // Make move
        support.firePropertyChange("moveMade", null, null); // Notify UI
    }
}

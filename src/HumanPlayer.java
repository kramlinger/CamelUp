
public class HumanPlayer extends Player {

    // private Board board;

    public HumanPlayer(int playerID, String name) {

        super(playerID, name);
        this.playerType = "Human";
    }

    @Override
    public void takeTurn() {
        // No action needed here; UI handles the turn
    }
}
